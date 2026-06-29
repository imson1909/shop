package net.sixik.sdmshop.client.screen_new.components.filters;

import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.TextField;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.Widget;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.sixik.sdmshop.client.screen_new.api.FilterPanelWidget;
import net.sixik.sdmshop.client.screen_new.api.FilterRefreshWidget;
import net.sixik.sdmshop.client.screen_new.api.GUIShopMenu;
import net.sixik.sdmshop.old_api.shop.AbstractEntryType;
import net.sixik.sdmshop.shop.sorts.AbstractEntryTypeFilter;
import net.sixik.sdmshop.utils.rendering.ShopRenderingWrapper;
import org.jetbrains.annotations.Nullable;

public class ShopFiltersComponentConfigPanel extends Panel {
   protected final ShopFiltersComponentModalPanel modalPanel;
   @Nullable
   protected ShopFiltersComponentTypePanel.Button selectedButton;

   public ShopFiltersComponentConfigPanel(ShopFiltersComponentModalPanel panel) {
      super(panel);
      this.modalPanel = panel;
   }

   public void addWidgets() {
      if (this.selectedButton != null) {
         List<AbstractEntryTypeFilter<? extends AbstractEntryType>> list = this.selectedButton.getFilter();

         for (int i = 0; i < list.size(); i++) {
            final AbstractEntryTypeFilter<?> element = list.get(i);
            TextField field = new TextField(this) {
               public void addMouseOverText(TooltipList list) {
                  element.addTooltips(list);
               }
            };
            field.setText(element.getTitle());
            this.add(field);
            element.addWidget(this);
         }
      }
   }

   public void alignWidgets() {
      if (this.selectedButton != null) {
         int paddingX = 8;
         int paddingY = 8;
         int gap = 6;
         int rowGap = 6;
         int titleToPanelGap = 4;
         int zoneW = this.width - 16;
         int y = 8;
         List<Widget> ws = this.getWidgets();
         if (!ws.isEmpty() && zoneW > 0) {
            int labelW = 0;

            for (int i = 0; i + 1 < ws.size(); i += 2) {
               Widget elem = ws.get(i + 1);
               if (!(elem instanceof FilterPanelWidget)) {
                  Widget label = ws.get(i);
                  labelW = Math.max(labelW, Math.max(1, label.width));
               }
            }

            labelW = Math.min(labelW, Math.max(80, (int)(zoneW * 0.45F)));
            int elementW = Math.max(1, zoneW - labelW - 6);
            int rowH = 12;

            for (int i = 0; i + 1 < ws.size(); i += 2) {
               Widget elem = ws.get(i + 1);
               if (!(elem instanceof FilterPanelWidget)) {
                  Widget label = ws.get(i);
                  rowH = Math.max(rowH, Math.max(label.height, elem.height));
               }
            }

            for (int i = 0; i + 1 < ws.size(); i += 2) {
               Widget label = ws.get(i);
               Widget elem = ws.get(i + 1);
               if (elem instanceof FilterPanelWidget fpw) {
                  label.setWidth(zoneW);
                  label.setHeight(Math.max(12, label.height));
                  label.posX = 8;
                  label.posY = y;
                  y += label.height + 4;
                  int ph = Math.max(1, fpw.getPanelHeight(this.height));
                  elem.setWidth(zoneW);
                  elem.setHeight(ph);
                  elem.posX = 8;
                  elem.posY = y;
                  if (elem instanceof FilterRefreshWidget refreshWidget) {
                     refreshWidget.updateWidget();
                  } else if (elem instanceof Panel p) {
                     p.clearWidgets();
                     p.addWidgets();
                     p.alignWidgets();
                  }

                  y += ph + 6;
               } else {
                  label.setWidth(labelW);
                  label.setHeight(rowH);
                  label.posX = 8;
                  label.posY = y + 2;
                  elem.setWidth(elementW);
                  elem.setHeight(rowH);
                  elem.posX = 8 + labelW + 6;
                  elem.posY = y;
                  if (elem instanceof FilterRefreshWidget refreshWidget) {
                     refreshWidget.updateWidget();
                  } else if (elem instanceof Panel p) {
                     p.alignWidgets();
                  }

                  y += rowH + 6;
               }
            }
         }
      }
   }

   public void onSelected(ShopFiltersComponentTypePanel.Button button) {
      this.selectedButton = button;
      this.clearWidgets();
      this.addWidgets();
      this.alignWidgets();
   }

   public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
      ShopRenderingWrapper.beginBatch(w, h, 4.0F, 1.0F);
      ShopRenderingWrapper.addBatchRect(graphics, x, y, w, h, GUIShopMenu.BACKGROUND_INT, GUIShopMenu.BORDER_INT);
      ShopRenderingWrapper.endBatch();
   }
}
