package net.sixik.sdmshop.network.async;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.networking.NetworkManager.Side;
import io.netty.buffer.Unpooled;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.sixik.sdmshop.SDMShop;

public class AsyncBridge {
   public static final ResourceLocation CHANNEL = new ResourceLocation("sdmshop", "async_bridge");
   private static final Map<Long, CompletableFuture<FriendlyByteBuf>> PENDING = new ConcurrentHashMap<>();
   private static final Map<String, Function<FriendlyByteBuf, FriendlyByteBuf>> HANDLERS = new ConcurrentHashMap<>();
   private static final AtomicLong ID_GEN = new AtomicLong();
   private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();

   public static void initServer() {
      NetworkManager.registerReceiver(Side.C2S, CHANNEL, AsyncBridge::onPacket);
   }

   @OnlyIn(Dist.CLIENT)
   public static void initClient() {
      NetworkManager.registerReceiver(Side.S2C, CHANNEL, AsyncBridge::onPacket);
   }

   public static CompletableFuture<FriendlyByteBuf> askServer(String subject, Function<FriendlyByteBuf, FriendlyByteBuf> writer) {
      return sendInternal(subject, writer, buf -> NetworkManager.sendToServer(CHANNEL, buf));
   }

   public static CompletableFuture<FriendlyByteBuf> askPlayer(ServerPlayer player, String subject, Function<FriendlyByteBuf, FriendlyByteBuf> writer) {
      return sendInternal(subject, writer, buf -> NetworkManager.sendToPlayer(player, CHANNEL, buf));
   }

   public static void registerHandler(String subject, Function<FriendlyByteBuf, FriendlyByteBuf> processor) {
      HANDLERS.put(subject, processor);
   }

   public static void completeExternal(long id, FriendlyByteBuf fullData) {
      CompletableFuture<FriendlyByteBuf> future = PENDING.remove(id);
      if (future != null) {
         future.complete(fullData);
      }
   }

   private static CompletableFuture<FriendlyByteBuf> sendInternal(
      String subject, Function<FriendlyByteBuf, FriendlyByteBuf> writer, Consumer<FriendlyByteBuf> sender
   ) {
      long reqId = ID_GEN.incrementAndGet();
      CompletableFuture<FriendlyByteBuf> future = new CompletableFuture<>();
      PENDING.put(reqId, future);
      SCHEDULER.schedule(() -> {
         if (PENDING.remove(reqId) != null) {
            future.completeExceptionally(new TimeoutException("Packet timed out: " + subject));
         }
      }, 5L, TimeUnit.SECONDS);
      FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
      buf.writeLong(reqId);
      buf.writeBoolean(true);
      buf.m_130070_(subject);
      writer.apply(buf);
      sender.accept(buf);
      return future;
   }

   private static void onPacket(FriendlyByteBuf buf, PacketContext context) {
      long id = buf.readLong();
      boolean isRequest = buf.readBoolean();
      if (isRequest) {
         String subject = buf.m_130277_();
         Function<FriendlyByteBuf, FriendlyByteBuf> handler = HANDLERS.get(subject);
         if (handler != null) {
            FriendlyByteBuf inputCopy = new FriendlyByteBuf(buf.copy());
            context.queue(() -> {
               try {
                  FriendlyByteBuf responsePayload = handler.apply(inputCopy);
                  if (responsePayload != null && responsePayload.readableBytes() > 2000000) {
                     if (context.getPlayer() instanceof ServerPlayer sp) {
                        BlobTransfer.sendToPlayer(sp, id, responsePayload);
                     } else {
                        BlobTransfer.sendToServer(id, responsePayload);
                     }
                  } else {
                     FriendlyByteBuf reply = new FriendlyByteBuf(Unpooled.buffer());
                     reply.writeLong(id);
                     reply.writeBoolean(false);
                     if (responsePayload != null) {
                        reply.writeBytes(responsePayload);
                     }

                     if (context.getPlayer() instanceof ServerPlayer sp) {
                        NetworkManager.sendToPlayer(sp, CHANNEL, reply);
                     } else {
                        NetworkManager.sendToServer(CHANNEL, reply);
                     }
                  }
               } catch (Exception e) {
                  SDMShop.LOGGER.error(e.getMessage(), e);
               } finally {
                  inputCopy.release();
               }
            });
         }
      } else {
         CompletableFuture<FriendlyByteBuf> future = PENDING.remove(id);
         if (future != null) {
            FriendlyByteBuf responseCopy = new FriendlyByteBuf(buf.copy());
            future.complete(responseCopy);
         }
      }
   }
}
