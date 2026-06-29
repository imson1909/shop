package net.sixik.sdmshop.network.economy;

import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;
import net.sixik.sdmeconomy.utils.CurrencyHelper;
import net.sixik.sdmshop.network.SDMShopNetwork;
import net.sixik.sdmshop.utils.ShopUtils;

public class ShopChangeMoneyC2S extends BaseC2SMessage {
   private final String moneyName;
   private final double value;

   public ShopChangeMoneyC2S(String moneyName, double value) {
      this.moneyName = moneyName;
      this.value = value;
   }

   public ShopChangeMoneyC2S(FriendlyByteBuf byteBuf) {
      this.moneyName = byteBuf.m_130277_();
      this.value = byteBuf.readDouble();
   }

   public MessageType getType() {
      return SDMShopNetwork.SHOP_CHANGE_MONEY;
   }

   public void write(FriendlyByteBuf friendlyByteBuf) {
      friendlyByteBuf.m_130070_(this.moneyName);
      friendlyByteBuf.writeDouble(this.value);
   }

   public void handle(PacketContext packetContext) {
      if (CurrencyHelper.isAdmin(packetContext.getPlayer())) {
         ShopUtils.setMoney(packetContext.getPlayer(), this.moneyName, this.value);
      }
   }
}
