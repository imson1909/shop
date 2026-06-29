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

public class SendMoveTabS2C extends BaseS2CMessage {
   private final UUID shopId;
   private final UUID tabFrom;
   private final UUID tabTo;
   private final MoveType moveType;

   public SendMoveTabS2C(UUID shopId, UUID tabFrom, UUID tabTo, MoveType moveType) {
      this.shopId = shopId;
      this.tabFrom = tabFrom;
      this.tabTo = tabTo;
      this.moveType = moveType;
   }

   public SendMoveTabS2C(FriendlyByteBuf byteBuf) {
      this.shopId = byteBuf.m_130259_();
      this.tabFrom = byteBuf.m_130259_();
      this.tabTo = byteBuf.m_130259_();
      this.moveType = MoveType.values()[byteBuf.readInt()];
   }

   public MessageType getType() {
      return SDMShopNetwork.SHOP_MOVE_TAB_S2C;
   }

   public void write(FriendlyByteBuf friendlyByteBuf) {
      friendlyByteBuf.m_130077_(this.shopId);
      friendlyByteBuf.m_130077_(this.tabFrom);
      friendlyByteBuf.m_130077_(this.tabTo);
      friendlyByteBuf.writeInt(this.moveType.ordinal());
   }

   public void handle(PacketContext packetContext) {
      BaseShop shop = SDMShopClient.CurrentShop;
      if (shop != null && Objects.equals(shop.getId(), this.shopId)) {
         if (this.moveType != MoveType.Up && this.moveType != MoveType.Down) {
            if (!shop.swapTabs(this.tabFrom, this.tabTo, this.moveType)) {
               SDMShop.LOGGER.error("Can't move tab {} to {}", this.tabFrom, this.tabTo);
               return;
            }
         } else if (!shop.moveTab(this.tabFrom, this.moveType)) {
            SDMShop.LOGGER.error("Can't move tab {} method {}", this.tabFrom, this.moveType);
            return;
         }

         shop.onChange();
      }
   }
}
