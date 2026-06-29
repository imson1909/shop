package net.sixik.sdmshop.compat.kubejs.events;

import dev.latvian.mods.kubejs.event.EventJS;
import net.sixik.sdmshop.api.ShopBase;
import net.sixik.sdmshop.shop.ShopTab;

public class ShopEventTabJS extends EventJS {
   private final ShopBase shop;
   private final ShopTab tab;

   public ShopEventTabJS(ShopBase shop, ShopTab tab) {
      this.shop = shop;
      this.tab = tab;
   }

   public ShopBase getShop() {
      return this.shop;
   }

   public ShopTab getTab() {
      return this.tab;
   }
}
