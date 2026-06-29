package net.sixik.sdmshop.network.async;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.networking.NetworkManager.Side;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlobTransfer {
   public static final ResourceLocation CHANNEL = new ResourceLocation("sdmshop", "blob_channel");
   private static final int CHUNK_SIZE = 51200;
   private static final Map<Long, ByteBuf> INCOMING_BUFFERS = new ConcurrentHashMap<>();

   public static void initServer() {
      NetworkManager.registerReceiver(Side.C2S, CHANNEL, BlobTransfer::onPacket);
   }

   @OnlyIn(Dist.CLIENT)
   public static void initClient() {
      NetworkManager.registerReceiver(Side.S2C, CHANNEL, BlobTransfer::onPacket);
   }

   public static void sendToPlayer(ServerPlayer player, long responseId, FriendlyByteBuf hugeData) {
      sendInternal(hugeData, responseId, buf -> NetworkManager.sendToPlayer(player, CHANNEL, buf));
   }

   public static void sendToServer(long requestId, FriendlyByteBuf hugeData) {
      sendInternal(hugeData, requestId, buf -> NetworkManager.sendToServer(CHANNEL, buf));
   }

   private static void sendInternal(ByteBuf data, long id, Consumer<FriendlyByteBuf> sender) {
      int totalSize = data.readableBytes();
      int chunks = (int)Math.ceil(totalSize / 51200.0);

      for (int i = 0; i < chunks; i++) {
         int offset = i * 51200;
         int length = Math.min(51200, totalSize - offset);
         FriendlyByteBuf packet = new FriendlyByteBuf(Unpooled.buffer());
         packet.writeLong(id);
         packet.writeInt(i);
         packet.writeInt(chunks);
         packet.writeBytes(data, offset, length);
         sender.accept(packet);
      }
   }

   private static void onPacket(FriendlyByteBuf buf, PacketContext context) {
      long id = buf.readLong();
      int chunkIndex = buf.readInt();
      int totalChunks = buf.readInt();
      ByteBuf accumulator = INCOMING_BUFFERS.computeIfAbsent(id, k -> Unpooled.buffer());
      accumulator.writeBytes(buf);
      if (chunkIndex == totalChunks - 1) {
         INCOMING_BUFFERS.remove(id);
         FriendlyByteBuf fullData = new FriendlyByteBuf(accumulator);
         context.queue(() -> completeBridgeRequest(id, fullData));
      }
   }

   private static void completeBridgeRequest(long id, FriendlyByteBuf data) {
      AsyncBridge.completeExternal(id, data);
   }
}
