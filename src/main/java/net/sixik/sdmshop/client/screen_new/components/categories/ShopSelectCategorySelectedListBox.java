package net.sixik.sdmshop.client.screen_new.components.categories;

import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.sixik.sdmshop.client.screen_new.api.GUIShopMenu;
import net.sixik.sdmshop.shop.ShopTab;
import net.sixik.sdmshop.utils.rendering.ShopRenderingWrapper;
import org.joml.Math;

public class ShopSelectCategorySelectedListBox extends Panel {
   protected final ObjectArrayList<ShopSelectCategoriesComponentModalPanel.AddedCategoriesButton> availableCategoriesButton = new ObjectArrayList();
   protected final ShopSelectCategoriesComponentModalPanel modalPanel;
   protected final List<ShopTab> selectedCategories;
   public int elementW;
   public int borderOffest;
   public int spacing;

   public ShopSelectCategorySelectedListBox(ShopSelectCategoriesComponentModalPanel panel, List<ShopTab> selectedCategories, int borderOffest, int spacing) {
      super(panel);
      this.modalPanel = panel;
      this.selectedCategories = selectedCategories;
      this.borderOffest = borderOffest;
      this.spacing = spacing;
   }

   public void addWidgets() {
      this.availableCategoriesButton.clear();
      this.elementW = this.width / 3;
      List<ShopTab> list = this.selectedCategories;

      for (int i = 0; i < list.size(); i++) {
         ShopTab category = list.get(i);
         var button = new ShopSelectCategoriesComponentModalPanel.AddedCategoriesButton(this, category) {
            @Override
            public void onClicked(MouseButton button) {
               if (button.isLeft()) {
                  Iterator<ShopTab> iterator = ShopSelectCategorySelectedListBox.this.selectedCategories.iterator();
                  boolean find = false;

                  while (iterator.hasNext()) {
                     ShopTab element = iterator.next();
                     if (element != null && element.getId().equals(this.tab.getId())) {
                        iterator.remove();
                        find = true;
                        break;
                     }
                  }

                  if (!find) {
                     ShopSelectCategorySelectedListBox.this.selectedCategories.add(this.tab);
                  }

                  ShopSelectCategorySelectedListBox.this.modalPanel.updateSelectedList();
               }
            }
         };
         this.add(button);
         button.width = this.elementW;
         this.availableCategoriesButton.add(button);
      }
   }

   public void alignWidgets() {
      int zoneW = this.width - this.borderOffest * 2;
      int startX = this.borderOffest;
      int startY = this.borderOffest;
      ObjectArrayList<ShopSelectCategoriesComponentModalPanel.AddedCategoriesButton> list = this.availableCategoriesButton;
      if (!list.isEmpty() && zoneW > 0) {
         int y = startY;
         int i = 0;

         while (i < list.size()) {
            int rowStart = i;
            int rowW = 0;
            int rowH = 0;

            while (i < list.size()) {
               ShopSelectCategoriesComponentModalPanel.AddedCategoriesButton w = (ShopSelectCategoriesComponentModalPanel.AddedCategoriesButton)list.get(i);
               int wW = Math.clamp(w.width, 1, zoneW);
               int wH = java.lang.Math.max(1, w.height);
               int add = rowW == 0 ? wW : this.spacing + wW;
               if (rowW > 0 && rowW + add > zoneW) {
                  break;
               }

               rowW += add;
               rowH = java.lang.Math.max(rowH, wH);
               i++;
            }

            int offsetX = java.lang.Math.max(0, (zoneW - rowW) / 2);
            int x = startX + offsetX;

            for (int j = rowStart; j < i; j++) {
               ShopSelectCategoriesComponentModalPanel.AddedCategoriesButton w = (ShopSelectCategoriesComponentModalPanel.AddedCategoriesButton)list.get(j);
               int wW = Math.clamp(w.width, 1, zoneW);
               w.posX = x;
               w.posY = y;
               x += wW + this.spacing;
            }

            y += rowH + this.spacing;
         }
      }
   }

   public void updateStats() {
   }

   public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
      ShopRenderingWrapper.beginBatch(w, h, 4.0F, 1.0F);
      ShopRenderingWrapper.addBatchRect(graphics, x, y, w, h, GUIShopMenu.BACKGROUND_INT, GUIShopMenu.BORDER_INT);
      ShopRenderingWrapper.endBatch();
   }
}
