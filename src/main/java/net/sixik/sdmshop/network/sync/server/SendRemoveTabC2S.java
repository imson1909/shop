package net.sixik.sdmshop.network.sync.server;

import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdmshop.network.SDMShopNetwork;
import net.sixik.sdmshop.network.sync.SendRemoveTabS2C;
import net.sixik.sdmshop.server.SDMShopServer;
import net.sixik.sdmshop.shop.BaseShop;
import net.sixik.sdmshop.shop.ShopTab;
import net.sixik.sdmshop.utils.ShopNetworkUtils;
import net.sixik.sdmshop.utils.ShopUtils;

public class SendRemoveTabC2S extends BaseC2SMessage {
   private final UUID shopId;
   private final UUID tabUuid;

   public SendRemoveTabC2S(BaseShop shop, ShopTab tab) {
      this(shop.getId(), tab.getId());
   }

   public SendRemoveTabC2S(UUID shopId, UUID tabUuid) {
      this.shopId = shopId;
      this.tabUuid = tabUuid;
   }

   public SendRemoveTabC2S(FriendlyByteBuf byteBuf) {
      this.shopId = byteBuf.m_130259_();
      this.tabUuid = byteBuf.m_130259_();
   }

   public MessageType getType() {
      return SDMShopNetwork.SHOP_REMOVE_TAB_C2S;
   }

   public void write(FriendlyByteBuf friendlyByteBuf) {
      friendlyByteBuf.m_130077_(this.shopId);
      friendlyByteBuf.m_130077_(this.tabUuid);
   }

   public void handle(PacketContext packetContext) {
      if (ShopUtils.isEditMode(packetContext.getPlayer())) {
         Optional<BaseShop> optShop = SDMShopServer.Instance().getShop(this.shopId);
         if (!optShop.isEmpty()) {
            BaseShop shop = optShop.get();
            if (shop.removeTab(this.tabUuid).success() && shop.removeEntry(s -> Objects.equals(s.getTab(), this.tabUuid)).success()) {
               ShopNetworkUtils.changeShop(shop, new SendRemoveTabS2C(this.shopId, this.tabUuid), packetContext);
            }
         }
      }
   }
}
