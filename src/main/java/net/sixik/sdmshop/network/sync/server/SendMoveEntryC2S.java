package net.sixik.sdmshop.network.sync.server;

import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdmshop.SDMShop;
import net.sixik.sdmshop.network.SDMShopNetwork;
import net.sixik.sdmshop.network.sync.SendMoveEntryS2C;
import net.sixik.sdmshop.old_api.MoveType;
import net.sixik.sdmshop.server.SDMShopServer;
import net.sixik.sdmshop.shop.BaseShop;
import net.sixik.sdmshop.utils.ShopNetworkUtils;
import net.sixik.sdmshop.utils.ShopUtils;

public class SendMoveEntryC2S extends BaseC2SMessage {
   private final UUID shopId;
   private final UUID entryFrom;
   private final UUID entryTo;
   private final MoveType moveType;

   public SendMoveEntryC2S(UUID shopId, UUID entryFrom, UUID entryTo, MoveType moveType) {
      this.shopId = shopId;
      this.entryFrom = entryFrom;
      this.entryTo = entryTo;
      this.moveType = moveType;
   }

   public SendMoveEntryC2S(FriendlyByteBuf byteBuf) {
      this.shopId = byteBuf.m_130259_();
      this.entryFrom = byteBuf.m_130259_();
      this.entryTo = byteBuf.m_130259_();
      this.moveType = MoveType.values()[byteBuf.readInt()];
   }

   public MessageType getType() {
      return SDMShopNetwork.SHOP_MOVE_ENTRY_C2S;
   }

   public void write(FriendlyByteBuf friendlyByteBuf) {
      friendlyByteBuf.m_130077_(this.shopId);
      friendlyByteBuf.m_130077_(this.entryFrom);
      friendlyByteBuf.m_130077_(this.entryTo);
      friendlyByteBuf.writeInt(this.moveType.ordinal());
   }

   public void handle(PacketContext packetContext) {
      if (ShopUtils.isEditMode(packetContext.getPlayer())) {
         Optional<BaseShop> optShop = SDMShopServer.Instance().getShop(this.shopId);
         if (!optShop.isEmpty()) {
            BaseShop shop = optShop.get();
            if (this.moveType != MoveType.Up && this.moveType != MoveType.Down) {
               if (!shop.swapEntries(this.entryFrom, this.entryTo, this.moveType)) {
                  SDMShop.LOGGER.error("Can't move entry {} to {}", this.entryFrom, this.entryTo);
                  return;
               }
            } else if (!shop.moveEntry(this.entryFrom, this.moveType)) {
               SDMShop.LOGGER.error("Can't move entry {} method {}", this.entryFrom, this.moveType);
               return;
            }

            ShopNetworkUtils.changeShop(shop, new SendMoveEntryS2C(this.shopId, this.entryFrom, this.entryTo, this.moveType), packetContext);
         }
      }
   }
}
