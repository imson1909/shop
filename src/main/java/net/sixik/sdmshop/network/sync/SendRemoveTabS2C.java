package net.sixik.sdmshop.network.sync;

import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdmshop.SDMShop;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.network.SDMShopNetwork;
import net.sixik.sdmshop.shop.BaseShop;

public class SendRemoveTabS2C extends BaseS2CMessage {
   private final UUID shopId;
   private final UUID tabUuid;

   public SendRemoveTabS2C(UUID shopId, UUID tabUuid) {
      this.shopId = shopId;
      this.tabUuid = tabUuid;
   }

   public SendRemoveTabS2C(FriendlyByteBuf byteBuf) {
      this.shopId = byteBuf.m_130259_();
      this.tabUuid = byteBuf.m_130259_();
   }

   public MessageType getType() {
      return SDMShopNetwork.SHOP_REMOVE_TAB_S2C;
   }

   public void write(FriendlyByteBuf friendlyByteBuf) {
      friendlyByteBuf.m_130077_(this.shopId);
      friendlyByteBuf.m_130077_(this.tabUuid);
   }

   public void handle(PacketContext packetContext) {
      BaseShop shop = SDMShopClient.CurrentShop;
      if (shop != null && Objects.equals(shop.getId(), this.shopId)) {
         if (!shop.removeTab(this.tabUuid).success()) {
            SDMShop.LOGGER.error("Can't remove shop tab {}", this.tabUuid);
         } else {
            if (!shop.removeEntry(s -> Objects.equals(s.getTab(), this.tabUuid), entry -> SDMShopClient.userData.getEntries().remove(entry.getId())).success()) {
               SDMShop.LOGGER.error("Can't remove shop tab entries {}", this.tabUuid);
            } else {
               SDMShopClient.userData.save();
               shop.onChange();
            }
         }
      }
   }
}
