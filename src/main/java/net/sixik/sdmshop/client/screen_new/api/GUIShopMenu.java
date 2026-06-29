package net.sixik.sdmshop.client.screen_new.api;

import dev.ftb.mods.ftblibrary.ui.BaseScreen;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Widget;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.old_api.shop.AbstractEntryType;
import net.sixik.sdmshop.registers.ShopContentRegister;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.shop.sorts.AbstractEntryTypeFilter;
import net.sixik.v2.color.RGBA;

public interface GUIShopMenu {
   RGBA EMPTY = RGBA.create(255, 255, 255, 1);
   int EMPTY_INT = EMPTY.toInt();
   int BORDER_WIDTH = 1;
   int CORNER_SIZE = 4;
   RGBA BACKGROUND = RGBA.create(0, 0, 0, 85);
   RGBA BACKGROUND_FILL = RGBA.create(0, 0, 0, 255);
   RGBA BORDER = RGBA.create(255, 255, 255, 28);
   RGBA BORDER_2 = RGBA.create(255, 255, 255, 128);
   RGBA BORDER_3 = RGBA.create(66, 170, 255, 255);
   RGBA BORDER_4 = RGBA.create(144, 238, 144, 255);
   int BACKGROUND_INT = BACKGROUND.toInt();
   int BACKGROUND_FILL_INT = BACKGROUND_FILL.toInt();
   int BORDER_INT = BORDER.toInt();
   int BORDER_2_INT = BORDER_2.toInt();
   int BORDER_3_INT = BORDER_3.toInt();
   int BORDER_4_INT = BORDER_4.toInt();
   int INPUT_BOX_INT = -14737633;
   int INPUT_BOX_BORDER_INT = -13421773;

   Panel getParent();

   void alignWidgets();

   void addWidgets();

   void add(Widget var1);

   default void add(Widget widget, int w, int h) {
      this.add(widget);
      widget.setSize(w, h);
   }

   default BaseScreen self() {
      return (BaseScreen)this;
   }

   ObjectArrayList<ShopScreenEvents.OnModalOpen> getModalOpenListeners();

   ObjectArrayList<ShopScreenEvents.OnModalClose> getModalCloseListeners();

   static Map<Class<? extends AbstractEntryType>, List<AbstractEntryTypeFilter<? extends AbstractEntryType>>> createFilters() {
      ObjectArrayList<Function<Class<? extends AbstractEntryType>, AbstractEntryTypeFilter<? extends AbstractEntryType>>> factories = ShopContentRegister.getFilters();
      List<ShopEntry> entries = SDMShopClient.CurrentShop.getEntries();
      ObjectOpenHashSet<Class<? extends AbstractEntryType>> classes = new ObjectOpenHashSet();

      for (int i = 0; i < entries.size(); i++) {
         classes.add(entries.get(i).getEntryType().getClass());
      }

      Map<Class<? extends AbstractEntryType>, List<AbstractEntryTypeFilter<? extends AbstractEntryType>>> map = new Object2ObjectOpenHashMap(classes.size());
      ObjectIterator i = classes.iterator();

      while (i.hasNext()) {
         Class<? extends AbstractEntryType> cls = (Class<? extends AbstractEntryType>)i.next();
         ObjectArrayList<AbstractEntryTypeFilter<? extends AbstractEntryType>> filtersForClass = new ObjectArrayList();

         for (int f = 0; f < factories.size(); f++) {
            AbstractEntryTypeFilter<? extends AbstractEntryType> filter = (AbstractEntryTypeFilter<? extends AbstractEntryType>)((Function)factories.get(f))
               .apply(cls);
            if (filter != null) {
               filtersForClass.add(filter);
            }
         }

         if (!filtersForClass.isEmpty()) {
            map.put(cls, filtersForClass);
         }
      }

      for (int ix = 0; ix < entries.size(); ix++) {
         AbstractEntryType type = entries.get(ix).getEntryType();
         Class<? extends AbstractEntryType> cls = (Class<? extends AbstractEntryType>)type.getClass();
         List<AbstractEntryTypeFilter<? extends AbstractEntryType>> filters = map.getOrDefault(cls, null);
         if (filters != null) {
            for (int j = 0; j < filters.size(); j++) {
               filters.get(j).collectFromImpl(type);
            }
         }
      }

      return map;
   }
}
