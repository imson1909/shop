package net.sixik.sdmshop.api;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;

public interface ShopEvents {
   Event<ShopBase.EntryBuyListener> ENTRY_BUY_EVENT = EventFactory.createLoop(new ShopBase.EntryBuyListener[0]);
   Event<ShopBase.EntrySellListener> ENTRY_SELL_EVENT = EventFactory.createLoop(new ShopBase.EntrySellListener[0]);
   Event<ShopBase.ShopChangeListener> SHOP_CHANGE_EVENT = EventFactory.createLoop(new ShopBase.ShopChangeListener[0]);
   Event<ShopBase.EntryAddListener> ENTRY_ADD_EVENT = EventFactory.createLoop(new ShopBase.EntryAddListener[0]);
   Event<ShopBase.EntryChangeListener> ENTRY_CHANGE_EVENT = EventFactory.createLoop(new ShopBase.EntryChangeListener[0]);
   Event<ShopBase.EntryRemoveListener> ENTRY_REMOVE_EVENT = EventFactory.createLoop(new ShopBase.EntryRemoveListener[0]);
   Event<ShopBase.TabAddListener> TAB_ADD_EVENT = EventFactory.createLoop(new ShopBase.TabAddListener[0]);
   Event<ShopBase.TabChangeListener> TAB_CHANGE_EVENT = EventFactory.createLoop(new ShopBase.TabChangeListener[0]);
   Event<ShopBase.TabRemoveListener> TAB_REMOVE_EVENT = EventFactory.createLoop(new ShopBase.TabRemoveListener[0]);
}
