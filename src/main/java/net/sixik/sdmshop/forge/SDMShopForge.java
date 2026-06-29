package net.sixik.sdmshop.forge;

import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.sixik.sdmshop.SDMShop;

@Mod("sdmshop")
public final class SDMShopForge {
   public SDMShopForge() {
      IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
      EventBuses.registerModEventBus("sdmshop", bus);
      SDMShop.init();
   }
}
