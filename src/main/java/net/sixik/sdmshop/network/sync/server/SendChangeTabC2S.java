package net.sixik.sdmshop.network.sync.server;

import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdmshop.network.SDMShopNetwork;
import net.sixik.sdmshop.network.sync.SendChangeTabS2C;
import net.sixik.sdmshop.server.SDMShopServer;
import net.sixik.sdmshop.shop.BaseShop;
import net.sixik.sdmshop.utils.ShopNetworkUtils;
import net.sixik.sdmshop.utils.ShopUtils;

public class SendChangeTabC2S extends BaseC2SMessage {
   private final UUID shopId;
   private final UUID tabId;
   private final CompoundTag nbt;

   public SendChangeTabC2S(UUID shopId, UUID tabId, CompoundTag nbt) {
      this.shopId = shopId;
      this.tabId = tabId;
      this.nbt = nbt;
   }

   public SendChangeTabC2S(FriendlyByteBuf byteBuf) {
      this.shopId = byteBuf.m_130259_();
      this.tabId = byteBuf.m_130259_();
      this.nbt = byteBuf.m_130261_();
   }

   public MessageType getType() {
      return SDMShopNetwork.SHOP_CHANGE_TAB_C2S;
   }

   public void write(FriendlyByteBuf friendlyByteBuf) {
      friendlyByteBuf.m_130077_(this.shopId);
      friendlyByteBuf.m_130077_(this.tabId);
      friendlyByteBuf.m_130079_(this.nbt);
   }

   public void handle(PacketContext packetContext) {
      if (ShopUtils.isEditMode(packetContext.getPlayer())) {
         Optional<BaseShop> optShop = SDMShopServer.Instance().getShop(this.shopId);
         if (!optShop.isEmpty()) {
            BaseShop shop = optShop.get();
            shop.getTabOptional(this.tabId).ifPresent(shopTab -> {
               shopTab.deserialize(this.nbt);
               ShopNetworkUtils.changeShop(shop, new SendChangeTabS2C(this.shopId, this.tabId, this.nbt), packetContext);
            });
         }
      }
   }
}
