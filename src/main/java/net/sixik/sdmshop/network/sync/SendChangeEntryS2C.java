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

public class SendChangeEntryS2C extends BaseS2CMessage {
   private final UUID shopId;
   private final UUID entryUuid;
   private final CompoundTag nbt;

   public SendChangeEntryS2C(UUID shopId, UUID entryUuid, CompoundTag nbt) {
      this.shopId = shopId;
      this.entryUuid = entryUuid;
      this.nbt = nbt;
   }

   public SendChangeEntryS2C(FriendlyByteBuf byteBuf) {
      this.shopId = byteBuf.m_130259_();
      this.entryUuid = byteBuf.m_130259_();
      this.nbt = byteBuf.m_130261_();
   }

   public MessageType getType() {
      return SDMShopNetwork.SHOP_CHANGE_ENTRY_S2C;
   }

   public void write(FriendlyByteBuf friendlyByteBuf) {
      friendlyByteBuf.m_130077_(this.shopId);
      friendlyByteBuf.m_130077_(this.entryUuid);
      friendlyByteBuf.m_130079_(this.nbt);
   }

   public void handle(PacketContext packetContext) {
      BaseShop shop = SDMShopClient.CurrentShop;
      if (shop != null && Objects.equals(shop.getId(), this.shopId)) {
         shop.getEntryOptional(this.entryUuid).ifPresent(entry -> {
            entry.deserialize(this.nbt);
            shop.onChange();
         });
      }
   }
}
