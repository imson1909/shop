package net.sixik.sdmshop.compat.kubejs.events;

import dev.latvian.mods.kubejs.event.EventJS;
import net.sixik.sdmshop.api.ShopBase;

public class ShopEventChangeJS extends EventJS {
   private final ShopBase base;

   public ShopEventChangeJS(ShopBase base) {
      this.base = base;
   }

   public ShopBase getShop() {
      return this.base;
   }
}
