package net.sixik.sdmshop.server;

import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.LifecycleEvent.ServerState;
import dev.architectury.event.events.common.PlayerEvent.PlayerJoin;
import net.sixik.sdmshop.network.sync.SendLimiterS2C;

public class SDMShopEvents {
   public static void init() {
      LifecycleEvent.SERVER_BEFORE_START.register(SDMShopServer::new);
      LifecycleEvent.SERVER_STOPPED.register((ServerState)s -> SDMShopServer.Instance().save(s));
      PlayerEvent.PLAYER_JOIN
         .register((PlayerJoin)serverPlayer -> new SendLimiterS2C(SDMShopServer.Instance().getShopLimiter().serializeClient(serverPlayer)).sendTo(serverPlayer));
   }
}
