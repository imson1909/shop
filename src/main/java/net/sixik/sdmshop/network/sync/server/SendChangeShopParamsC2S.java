package net.sixik.sdmshop.network.sync.server;

import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdmshop.network.SDMShopNetwork;
import net.sixik.sdmshop.network.sync.SendChangeShopParamsS2C;
import net.sixik.sdmshop.server.SDMShopServer;
import net.sixik.sdmshop.shop.BaseShop;
import net.sixik.sdmshop.utils.ShopDebugUtils;
import net.sixik.sdmshop.utils.ShopNetworkUtils;

public class SendChangeShopParamsC2S extends BaseC2SMessage {
   private final UUID shopId;
   private final CompoundTag nbt;

   public SendChangeShopParamsC2S(BaseShop shop) {
      this(shop.getId(), shop.getParams().serialize());
   }

   public SendChangeShopParamsC2S(UUID shopId, CompoundTag nbt) {
      this.shopId = shopId;
      this.nbt = nbt;
   }

   public SendChangeShopParamsC2S(FriendlyByteBuf byteBuf) {
      this.shopId = byteBuf.m_130259_();
      this.nbt = byteBuf.m_130261_();
   }

   public MessageType getType() {
      return SDMShopNetwork.CHANGE_PARAMS_C2S;
   }

   public void write(FriendlyByteBuf friendlyByteBuf) {
      friendlyByteBuf.m_130077_(this.shopId);
      friendlyByteBuf.m_130079_(this.nbt);
   }

   public void handle(PacketContext packetContext) {
      ShopDebugUtils.log("SendChangeShopParamsC2S ACCEPT");
      SDMShopServer.InstanceOptional().flatMap(sdmShopServer -> sdmShopServer.getShop(this.shopId)).ifPresent(shop -> {
         ShopDebugUtils.log("ShopParams: {}", this.nbt);
         shop.getParams().deserialize(this.nbt);
         ShopNetworkUtils.changeShop(shop, new SendChangeShopParamsS2C(this.shopId, this.nbt), packetContext);
      });
   }
}
