package net.sixik.sdmshop.client.screen_new.components.creator.data;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import net.minecraft.world.item.ItemStack;
import net.sixik.sdmshop.old_api.shop.AbstractEntryType;
import net.sixik.sdmshop.shop.ShopTab;

public class ShopCreatorComponentData {
   public static ShopCreatorComponentData Data = getData();
   private static ShopCreatorComponentData _data;
   public SelectedCreatorEnum SelectedCreator = SelectedCreatorEnum.Entry;
   public ShopCreatorComponentData.Entry Entry;
   public ShopCreatorComponentData.Category Category;
   public Map<String, Object> CustomData = new Object2ObjectOpenHashMap();

   private static ShopCreatorComponentData getData() {
      if (_data == null) {
         _data = new ShopCreatorComponentData();
         _data.setDefault();
      }

      return _data;
   }

   public static void loadDefault() {
      getData().setDefault();
   }

   private void setDefault() {
      this.SelectedCreator = SelectedCreatorEnum.Entry;
      this.Entry = new ShopCreatorComponentData.Entry();
      this.Category = new ShopCreatorComponentData.Category();
   }

   public static class Category {
      public String name = "";
   }

   public static class Entry {
      public AbstractEntryType selectedType = null;
      public ItemStack lastSelectedItemStack = ItemStack.f_41583_;
      public ShopTab selectedTab;
   }
}
