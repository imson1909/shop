package net.sixik.sdmshop.network.sync;

import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.network.SDMShopNetwork;
import net.sixik.sdmshop.shop.BaseShop;

public class SendChangeTabS2C extends BaseS2CMessage {
   private final UUID shopId;
   private final UUID tabId;
   private final CompoundTag nbt;

   public SendChangeTabS2C(UUID shopId, UUID tabId, CompoundTag nbt) {
      this.shopId = shopId;
      this.tabId = tabId;
      this.nbt = nbt;
   }

   public SendChangeTabS2C(FriendlyByteBuf byteBuf) {
      this.shopId = byteBuf.m_130259_();
      this.tabId = byteBuf.m_130259_();
      this.nbt = byteBuf.m_130261_();
   }

   public MessageType getType() {
      return SDMShopNetwork.SHOP_CHANGE_TAB_S2C;
   }

   public void write(FriendlyByteBuf friendlyByteBuf) {
      friendlyByteBuf.m_130077_(this.shopId);
      friendlyByteBuf.m_130077_(this.tabId);
      friendlyByteBuf.m_130079_(this.nbt);
   }

   public void handle(PacketContext packetContext) {
      BaseShop shop = SDMShopClient.CurrentShop;
      if (shop != null && Objects.equals(shop.getId(), this.shopId)) {
         shop.getTabOptional(this.tabId).ifPresent(shopTab -> {
            shopTab.deserialize(this.nbt);
            shop.onChange();
         });
      }
   }
}
