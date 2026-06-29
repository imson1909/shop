package net.sixik.sdmshop.network.sync.server;

import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdmshop.SDMShop;
import net.sixik.sdmshop.network.SDMShopNetwork;
import net.sixik.sdmshop.network.sync.SendMoveTabS2C;
import net.sixik.sdmshop.old_api.MoveType;
import net.sixik.sdmshop.server.SDMShopServer;
import net.sixik.sdmshop.shop.BaseShop;
import net.sixik.sdmshop.utils.ShopNetworkUtils;
import net.sixik.sdmshop.utils.ShopUtils;

public class SendMoveTabC2S extends BaseC2SMessage {
   private final UUID shopId;
   private final UUID tabFrom;
   private final UUID tabTo;
   private final MoveType moveType;

   public SendMoveTabC2S(UUID shopId, UUID tabFrom, UUID tabTo, MoveType moveType) {
      this.shopId = shopId;
      this.tabFrom = tabFrom;
      this.tabTo = tabTo;
      this.moveType = moveType;
   }

   public SendMoveTabC2S(FriendlyByteBuf byteBuf) {
      this.shopId = byteBuf.m_130259_();
      this.tabFrom = byteBuf.m_130259_();
      this.tabTo = byteBuf.m_130259_();
      this.moveType = MoveType.values()[byteBuf.readInt()];
   }

   public MessageType getType() {
      return SDMShopNetwork.SHOP_MOVE_TAB_C2S;
   }

   public void write(FriendlyByteBuf friendlyByteBuf) {
      friendlyByteBuf.m_130077_(this.shopId);
      friendlyByteBuf.m_130077_(this.tabFrom);
      friendlyByteBuf.m_130077_(this.tabTo);
      friendlyByteBuf.writeInt(this.moveType.ordinal());
   }

   public void handle(PacketContext packetContext) {
      if (ShopUtils.isEditMode(packetContext.getPlayer())) {
         Optional<BaseShop> optShop = SDMShopServer.Instance().getShop(this.shopId);
         if (!optShop.isEmpty()) {
            BaseShop shop = optShop.get();
            if (this.moveType != MoveType.Up && this.moveType != MoveType.Down) {
               if (!shop.swapTabs(this.tabFrom, this.tabTo, this.moveType)) {
                  SDMShop.LOGGER.error("Can't move tab {} to {}", this.tabFrom, this.tabTo);
                  return;
               }
            } else if (!shop.moveTab(this.tabFrom, this.moveType)) {
               SDMShop.LOGGER.error("Can't move tab {} method {}", this.tabFrom, this.moveType);
               return;
            }

            ShopNetworkUtils.changeShop(shop, new SendMoveTabS2C(this.shopId, this.tabFrom, this.tabTo, this.moveType), packetContext);
         }
      }
   }
}
