package net.sixik.sdmshop.network.sync.server;

import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdmshop.SDMShop;
import net.sixik.sdmshop.network.SDMShopNetwork;
import net.sixik.sdmshop.network.sync.SendRemoveEntryS2C;
import net.sixik.sdmshop.server.SDMShopServer;
import net.sixik.sdmshop.shop.BaseShop;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.utils.ShopNetworkUtils;
import net.sixik.sdmshop.utils.ShopUtils;

public class SendRemoveEntryC2S extends BaseC2SMessage {
   private final UUID shopId;
   private final UUID entryUuid;

   public SendRemoveEntryC2S(BaseShop shop, ShopEntry entry) {
      this(shop.getId(), entry.getId());
   }

   public SendRemoveEntryC2S(UUID shopId, UUID entryUuid) {
      this.shopId = shopId;
      this.entryUuid = entryUuid;
   }

   public SendRemoveEntryC2S(FriendlyByteBuf byteBuf) {
      this.shopId = byteBuf.m_130259_();
      this.entryUuid = byteBuf.m_130259_();
   }

   public MessageType getType() {
      return SDMShopNetwork.SHOP_REMOVE_ENTRY_C2S;
   }

   public void write(FriendlyByteBuf friendlyByteBuf) {
      friendlyByteBuf.m_130077_(this.shopId);
      friendlyByteBuf.m_130077_(this.entryUuid);
   }

   public void handle(PacketContext packetContext) {
      if (ShopUtils.isEditMode(packetContext.getPlayer())) {
         Optional<BaseShop> optShop = SDMShopServer.Instance().getShop(this.shopId);
         if (!optShop.isEmpty()) {
            BaseShop shop = optShop.get();
            if (!shop.removeEntry(this.entryUuid).success()) {
               SDMShop.LOGGER.warn("Can't remove shop entry {} he not exists!", this.entryUuid);
            } else {
               ShopNetworkUtils.changeShop(shop, new SendRemoveEntryS2C(this.shopId, this.entryUuid), packetContext);
            }
         }
      }
   }
}
