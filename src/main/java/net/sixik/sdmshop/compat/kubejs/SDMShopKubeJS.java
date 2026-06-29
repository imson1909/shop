package net.sixik.sdmshop.compat.kubejs;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import net.sixik.sdmshop.api.ShopBase;
import net.sixik.sdmshop.api.ShopEvents;
import net.sixik.sdmshop.compat.kubejs.events.ShopEventChangeJS;
import net.sixik.sdmshop.compat.kubejs.events.ShopEventEntryAddJS;
import net.sixik.sdmshop.compat.kubejs.events.ShopEventEntryBuyJS;
import net.sixik.sdmshop.compat.kubejs.events.ShopEventEntryChangeJS;
import net.sixik.sdmshop.compat.kubejs.events.ShopEventEntryRemoveJS;
import net.sixik.sdmshop.compat.kubejs.events.ShopEventEntrySellJS;
import net.sixik.sdmshop.compat.kubejs.events.ShopEventTabAddJS;
import net.sixik.sdmshop.compat.kubejs.events.ShopEventTabChangeJS;
import net.sixik.sdmshop.compat.kubejs.events.ShopEventTabRemoveJS;

public class SDMShopKubeJS extends KubeJSPlugin {
   public static void initPlugin() {
      ShopEvents.ENTRY_BUY_EVENT
         .register(
            (ShopBase.EntryBuyListener)(base, entry, tab, player, count) -> ShopJSEvents.BUY_ENTRY
               .post(new ShopEventEntryBuyJS(base, entry, tab, player, count))
         );
      ShopEvents.ENTRY_SELL_EVENT
         .register(
            (ShopBase.EntrySellListener)(base, entry, tab, player, count) -> ShopJSEvents.SELL_ENTRY
               .post(new ShopEventEntrySellJS(base, entry, tab, player, count))
         );
      ShopEvents.SHOP_CHANGE_EVENT.register((ShopBase.ShopChangeListener)base -> ShopJSEvents.SHOP_CHANGE.post(new ShopEventChangeJS(base)));
      ShopEvents.ENTRY_ADD_EVENT
         .register((ShopBase.EntryAddListener)(shop, entry, tab) -> ShopJSEvents.ENTRY_ADD.post(new ShopEventEntryAddJS(shop, entry, tab)));
      ShopEvents.ENTRY_REMOVE_EVENT
         .register((ShopBase.EntryRemoveListener)(shop, entry, tab) -> ShopJSEvents.ENTRY_REMOVE.post(new ShopEventEntryRemoveJS(shop, entry, tab)));
      ShopEvents.ENTRY_CHANGE_EVENT
         .register((ShopBase.EntryChangeListener)(shop, entry, tab) -> ShopJSEvents.ENTRY_CHANGE.post(new ShopEventEntryChangeJS(shop, entry, tab)));
      ShopEvents.TAB_ADD_EVENT.register((ShopBase.TabAddListener)(shop, tab) -> ShopJSEvents.TAB_ADD.post(new ShopEventTabAddJS(shop, tab)));
      ShopEvents.TAB_REMOVE_EVENT.register((ShopBase.TabRemoveListener)(shop, tab) -> ShopJSEvents.TAB_REMOVE.post(new ShopEventTabRemoveJS(shop, tab)));
      ShopEvents.TAB_CHANGE_EVENT.register((ShopBase.TabChangeListener)(shop, tab) -> ShopJSEvents.TAB_CHANGE.post(new ShopEventTabChangeJS(shop, tab)));
   }

   public void registerBindings(BindingsEvent event) {
      if (event.getType().isServer()) {
         event.add("SDMShop", ShopServerJS.class);
      }

      if (event.getType().isClient()) {
         event.add("SDMShopClient", ShopClientJS.class);
      }
   }

   public void registerEvents() {
      ShopJSEvents.GROUP.register();
   }
}
