package net.sixik.sdmshop.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.sixik.sdmshop.old_api.MoveType;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.shop.ShopParams;
import net.sixik.sdmshop.shop.ShopTab;
import net.sixik.sdmshop.utils.HashUtils;
import net.sixik.sdmshop.utils.ListHelper;
import net.sixik.sdmshop.utils.RemoveResult;
import net.sixik.sdmshop.utils.ShopDebugUtils;
import org.jetbrains.annotations.Nullable;

public interface ShopBase {
   String NULL_HASH = "null_hash";
   String ENTRIES_KEY = "shop_entries";
   String TABS_KEY = "shop_tabs";
   String PARAMS_KEY = "shop_params";

   ResourceLocation getRegistryId();

   UUID getId();

   default String getIdString() {
      return this.getId().toString();
   }

   ShopParams getParams();

   boolean isDirty();

   void setDirty(boolean var1);

   List<ShopBase.ShopChangeListener> getShopChangeListeners();

   default void onChangeForce() {
      this.onChangeMethod();
   }

   default void onChange() {
      if (this.isDirty()) {
         this.onChangeMethod();
      }
   }

   default void onChangeMethod() {
      List<ShopBase.ShopChangeListener> listeners = this.getShopChangeListeners();

      for (int i = 0; i < listeners.size(); i++) {
         listeners.get(i).handle(this);
      }

      ((ShopBase.ShopChangeListener)ShopEvents.SHOP_CHANGE_EVENT.invoker()).handle(this);
      this.onChangeEvent();
      this.setDirty(false);
   }

   default void clearData() {
      this.getEntries().clear();
      this.getTabs().clear();
      this.onChangeForce();
   }

   default void onChangeEvent() {
   }

   @Nullable
   Tag getCachedNbt();

   void setCachedNbt(Tag var1);

   String getVersion();

   void setVersion(String var1);

   default String calculateVersion() {
      if (!this.isDirty() && !this.getVersion().equals("null_hash") && !this.getVersion().isEmpty()) {
         return this.getVersion();
      } else if (this.getCachedNbt() instanceof CompoundTag compoundTag) {
         String hash = HashUtils.calculateHash(compoundTag);
         this.setVersion(hash);
         return hash;
      } else {
         DataResult<Tag> result = this.codecNetwork().encodeStart(NbtOps.f_128958_, this);
         Tag nbt = (Tag)result.result().orElse(null);
         if (nbt instanceof CompoundTag compoundTag) {
            String hash = HashUtils.calculateHash(compoundTag);
            this.setVersion(hash);
            return hash;
         } else {
            return "null_hash";
         }
      }
   }

   List<ShopEntry> getEntries();

   List<ShopTab> getTabs();

   <T extends ShopBase> Codec<T> codec();

   default <T extends ShopBase> Codec<T> codecNetwork() {
      return this.codec();
   }

   @Nullable
   default ShopTab getTab(UUID tabId) {
      if (tabId == null) {
         return null;
      }

      List<ShopTab> list = this.getTabs();

      for (int i = 0; i < list.size(); i++) {
         ShopTab tab = list.get(i);
         if (tabId.equals(tab.getId())) {
            return tab;
         }
      }

      return null;
   }

   default Optional<ShopTab> getTabOptional(UUID tabId) {
      return Optional.ofNullable(this.getTab(tabId));
   }

   @Nullable
   default ShopTab getTab(ShopEntry entry) {
      return this.getTab(entry.getTab());
   }

   default Optional<ShopTab> getTabOptional(ShopEntry entry) {
      return this.getTabOptional(entry.getTab());
   }

   @Nullable
   default ShopEntry getEntry(UUID entryId) {
      if (entryId == null) {
         return null;
      }

      List<ShopEntry> list = this.getEntries();

      for (int i = 0; i < list.size(); i++) {
         ShopEntry entry = list.get(i);
         if (entryId.equals(entry.getId())) {
            return entry;
         }
      }

      return null;
   }

   default Optional<ShopEntry> getEntryOptional(UUID entryId) {
      return Optional.ofNullable(this.getEntry(entryId));
   }

   default List<ShopEntry> getEntriesByTab(ShopTab tab) {
      return this.getEntriesByTab(tab.getId());
   }

   default List<ShopEntry> getEntriesByTab(UUID tabId) {
      ObjectArrayList<ShopEntry> entriesList = new ObjectArrayList();
      List<ShopEntry> list = this.getEntries();

      for (int i = 0; i < list.size(); i++) {
         ShopEntry entry = list.get(i);
         if (Objects.equals(entry.getTab(), tabId)) {
            entriesList.add(entry);
         }
      }

      return entriesList;
   }

   List<ShopBase.EntryAddListener> getEntryAddListeners();

   List<ShopBase.EntryRemoveListener> getEntryRemoveListeners();

   List<ShopBase.EntryChangeListener> getEntryChangeListeners();

   default boolean addEntry(ShopEntry entry) {
      return this.addEntry(null, entry);
   }

   default boolean addEntry(@Nullable ShopTab tab, ShopEntry entry) {
      if (entry == null) {
         return false;
      }

      ShopTab resolvedTab = tab != null ? tab : (entry.getTab() != null ? this.getTab(entry.getTab()) : null);
      if (resolvedTab == null) {
         return false;
      }

      if (this.getEntry(entry.getId()) != null) {
         return false;
      }

      this.getEntries().add(entry);
      this.onEntryAdd(entry, resolvedTab);
      this.setDirty(true);
      return true;
   }

   default RemoveResult removeEntriesUnSafe(ShopTab shopTab) {
      RemoveResult result = this.removeEntriesUnSafe(shopTab.getId());
      if (result.success()) {
         this.setDirty(true);
      }

      return result;
   }

   default RemoveResult removeEntriesUnSafe(UUID tabId) {
      if (tabId == null) {
         return RemoveResult.FAIL;
      } else if (this.getEntries().removeIf(s -> s.getTab().equals(tabId))) {
         this.setDirty(true);
         return RemoveResult.SUCCESS;
      } else {
         return RemoveResult.FAIL;
      }
   }

   default RemoveResult removeEntriesUnSafe(Predicate<ShopEntry> entry) {
      if (entry == null) {
         return RemoveResult.FAIL;
      } else if (this.getEntries().removeIf(entry)) {
         this.setDirty(true);
         return RemoveResult.SUCCESS;
      } else {
         return RemoveResult.FAIL;
      }
   }

   default RemoveResult removeEntry(ShopEntry entryBase) {
      return this.removeEntry(entryBase.getId());
   }

   default RemoveResult removeEntry(UUID entryBase) {
      if (entryBase == null) {
         return RemoveResult.FAIL;
      }

      int idx = this.indexOfEntry(entryBase);
      if (idx < 0) {
         return RemoveResult.FAIL;
      }

      ShopEntry removed = this.getEntries().remove(idx);
      ShopTab tab = this.getTab(removed.getTab());
      if (tab == null) {
         throw new NullPointerException("Tab is null!");
      }

      this.onEntryRemove(removed, tab);
      this.setDirty(true);
      return new RemoveResult(true);
   }

   default RemoveResult removeEntry(Predicate<ShopEntry> entryPredicate, Consumer<ShopEntry> onFind) {
      if (entryPredicate == null) {
         return RemoveResult.FAIL;
      }

      if (onFind == null) {
         return this.removeEntry(entryPredicate);
      }

      List<Integer> removedIndices = new ArrayList<>();
      List<ShopEntry> list = this.getEntries();

      for (int i = 0; i < list.size(); i++) {
         ShopEntry entry = list.get(i);
         if (entryPredicate.test(entry)) {
            onFind.accept(entry);
            ShopEntry removed = list.remove(i);
            removedIndices.add(i);
            ShopTab tab = this.getTab(removed.getTab());
            if (tab == null) {
               throw new NullPointerException("Tab is null!");
            }

            this.onEntryRemove(removed, tab);
            this.setDirty(true);
            return new RemoveResult(true, removedIndices);
         }
      }

      return new RemoveResult(false, removedIndices);
   }

   default RemoveResult removeEntry(Predicate<ShopEntry> entryPredicate) {
      if (entryPredicate == null) {
         return RemoveResult.FAIL;
      }

      List<Integer> removedIndices = new ArrayList<>();
      List<ShopEntry> list = this.getEntries();

      for (int i = 0; i < list.size(); i++) {
         ShopEntry entry = list.get(i);
         if (entryPredicate.test(entry)) {
            ShopEntry removed = list.remove(i);
            removedIndices.add(i);
            ShopTab tab = this.getTab(removed.getTab());
            if (tab == null) {
               throw new NullPointerException("Tab is null!");
            }

            this.onEntryRemove(removed, tab);
            this.setDirty(true);
            return new RemoveResult(true, removedIndices);
         }
      }

      return new RemoveResult(false, removedIndices);
   }

   default void entryChange(UUID entryId, Consumer<ShopEntry> consumer) {
      if (entryId != null && consumer != null) {
         ShopEntry entry = this.getEntry(entryId);
         if (entry != null) {
            consumer.accept(entry);
            ShopTab tab = this.getTab(entry.getTab());
            this.onEntryChange(entry, tab);
            this.setDirty(true);
         }
      }
   }

   default int indexOfEntry(UUID entryId) {
      if (entryId == null) {
         return -1;
      }

      List<ShopEntry> list = this.getEntries();

      for (int i = 0; i < list.size(); i++) {
         ShopEntry entry = list.get(i);
         if (entryId.equals(entry.getId())) {
            return i;
         }
      }

      return -1;
   }

   default void onEntryAdd(ShopEntry entry, ShopTab tab) {
      List<ShopBase.EntryAddListener> listeners = this.getEntryAddListeners();

      for (int i = 0; i < listeners.size(); i++) {
         listeners.get(i).handle(this, entry, tab);
      }

      ((ShopBase.EntryAddListener)ShopEvents.ENTRY_ADD_EVENT.invoker()).handle(this, entry, tab);
   }

   default void onEntryRemove(ShopEntry entry, ShopTab tab) {
      List<ShopBase.EntryRemoveListener> listeners = this.getEntryRemoveListeners();

      for (int i = 0; i < listeners.size(); i++) {
         listeners.get(i).handle(this, entry, tab);
      }

      ((ShopBase.EntryRemoveListener)ShopEvents.ENTRY_REMOVE_EVENT.invoker()).handle(this, entry, tab);
   }

   default void onEntryChange(ShopEntry entry, ShopTab tab) {
      List<ShopBase.EntryChangeListener> listeners = this.getEntryChangeListeners();

      for (int i = 0; i < listeners.size(); i++) {
         listeners.get(i).handle(this, entry, tab);
      }

      ((ShopBase.EntryChangeListener)ShopEvents.ENTRY_CHANGE_EVENT.invoker()).handle(this, entry, tab);
   }

   List<ShopBase.TabAddListener> getTabAddListeners();

   List<ShopBase.TabRemoveListener> getTabRemoveListeners();

   List<ShopBase.TabChangeListener> getTabChangeListeners();

   default boolean addTab(ShopTab tab) {
      if (tab == null) {
         return false;
      }

      int idx = this.indexOfTab(tab.getId());
      if (idx >= 0) {
         this.getTabs().set(idx, tab);
      } else {
         this.getTabs().add(tab);
      }

      this.onTabAdd(tab);
      this.setDirty(true);
      return true;
   }

   default RemoveResult removeTab(ShopTab tab) {
      return this.removeTab(tab.getId());
   }

   default RemoveResult removeTab(UUID tab) {
      if (tab == null) {
         return RemoveResult.FAIL;
      }

      int idx = this.indexOfTab(tab);
      if (idx < 0) {
         return RemoveResult.FAIL;
      }

      ShopTab removed = this.getTabs().remove(idx);
      this.onTabRemove(removed);
      this.setDirty(true);
      return RemoveResult.SUCCESS;
   }

   default int indexOfTab(UUID tabId) {
      if (tabId == null) {
         return -1;
      }

      List<ShopTab> list = this.getTabs();

      for (int i = 0; i < list.size(); i++) {
         ShopTab tab = list.get(i);
         if (tabId.equals(tab.getId())) {
            return i;
         }
      }

      return -1;
   }

   default void changeTab(UUID tabId, Consumer<ShopTab> consumer) {
      if (tabId != null && consumer != null) {
         ShopTab tab = this.getTab(tabId);
         if (tab != null) {
            consumer.accept(tab);
            this.onTabChange(tab);
            this.setDirty(true);
         }
      }
   }

   default void onTabAdd(ShopTab tab) {
      List<ShopBase.TabAddListener> list = this.getTabAddListeners();

      for (int i = 0; i < list.size(); i++) {
         list.get(i).handle(this, tab);
      }

      ((ShopBase.TabAddListener)ShopEvents.TAB_ADD_EVENT.invoker()).handle(this, tab);
   }

   default void onTabRemove(ShopTab tab) {
      List<ShopBase.TabRemoveListener> list = this.getTabRemoveListeners();

      for (int i = 0; i < list.size(); i++) {
         list.get(i).handle(this, tab);
      }

      ((ShopBase.TabRemoveListener)ShopEvents.TAB_REMOVE_EVENT.invoker()).handle(this, tab);
   }

   default void onTabChange(ShopTab tab) {
      List<ShopBase.TabChangeListener> list = this.getTabChangeListeners();

      for (int i = 0; i < list.size(); i++) {
         list.get(i).handle(this, tab);
      }

      ((ShopBase.TabChangeListener)ShopEvents.TAB_CHANGE_EVENT.invoker()).handle(this, tab);
   }

   default boolean swapEntries(UUID entryFrom, UUID entryTo, MoveType type) {
      if (entryFrom != null && entryTo != null && type != null) {
         List<ShopEntry> entries = this.getEntries();
         int i1 = -1;
         int i2 = -1;

         for (int i = 0; i < entries.size() && (i1 == -1 || i2 == -1); i++) {
            ShopEntry e = entries.get(i);
            if (e != null) {
               UUID id = e.getId();
               if (Objects.equals(id, entryFrom)) {
                  i1 = i;
               } else if (Objects.equals(id, entryTo)) {
                  i2 = i;
               }
            }
         }

         if (i1 != -1 && i2 != -1) {
            switch (type) {
               case Swap:
                  ListHelper.swap(entries, i1, i2);
                  break;
               case Insert:
                  ListHelper.insert(entries, i1, i2);
                  break;
               default:
                  return false;
            }

            ShopEntry e1 = entries.get(i2);
            ShopEntry e2 = entries.get(i1);
            if (e1 != null) {
               this.onEntryChange(e1, this.getTab(e1.getTab()));
            }

            if (e2 != null) {
               this.onEntryChange(e2, this.getTab(e2.getTab()));
            }

            this.setDirty(true);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   default boolean swapTabs(UUID tabFrom, UUID tabTo, MoveType type) {
      if (tabFrom != null && tabTo != null && type != null) {
         List<ShopTab> tabs = this.getTabs();
         int i1 = -1;
         int i2 = -1;

         for (int i = 0; i < tabs.size() && (i1 == -1 || i2 == -1); i++) {
            ShopTab t = tabs.get(i);
            if (t != null) {
               UUID id = t.getId();
               if (Objects.equals(id, tabFrom)) {
                  i1 = i;
               } else if (Objects.equals(id, tabTo)) {
                  i2 = i;
               }
            }
         }

         if (i1 != -1 && i2 != -1) {
            switch (type) {
               case Swap:
                  ListHelper.swap(tabs, i1, i2);
                  break;
               case Insert:
                  ListHelper.insert(tabs, i1, i2);
                  break;
               default:
                  return false;
            }

            ShopTab t1 = tabs.get(i2);
            ShopTab t2 = tabs.get(i1);
            if (t1 != null) {
               this.onTabChange(t1);
            }

            if (t2 != null) {
               this.onTabChange(t2);
            }

            this.setDirty(true);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   default boolean moveEntry(UUID entryId, MoveType direction) {
      if (entryId != null && direction != null) {
         List<ShopEntry> entries = this.getEntries();
         int index = -1;

         for (int i = 0; i < entries.size(); i++) {
            ShopEntry e = entries.get(i);
            if (e != null && Objects.equals(e.getId(), entryId)) {
               index = i;
               break;
            }
         }

         if (index == -1) {
            ShopDebugUtils.error("Entry with UUID {} not found!", entryId);
            return false;
         }

         if (direction != MoveType.Swap && direction != MoveType.Insert) {
            int newIndex = switch (direction) {
               case Up -> index - 1;
               case Down -> index + 1;
               default -> index;
            };
            if (newIndex >= 0 && newIndex < entries.size()) {
               Collections.swap(entries, index, newIndex);
               ShopEntry moved = entries.get(newIndex);
               if (moved != null) {
                  this.onEntryChange(moved, this.getTab(moved.getTab()));
               }

               this.setDirty(true);
               ShopDebugUtils.log("Moved entry {} {}: {} -> {}", entryId, direction, index, newIndex);
               return true;
            } else {
               ShopDebugUtils.log("Cannot move entry {} {}: already at {}!", entryId, direction, newIndex < 0 ? "start" : "end");
               return false;
            }
         } else {
            ShopDebugUtils.log("Cannot move entry {} with {}: use swapEntries(...) instead!", entryId, direction);
            return false;
         }
      } else {
         return false;
      }
   }

   default boolean moveTab(UUID tabId, MoveType direction) {
      if (tabId != null && direction != null) {
         List<ShopTab> tabs = this.getTabs();
         int index = -1;

         for (int i = 0; i < tabs.size(); i++) {
            ShopTab t = tabs.get(i);
            if (t != null && Objects.equals(t.getId(), tabId)) {
               index = i;
               break;
            }
         }

         if (index == -1) {
            ShopDebugUtils.error("Tab with UUID {} not found!", tabId);
            return false;
         }

         if (direction != MoveType.Swap && direction != MoveType.Insert) {
            int newIndex = switch (direction) {
               case Up -> index - 1;
               case Down -> index + 1;
               default -> index;
            };
            if (newIndex >= 0 && newIndex < tabs.size()) {
               Collections.swap(tabs, index, newIndex);
               ShopTab moved = tabs.get(newIndex);
               if (moved != null) {
                  this.onTabChange(moved);
               }

               this.setDirty(true);
               ShopDebugUtils.log("Moved tab {} {}: {} -> {}", tabId, direction, index, newIndex);
               return true;
            } else {
               ShopDebugUtils.log("Cannot move tab {} {}: already at {}!", tabId, direction, newIndex < 0 ? "start" : "end");
               return false;
            }
         } else {
            ShopDebugUtils.log("Cannot move tab {} with {}: use swapTabs(...) instead!", tabId, direction);
            return false;
         }
      } else {
         return false;
      }
   }

   CompoundTag serializeOrCache();

   default boolean isClient() {
      return false;
   }

   static boolean isVersionNull(String version) {
      return Objects.equals(version, "null_hash");
   }

   @FunctionalInterface
   interface EntryAddListener {
      void handle(ShopBase var1, ShopEntry var2, ShopTab var3);
   }

   @FunctionalInterface
   interface EntryBuyListener {
      void handle(ShopBase var1, ShopEntry var2, ShopTab var3, ServerPlayer var4, int var5);
   }

   @FunctionalInterface
   interface EntryChangeListener {
      void handle(ShopBase var1, ShopEntry var2, ShopTab var3);
   }

   @FunctionalInterface
   interface EntryRemoveListener {
      void handle(ShopBase var1, ShopEntry var2, ShopTab var3);
   }

   @FunctionalInterface
   interface EntrySellListener {
      void handle(ShopBase var1, ShopEntry var2, ShopTab var3, ServerPlayer var4, int var5);
   }

   @FunctionalInterface
   interface ShopChangeListener {
      void handle(ShopBase var1);
   }

   @FunctionalInterface
   interface TabAddListener {
      void handle(ShopBase var1, ShopTab var2);
   }

   @FunctionalInterface
   interface TabChangeListener {
      void handle(ShopBase var1, ShopTab var2);
   }

   @FunctionalInterface
   interface TabRemoveListener {
      void handle(ShopBase var1, ShopTab var2);
   }
}
