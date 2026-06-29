package net.sixik.sdmshop.network.sync.server;

import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.sixik.sdmshop.api.ShopApi;
import net.sixik.sdmshop.network.SDMShopNetwork;
import net.sixik.sdmshop.network.sync.SendLimiterS2C;
import net.sixik.sdmshop.old_api.shop.ShopObjectTypes;
import net.sixik.sdmshop.server.SDMShopServer;
import net.sixik.sdmshop.shop.limiter.ShopLimiter;
import net.sixik.sdmshop.utils.ShopAdminUtils;
import net.sixik.sdmshop.utils.ShopUtils;

public class SendResetLimiterC2S extends BaseC2SMessage {
   private final UUID objectId;
   private final ShopObjectTypes type;

   public SendResetLimiterC2S(UUID objectId, ShopObjectTypes type) {
      this.objectId = objectId;
      this.type = type;
   }

   public SendResetLimiterC2S(FriendlyByteBuf byteBuf) {
      this.objectId = byteBuf.m_130259_();
      this.type = ShopObjectTypes.values()[byteBuf.readShort()];
   }

   public MessageType getType() {
      return SDMShopNetwork.RESET_LIMITER_C2S;
   }

   public void write(FriendlyByteBuf friendlyByteBuf) {
      friendlyByteBuf.m_130077_(this.objectId);
      friendlyByteBuf.writeShort(this.type.ordinal());
   }

   public void handle(PacketContext packetContext) {
      Player player = packetContext.getPlayer();
      if (!ShopUtils.isEditMode(player)) {
         ShopAdminUtils.error(player, "Failed to clear limiter data: administrator rights required.");
      } else {
         ShopApi.resetAllData(this.objectId, this.type);
         ShopAdminUtils.info(player, "Limiter data for type %s with id %s cleared", this.type.name(), this.objectId);
         ShopLimiter limiter = ShopApi.getLimiter();

         for (ServerPlayer p : player.m_20194_().m_6846_().m_11314_()) {
            new SendLimiterS2C(limiter.serializeClient(p)).sendTo(p);
         }

         SDMShopServer.Instance().saveLimiter(player.m_20194_());
      }
   }
}
