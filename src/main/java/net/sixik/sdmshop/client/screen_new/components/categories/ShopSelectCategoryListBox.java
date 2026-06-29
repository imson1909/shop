package net.sixik.sdmshop.client.screen_new.components.categories;

import dev.ftb.mods.ftblibrary.ui.ContextMenuItem;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.Widget;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.gui.GuiGraphics;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.client.screen_new.api.GUIShopMenu;
import net.sixik.sdmshop.shop.ShopTab;
import net.sixik.sdmshop.utils.ShopContextMenuUtils;
import net.sixik.sdmshop.utils.ShopUtils;
import net.sixik.sdmshop.utils.config.SDMEditConfigScreen;
import net.sixik.sdmshop.utils.rendering.ShopRenderingWrapper;
import org.joml.Math;

public class ShopSelectCategoryListBox extends Panel {
   protected SDMEditConfigScreen editConfigScreen;
   protected final ObjectArrayList<ShopSelectCategoriesComponentModalPanel.SelectCategoriesButton> availableCategoriesButton = new ObjectArrayList();
   protected final ShopSelectCategoriesComponentModalPanel modalPanel;
   protected final List<ShopTab> selectedCategories;
   public int elementW;
   public int borderOffest;
   public int spacing;
   private String lastSearch = "";

   public ShopSelectCategoryListBox(ShopSelectCategoriesComponentModalPanel panel, List<ShopTab> selectedCategories, int borderOffest, int spacing) {
      super(panel);
      this.modalPanel = panel;
      this.selectedCategories = selectedCategories;
      this.borderOffest = borderOffest;
      this.spacing = spacing;
   }

   public void addWidgets() {
      this.availableCategoriesButton.clear();
      this.elementW = this.width / 3;
      List<ShopTab> list = SDMShopClient.CurrentShop.getTabs();

      for (int i = 0; i < list.size(); i++) {
         ShopTab category = list.get(i);
         ShopSelectCategoriesComponentModalPanel.SelectCategoriesButton button = new ShopSelectCategoriesComponentModalPanel.SelectCategoriesButton(
            this, category, (b, tab) -> {
               if (!b.isLeft()) {
                  List<ContextMenuItem> contextMenu = ShopContextMenuUtils.getContextMenu(tab, deleted -> {
                     this.selectedCategories.removeIf(findx -> findx.getId().equals(deleted.getId()));
                     this.getParent().refreshWidgets();
                  }, s -> this.editConfigScreen = s, () -> {
                     if (this.editConfigScreen != null) {
                        this.getParent().refreshWidgets();
                        this.editConfigScreen.closeGui();
                     }
                  }, 14);
                  if (!contextMenu.isEmpty()) {
                     this.getGui().openContextMenu(contextMenu);
                  }
               } else {
                  Iterator<ShopTab> iterator = this.selectedCategories.iterator();
                  boolean find = false;

                  while (iterator.hasNext()) {
                     ShopTab element = iterator.next();
                     if (element != null && element.getId().equals(tab.getId())) {
                        iterator.remove();
                        find = true;
                        break;
                     }
                  }

                  if (!find) {
                     this.selectedCategories.add(tab);
                  }

                  this.modalPanel.updateSelectedList();
               }
            }
         );
         this.add(button);
         button.width = this.elementW;
         this.availableCategoriesButton.add(button);
      }
   }

   public void alignWidgets() {
      int zoneW = this.width - this.borderOffest * 2;
      int startX = this.borderOffest;
      int startY = this.borderOffest;
      ObjectArrayList<ShopSelectCategoriesComponentModalPanel.SelectCategoriesButton> list = this.availableCategoriesButton;
      if (!list.isEmpty() && zoneW > 0) {
         int y = startY;
         List<Widget> row = new ObjectArrayList(16);
         int i = 0;

         while (i < list.size()) {
            row.clear();
            int rowW = 0;
            int rowH = 0;

            while (i < list.size()) {
               ShopSelectCategoriesComponentModalPanel.SelectCategoriesButton w = (ShopSelectCategoriesComponentModalPanel.SelectCategoriesButton)list.get(i);
               i++;
               if (w.isEnabled()) {
                  int wW = Math.clamp(w.width, 1, zoneW);
                  int wH = java.lang.Math.max(1, w.height);
                  int add = row.isEmpty() ? wW : this.spacing + wW;
                  if (!row.isEmpty() && rowW + add > zoneW) {
                     i--;
                     break;
                  }

                  row.add(w);
                  rowW += add;
                  rowH = java.lang.Math.max(rowH, wH);
               }
            }

            if (!row.isEmpty()) {
               int offsetX = java.lang.Math.max(0, (zoneW - rowW) / 2);
               int x = startX + offsetX;

               for (int k = 0; k < row.size(); k++) {
                  Widget w = row.get(k);
                  int wW = Math.clamp(w.width, 1, zoneW);
                  w.posX = x;
                  w.posY = y;
                  x += wW + this.spacing;
               }

               y += rowH + this.spacing;
            }
         }
      }
   }

   public void updateStats() {
      for (int i = 0; i < this.availableCategoriesButton.size(); i++) {
         ShopSelectCategoriesComponentModalPanel.SelectCategoriesButton button = (ShopSelectCategoriesComponentModalPanel.SelectCategoriesButton)this.availableCategoriesButton
            .get(i);
         button.selected = this.selectedCategories.contains(button.category);
      }
   }

   public void onSearch(String text) {
      String q = ShopUtils.normalize(text);
      if (!Objects.equals(this.lastSearch, q)) {
         boolean showAll = q.isEmpty();

         for (int i = 0; i < this.availableCategoriesButton.size(); i++) {
            ShopSelectCategoriesComponentModalPanel.SelectCategoriesButton button = (ShopSelectCategoriesComponentModalPanel.SelectCategoriesButton)this.availableCategoriesButton
               .get(i);
            boolean visible;
            if (showAll) {
               visible = true;
            } else {
               String name = ShopUtils.normalize(button.getTitle().getString());
               visible = ShopUtils.matchesQuery(name, q);
            }

            button.enabled = visible;
         }

         this.lastSearch = q;
         this.alignWidgets();
      }
   }

   public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
      ShopRenderingWrapper.beginBatch(w, h, 4.0F, 1.0F);
      ShopRenderingWrapper.addBatchRect(graphics, x, y, w, h, GUIShopMenu.BACKGROUND_INT, GUIShopMenu.BORDER_INT);
      ShopRenderingWrapper.endBatch();
   }
}
