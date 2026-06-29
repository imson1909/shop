package net.sixik.sdmshop.utils;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.sixik.sdmshop.shop.BaseShop;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.shop.ShopTab;

public class ShopSerializerUtils {
   public static void deleteEntriesWithNonexistentTabs(BaseShop shop) {
      List<ShopEntry> entries = shop.getEntries();
      Set<UUID> existingTabIds = new ObjectArraySet();

      for (ShopTab tab : shop.getTabs()) {
         existingTabIds.add(tab.getId());
      }

      Set<UUID> nonexistentTabIds = new ObjectArraySet();

      for (ShopEntry entry : entries) {
         UUID tabId = entry.getTab();
         if (!existingTabIds.contains(tabId)) {
            nonexistentTabIds.add(tabId);
         }
      }

      shop.removeEntriesUnSafe(entryx -> nonexistentTabIds.contains(entryx.getTab()));
   }
}
