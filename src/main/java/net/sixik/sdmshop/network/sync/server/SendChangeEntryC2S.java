package net.sixik.sdmshop.network.sync.server;

import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdmshop.network.SDMShopNetwork;
import net.sixik.sdmshop.network.sync.SendChangeEntryS2C;
import net.sixik.sdmshop.server.SDMShopServer;
import net.sixik.sdmshop.shop.BaseShop;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.utils.ShopNetworkUtils;
import net.sixik.sdmshop.utils.ShopUtils;

public class SendChangeEntryC2S extends BaseC2SMessage {
   private final UUID shopId;
   private final UUID entryUuid;
   private final CompoundTag nbt;

   public SendChangeEntryC2S(BaseShop shop, ShopEntry entry) {
      this(shop.getId(), entry.getId(), entry.serialize());
   }

   public SendChangeEntryC2S(UUID shopId, UUID entryUuid, CompoundTag nbt) {
      this.shopId = shopId;
      this.entryUuid = entryUuid;
      this.nbt = nbt;
   }

   public SendChangeEntryC2S(FriendlyByteBuf byteBuf) {
      this.shopId = byteBuf.m_130259_();
      this.entryUuid = byteBuf.m_130259_();
      this.nbt = byteBuf.m_130261_();
   }

   public MessageType getType() {
      return SDMShopNetwork.SHOP_CHANGE_ENTRY_C2S;
   }

   public void write(FriendlyByteBuf friendlyByteBuf) {
      friendlyByteBuf.m_130077_(this.shopId);
      friendlyByteBuf.m_130077_(this.entryUuid);
      friendlyByteBuf.m_130079_(this.nbt);
   }

   public void handle(PacketContext packetContext) {
      if (ShopUtils.isEditMode(packetContext.getPlayer())) {
         Optional<BaseShop> optShop = SDMShopServer.Instance().getShop(this.shopId);
         if (!optShop.isEmpty()) {
            BaseShop shop = optShop.get();
            shop.getEntryOptional(this.entryUuid).ifPresent(entry -> {
               entry.deserialize(this.nbt);
               ShopNetworkUtils.changeShop(shop, new SendChangeEntryS2C(this.shopId, this.entryUuid, this.nbt), packetContext);
            });
         }
      }
   }
}
