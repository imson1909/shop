package net.sixik.sdmshop.utils;

import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.networking.simple.BaseS2CMessage;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.sixik.sdmshop.server.SDMShopServer;
import net.sixik.sdmshop.shop.BaseShop;

public class ShopNetworkUtils {
   public static void changeShop(BaseShop shop, BaseS2CMessage message, PacketContext context) {
      shop.onChangeForce();
      sendToAllExcept(message, context);
      SDMShopServer.Instance().saveShop(context.getPlayer().m_20194_(), shop.getId());
   }

   public static void sendToAllExcept(BaseS2CMessage message, PacketContext context) {
      sendToAllExcept(message, context.getPlayer().m_20194_(), (ServerPlayer)context.getPlayer());
   }

   public static void sendToAllExcept(BaseS2CMessage message, MinecraftServer server, UUID uuid) {
      for (ServerPlayer serverPlayer : server.m_6846_().m_11314_()) {
         if (!Objects.equals(serverPlayer.m_36316_().getId(), uuid)) {
            message.sendTo(serverPlayer);
         }
      }
   }

   public static void sendToAllExcept(BaseS2CMessage message, MinecraftServer server, ServerPlayer player) {
      for (ServerPlayer serverPlayer : server.m_6846_().m_11314_()) {
         if (!Objects.equals(serverPlayer, player)) {
            message.sendTo(serverPlayer);
         }
      }
   }
}
