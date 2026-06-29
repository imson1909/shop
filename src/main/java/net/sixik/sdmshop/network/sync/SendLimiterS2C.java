package net.sixik.sdmshop.network.sync;

import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.network.SDMShopNetwork;

public class SendLimiterS2C extends BaseS2CMessage {
   private final CompoundTag nbt;

   public SendLimiterS2C(CompoundTag nbt) {
      this.nbt = nbt;
   }

   public SendLimiterS2C(FriendlyByteBuf byteBuf) {
      this.nbt = byteBuf.m_130261_();
   }

   public MessageType getType() {
      return SDMShopNetwork.SEND_LIMITER;
   }

   public void write(FriendlyByteBuf friendlyByteBuf) {
      friendlyByteBuf.m_130079_(this.nbt);
   }

   public void handle(PacketContext packetContext) {
      SDMShopClient.shopLimiter.deserializeClient(this.nbt);
   }
}
