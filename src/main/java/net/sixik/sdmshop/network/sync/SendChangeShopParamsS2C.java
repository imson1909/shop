package net.sixik.sdmshop.network.sync;

import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdmshop.network.SDMShopNetwork;
import net.sixik.sdmshop.shop.BaseShop;
import net.sixik.sdmshop.utils.ShopDebugUtils;
import net.sixik.sdmshop.utils.ShopUtilsClient;

public class SendChangeShopParamsS2C extends BaseS2CMessage {
   private final UUID shopId;
   private final CompoundTag nbt;

   public SendChangeShopParamsS2C(BaseShop shop) {
      this(shop.getId(), shop.getParams().serialize());
   }

   public SendChangeShopParamsS2C(UUID shopId, CompoundTag nbt) {
      this.shopId = shopId;
      this.nbt = nbt;
   }

   public SendChangeShopParamsS2C(FriendlyByteBuf byteBuf) {
      this.shopId = byteBuf.m_130259_();
      this.nbt = byteBuf.m_130261_();
   }

   public MessageType getType() {
      return SDMShopNetwork.CHANGE_PARAMS_S2C;
   }

   public void write(FriendlyByteBuf friendlyByteBuf) {
      friendlyByteBuf.m_130077_(this.shopId);
      friendlyByteBuf.m_130079_(this.nbt);
   }

   public void handle(PacketContext packetContext) {
      ShopDebugUtils.log("SendChangeShopParamsS2C ACCEPT");
      BaseShop shop = ShopUtilsClient.getShop();
      if (shop != null && Objects.equals(shop.getId(), this.shopId)) {
         ShopDebugUtils.log("ShopParams: {}", this.nbt);
         shop.getParams().deserialize(this.nbt);
         shop.onChange();
      } else {
         ShopDebugUtils.error("Can't sync shop params! {}, {}", shop != null ? shop.getId() : "null", this.shopId);
      }
   }
}
