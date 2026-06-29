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
import net.sixik.sdmshop.old_api.MoveType;
import net.sixik.sdmshop.shop.BaseShop;

public class SendMoveEntryS2C extends BaseS2CMessage {
   private final UUID shopId;
   private final UUID entryFrom;
   private final UUID entryTo;
   private final MoveType moveType;

   public SendMoveEntryS2C(UUID shopId, UUID entryFrom, UUID entryTo, MoveType moveType) {
      this.shopId = shopId;
      this.entryFrom = entryFrom;
      this.entryTo = entryTo;
      this.moveType = moveType;
   }

   public SendMoveEntryS2C(FriendlyByteBuf byteBuf) {
      this.shopId = byteBuf.m_130259_();
      this.entryFrom = byteBuf.m_130259_();
      this.entryTo = byteBuf.m_130259_();
      this.moveType = MoveType.values()[byteBuf.readInt()];
   }

   public MessageType getType() {
      return SDMShopNetwork.SHOP_MOVE_ENTRY_S2C;
   }

   public void write(FriendlyByteBuf friendlyByteBuf) {
      friendlyByteBuf.m_130077_(this.shopId);
      friendlyByteBuf.m_130077_(this.entryFrom);
      friendlyByteBuf.m_130077_(this.entryTo);
      friendlyByteBuf.writeInt(this.moveType.ordinal());
   }

   public void handle(PacketContext packetContext) {
      BaseShop shop = SDMShopClient.CurrentShop;
      if (shop != null && Objects.equals(shop.getId(), this.shopId)) {
         if (this.moveType != MoveType.Up && this.moveType != MoveType.Down) {
            if (!shop.swapEntries(this.entryFrom, this.entryTo, this.moveType)) {
               SDMShop.LOGGER.error("Can't move entry {} to {}", this.entryFrom, this.entryTo);
               return;
            }
         } else if (!shop.moveEntry(this.entryFrom, this.moveType)) {
            SDMShop.LOGGER.error("Can't move entry {} method {}", this.entryFrom, this.moveType);
            return;
         }

         shop.onChange();
      }
   }
}
