package net.sixik.sdmshop.api;

import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.sixik.sdmshop.network.server.SendBuyEntryC2S;
import net.sixik.sdmshop.old_api.shop.ShopObjectTypes;
import net.sixik.sdmshop.server.SDMShopServer;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.shop.ShopTab;
import net.sixik.sdmshop.shop.limiter.ShopLimiter;

public class ShopApi {
   public static ShopLimiter getLimiter() {
      return SDMShopServer.Instance().getShopLimiter();
   }

   public static void resetLimitAllForGlobal() {
      getLimiter().resetAllDataGlobal();
   }

   public static void resetAllData(UUID id, ShopObjectTypes types) {
      getLimiter().resetAllData(id, types);
   }

   public static void resetLimitAllForPlayer(ServerPlayer player) {
      getLimiter().resetAllData(player);
   }

   public static void resetLimit(ServerPlayer player, ShopEntry entry) {
      getLimiter().resetEntryData(entry.getId(), player);
   }

   public static void resetLimit(ServerPlayer player, ShopTab entry) {
      getLimiter().resetTabData(entry.getId(), player);
   }

   @OnlyIn(Dist.CLIENT)
   public static void sendBuyEntry(ShopEntry entry, int count) {
      new SendBuyEntryC2S(entry.getOwnerShop().getId(), entry.getId(), count).sendToServer();
   }
}
