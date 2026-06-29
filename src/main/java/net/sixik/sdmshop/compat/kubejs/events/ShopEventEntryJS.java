package net.sixik.sdmshop.compat.kubejs.events;

import dev.latvian.mods.kubejs.event.EventJS;
import net.sixik.sdmshop.api.ShopBase;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.shop.ShopTab;

public class ShopEventEntryJS extends EventJS {
   private final ShopBase shop;
   private final ShopEntry entry;
   private final ShopTab tab;

   public ShopEventEntryJS(ShopBase shop, ShopEntry entry, ShopTab tab) {
      this.shop = shop;
      this.entry = entry;
      this.tab = tab;
   }

   public ShopEntry getEntry() {
      return this.entry;
   }

   public ShopTab getTab() {
      return this.tab;
   }

   public ShopBase getShop() {
      return this.shop;
   }
}
