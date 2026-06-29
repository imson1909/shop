package net.sixik.sdmshop.compat.kubejs;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import net.sixik.sdmshop.compat.kubejs.events.ShopEventChangeJS;
import net.sixik.sdmshop.compat.kubejs.events.ShopEventEntryAddJS;
import net.sixik.sdmshop.compat.kubejs.events.ShopEventEntryBuyJS;
import net.sixik.sdmshop.compat.kubejs.events.ShopEventEntryChangeJS;
import net.sixik.sdmshop.compat.kubejs.events.ShopEventEntryRemoveJS;
import net.sixik.sdmshop.compat.kubejs.events.ShopEventEntrySellJS;
import net.sixik.sdmshop.compat.kubejs.events.ShopEventTabAddJS;
import net.sixik.sdmshop.compat.kubejs.events.ShopEventTabChangeJS;
import net.sixik.sdmshop.compat.kubejs.events.ShopEventTabRemoveJS;

public interface ShopJSEvents {
   EventGroup GROUP = EventGroup.of("SDMShopEvents");
   EventHandler BUY_ENTRY = GROUP.server("buyEntry", () -> ShopEventEntryBuyJS.class);
   EventHandler SELL_ENTRY = GROUP.server("sellEntry", () -> ShopEventEntrySellJS.class);
   EventHandler SHOP_CHANGE = GROUP.server("shopChange", () -> ShopEventChangeJS.class);
   EventHandler ENTRY_ADD = GROUP.server("entryAdd", () -> ShopEventEntryAddJS.class);
   EventHandler ENTRY_REMOVE = GROUP.server("entryRemove", () -> ShopEventEntryRemoveJS.class);
   EventHandler ENTRY_CHANGE = GROUP.server("entryChange", () -> ShopEventEntryChangeJS.class);
   EventHandler TAB_ADD = GROUP.server("tabAdd", () -> ShopEventTabAddJS.class);
   EventHandler TAB_REMOVE = GROUP.server("tabRemove", () -> ShopEventTabRemoveJS.class);
   EventHandler TAB_CHANGE = GROUP.server("tabChange", () -> ShopEventTabChangeJS.class);
}
