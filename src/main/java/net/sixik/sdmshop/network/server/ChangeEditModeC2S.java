package net.sixik.sdmshop.network.server;

import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdmeconomy.utils.CurrencyHelper;
import net.sixik.sdmshop.network.SDMShopNetwork;
import net.sixik.sdmshop.utils.ShopUtils;

public class ChangeEditModeC2S extends BaseC2SMessage {
   private final boolean value;

   public ChangeEditModeC2S(boolean value) {
      this.value = value;
   }

   public ChangeEditModeC2S(FriendlyByteBuf value) {
      this.value = value.readBoolean();
   }

   public MessageType getType() {
      return SDMShopNetwork.CHANGE_EDIT_MODE_C2S;
   }

   public void write(FriendlyByteBuf friendlyByteBuf) {
      friendlyByteBuf.writeBoolean(this.value);
   }

   public void handle(PacketContext packetContext) {
      if (CurrencyHelper.isAdmin(packetContext.getPlayer())) {
         ShopUtils.changeEditMode(packetContext.getPlayer(), this.value);
      }
   }
}
