package net.sixik.sdmshop.network.sync.server;

import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdmshop.SDMShop;
import net.sixik.sdmshop.network.SDMShopNetwork;
import net.sixik.sdmshop.network.sync.SendAddTabS2C;
import net.sixik.sdmshop.server.SDMShopServer;
import net.sixik.sdmshop.shop.BaseShop;
import net.sixik.sdmshop.shop.ShopTab;
import net.sixik.sdmshop.utils.ShopNetworkUtils;
import net.sixik.sdmshop.utils.ShopUtils;

public class SendAddTabC2S extends BaseC2SMessage {
   private final UUID shopId;
   private final CompoundTag nbt;

   public SendAddTabC2S(BaseShop shop, ShopTab tab) {
      this(shop.getId(), tab.serialize());
   }

   public SendAddTabC2S(UUID shopId, CompoundTag nbt) {
      this.shopId = shopId;
      this.nbt = nbt;
   }

   public SendAddTabC2S(FriendlyByteBuf byteBuf) {
      this.shopId = byteBuf.m_130259_();
      this.nbt = byteBuf.m_130261_();
   }

   public MessageType getType() {
      return SDMShopNetwork.SHOP_ADD_TAB_C2S;
   }

   public void write(FriendlyByteBuf friendlyByteBuf) {
      friendlyByteBuf.m_130077_(this.shopId);
      friendlyByteBuf.m_130079_(this.nbt);
   }

   public void handle(PacketContext packetContext) {
      if (ShopUtils.isEditMode(packetContext.getPlayer())) {
         Optional<BaseShop> optShop = SDMShopServer.Instance().getShop(this.shopId);
         if (optShop.isEmpty()) {
            SDMShop.LOGGER.error("Shop {} not found!", this.shopId);
         } else {
            BaseShop shop = optShop.get();
            ShopTab tab = new ShopTab(shop);
            tab.deserialize(this.nbt);
            shop.getTabs().add(tab);
            ShopNetworkUtils.changeShop(shop, new SendAddTabS2C(this.shopId, this.nbt), packetContext);
         }
      }
   }
}
