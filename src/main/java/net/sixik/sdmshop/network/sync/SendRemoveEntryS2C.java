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

public class SendRemoveEntryS2C extends BaseS2CMessage {
   private final UUID shopId;
   private final UUID entryUuid;

   public SendRemoveEntryS2C(UUID shopId, UUID entryUuid) {
      this.shopId = shopId;
      this.entryUuid = entryUuid;
   }

   public SendRemoveEntryS2C(FriendlyByteBuf byteBuf) {
      this.shopId = byteBuf.m_130259_();
      this.entryUuid = byteBuf.m_130259_();
   }

   public MessageType getType() {
      return SDMShopNetwork.SHOP_REMOVE_ENTRY_S2C;
   }

   public void write(FriendlyByteBuf friendlyByteBuf) {
      friendlyByteBuf.m_130077_(this.shopId);
      friendlyByteBuf.m_130077_(this.entryUuid);
   }

   public void handle(PacketContext packetContext) {
      BaseShop shop = SDMShopClient.CurrentShop;
      if (shop != null && Objects.equals(shop.getId(), this.shopId)) {
         if (!shop.removeEntry(this.entryUuid).success()) {
            SDMShop.LOGGER.warn("Can't remove shop entry {} he not exists!", this.entryUuid);
         } else if (SDMShopClient.userData.getEntries().remove(this.entryUuid)) {
            SDMShopClient.userData.save();
         }

         shop.onChange();
      }
   }
}
