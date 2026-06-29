package net.sixik.sdmshop.network.server;

import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.sixik.sdmshop.SDMShop;
import net.sixik.sdmshop.api.ShopBase;
import net.sixik.sdmshop.api.ShopEvents;
import net.sixik.sdmshop.config.ShopConfig;
import net.sixik.sdmshop.network.SDMShopNetwork;
import net.sixik.sdmshop.network.sync.SendLimiterS2C;
import net.sixik.sdmshop.server.SDMShopServer;
import net.sixik.sdmshop.shop.BaseShop;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.shop.ShopTab;
import net.sixik.sdmshop.shop.limiter.ShopLimiter;

public class SendBuyEntryC2S extends BaseC2SMessage {
   private final UUID shopId;
   private final UUID entryId;
   private final int requestedCount;

   public SendBuyEntryC2S(UUID shopId, UUID entryId, int count) {
      this.shopId = shopId;
      this.entryId = entryId;
      this.requestedCount = count;
   }

   public SendBuyEntryC2S(FriendlyByteBuf byteBuf) {
      this.shopId = byteBuf.m_130259_();
      this.entryId = byteBuf.m_130259_();
      this.requestedCount = byteBuf.readInt();
   }

   public MessageType getType() {
      return SDMShopNetwork.SHOP_BUY_ENTRY;
   }

   public void write(FriendlyByteBuf friendlyByteBuf) {
      friendlyByteBuf.m_130077_(this.shopId);
      friendlyByteBuf.m_130077_(this.entryId);
      friendlyByteBuf.writeInt(this.requestedCount);
   }

   public void handle(PacketContext context) {
      ServerPlayer player = (ServerPlayer)context.getPlayer();
      SDMShopServer shopServer = SDMShopServer.InstanceOptional().orElse(null);
      if (shopServer != null) {
         BaseShop shop = shopServer.getShop(this.shopId).orElse(null);
         if (shop == null) {
            SDMShop.LOGGER.warn("Player {} tried to buy from non-existent shop {}", player.m_7755_().getString(), this.shopId);
         } else {
            ShopEntry entry = shop.getEntryOptional(this.entryId).orElse(null);
            if (entry == null) {
               SDMShop.LOGGER.warn("Player {} tried to buy but entry {} not found on server !", player.m_7755_().getString(), this.entryId);
            } else {
               ShopTab tab = shop.getTabOptional(entry).orElse(null);
               if (tab != null) {
                  context.queue(() -> this.processPurchase(player, shop, tab, entry));
               } else {
                  SDMShop.LOGGER.warn("Player {} tried to buy but tab {} not found on server !", player.m_7755_().getString(), entry.getTab());
                  if (SDMShop.isDeveloper()) {
                     for (ShopTab shopTab : shop.getTabs()) {
                        SDMShop.LOGGER.debug("Available tab ID: {}", shopTab.getId());
                     }
                  }
               }
            }
         }
      }
   }

   private void processPurchase(ServerPlayer player, BaseShop shop, ShopTab tab, ShopEntry entry) {
      ShopLimiter limiter = SDMShopServer.Instance().getShopLimiter();
      int limitTab = tab.getObjectLimitLeft(player);
      int limitEntry = entry.getObjectLimitLeft(player);
      if ((!tab.isLimiterActive() || limitTab > 0) && (!entry.isLimiterActive() || limitEntry > 0)) {
         int safeCount = this.requestedCount;
         if (tab.isLimiterActive()) {
            safeCount = Math.min(safeCount, limitTab);
         }

         if (entry.isLimiterActive()) {
            safeCount = Math.min(safeCount, limitEntry);
         }

         if (safeCount <= 0) {
            SDMShop.LOGGER.warn("Player {} tried to buy but count <= 0", player.m_7755_().getString());
         } else if (!entry.getEntryType().canExecute(player, entry, safeCount)) {
            SDMShop.LOGGER.warn("Player {} tried to buy but can't execute", player.m_7755_().getString());
         } else {
            boolean success = entry.getType().isSell() ? entry.onSell(player, safeCount) : entry.onBuy(player, safeCount);
            if (!success) {
               SDMShop.LOGGER.error("Transaction failed for player {}", player.m_7755_().getString());
            } else {
               if (entry.getType().isSell()) {
                  ((ShopBase.EntrySellListener)ShopEvents.ENTRY_SELL_EVENT.invoker()).handle(shop, entry, tab, player, safeCount);
               } else {
                  ((ShopBase.EntryBuyListener)ShopEvents.ENTRY_BUY_EVENT.invoker()).handle(shop, entry, tab, player, safeCount);
               }

               this.updateLimiterData(limiter, tab, entry, player, safeCount);
               if ((Boolean)ShopConfig.SEND_NOTIFY.get()) {
                  entry.getEntryType().sendNotifiedMessage(player, entry, safeCount);
               }

               this.broadcastUpdates(limiter, tab, entry, player);
            }
         }
      } else {
         SDMShop.LOGGER.warn("Player {} tried to buy but limit is left", player.m_7755_().getString());
      }
   }

   private void updateLimiterData(ShopLimiter limiter, ShopTab tab, ShopEntry entry, ServerPlayer player, int count) {
      UUID playerId = player.m_36316_().getId();
      if (tab.isLimiterActive()) {
         if (tab.getLimiterType().isPlayer()) {
            limiter.addTabData(tab.getId(), playerId, count);
         } else {
            limiter.addTabData(tab.getId(), count);
         }
      }

      if (entry.isLimiterActive()) {
         if (entry.getLimiterType().isPlayer()) {
            limiter.addOrSetEntryData(entry.getId(), playerId, count);
         } else {
            limiter.addOrSetEntryData(entry.getId(), count);
         }
      }
   }

   private void broadcastUpdates(ShopLimiter limiter, ShopTab tab, ShopEntry entry, ServerPlayer buyer) {
      boolean isGlobalUpdate = tab.isLimiterActive() && tab.getLimiterType().isGlobal() || entry.isLimiterActive() && entry.getLimiterType().isGlobal();
      if (isGlobalUpdate) {
         for (ServerPlayer p : buyer.m_20194_().m_6846_().m_11314_()) {
            new SendLimiterS2C(limiter.serializeClient(p)).sendTo(p);
         }
      } else {
         new SendLimiterS2C(limiter.serializeClient(buyer)).sendTo(buyer);
      }
   }
}
