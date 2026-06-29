package net.sixik.sdmshop.network.sync;

import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.network.SDMShopNetwork;
import net.sixik.sdmshop.shop.BaseShop;
import net.sixik.sdmshop.shop.ShopEntry;

public class SendAddEntryS2C extends BaseS2CMessage {
   private final UUID shopId;
   private final CompoundTag nbt;

   public SendAddEntryS2C(UUID shopId, CompoundTag nbt) {
      this.shopId = shopId;
      this.nbt = nbt;
   }

   public SendAddEntryS2C(FriendlyByteBuf byteBuf) {
      this.shopId = byteBuf.m_130259_();
      this.nbt = byteBuf.m_130261_();
   }

   public MessageType getType() {
      return SDMShopNetwork.SHOP_ADD_ENTRY_S2C;
   }

   public void write(FriendlyByteBuf friendlyByteBuf) {
      friendlyByteBuf.m_130077_(this.shopId);
      friendlyByteBuf.m_130079_(this.nbt);
   }

   public void handle(PacketContext packetContext) {
      BaseShop shop = SDMShopClient.CurrentShop;
      if (shop != null && Objects.equals(shop.getId(), this.shopId)) {
         ShopEntry entry = new ShopEntry(shop);
         entry.deserialize(this.nbt);
         shop.addEntry(entry);
         shop.onChange();
      }
   }
}
