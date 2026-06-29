package net.sixik.sdmshop.client.screen_new.components.filters;

import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.Widget;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import java.util.List;
import java.util.function.Function;
import net.minecraft.client.gui.GuiGraphics;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.client.screen_new.api.GUIShopMenu;
import net.sixik.sdmshop.old_api.shop.AbstractEntryType;
import net.sixik.sdmshop.registers.ShopContentRegister;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.shop.sorts.AbstractEntryTypeFilter;
import net.sixik.sdmshop.utils.rendering.ShopRenderingWrapper;

public class ShopFiltersComponentTypePanel extends Panel {
   public static final List<AbstractEntryTypeFilter<? extends AbstractEntryType>> NULL = List.of();
   protected final ShopFiltersComponentModalPanel modalPanel;
   public ShopFiltersComponentTypePanel.Button selectedTypeButton;

   public ShopFiltersComponentTypePanel(ShopFiltersComponentModalPanel panel) {
      super(panel);
      this.modalPanel = panel;
   }

   public void addWidgets() {
      ShopEntry entry = new ShopEntry(SDMShopClient.CurrentShop);

      for (Function<ShopEntry, AbstractEntryType> value : ShopContentRegister.getEntryTypes().values()) {
         AbstractEntryType type = value.apply(entry);
         if (type != null) {
            List<AbstractEntryTypeFilter<? extends AbstractEntryType>> filter = SDMShopClient.shopFilters.getOrDefault(type.getClass(), NULL);
            if (!filter.isEmpty()) {
               ShopFiltersComponentTypePanel.Button button = new ShopFiltersComponentTypePanel.Button(this, type, filter);
               this.add(button);
               this.onSelected(button);
            }
         }
      }
   }

   public void alignWidgets() {
      int wH = this.width - 4;
      List<Widget> list = this.getWidgets();
      int offsetY = 4;

      for (int i = 0; i < list.size(); i++) {
         Widget w = list.get(i);
         w.setHeight(12);
         w.setPos(2, 4 + i * (w.getHeight() + 2));
         w.setWidth(wH);
      }
   }

   public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
      ShopRenderingWrapper.beginBatch(w, h, 4.0F, 1.0F);
      ShopRenderingWrapper.addBatchRect(graphics, x, y, w, h, GUIShopMenu.BACKGROUND_INT, GUIShopMenu.BORDER_INT);
      ShopRenderingWrapper.endBatch();
   }

   public void onSelected(ShopFiltersComponentTypePanel.Button button) {
      List<Widget> list = this.getWidgets();

      for (int i = 0; i < list.size(); i++) {
         Widget widget = list.get(i);
         if (widget instanceof ShopFiltersComponentTypePanel.Button b && widget == button) {
            if (!b.selected) {
               b.selected = true;
               this.selectedTypeButton = b;
               this.modalPanel.onSelected(this.selectedTypeButton);
            }
         } else if (widget instanceof ShopFiltersComponentTypePanel.Button b) {
            b.selected = false;
         }
      }
   }

   public ShopFiltersComponentTypePanel.Button getSelectedTypeButton() {
      return this.selectedTypeButton;
   }

   public static class Button extends SimpleTextButton {
      protected final ShopFiltersComponentTypePanel typePanel;
      protected final AbstractEntryType type;
      protected final List<AbstractEntryTypeFilter<? extends AbstractEntryType>> filter;
      protected boolean selected = false;

      public Button(ShopFiltersComponentTypePanel panel, AbstractEntryType type, List<AbstractEntryTypeFilter<? extends AbstractEntryType>> filter) {
         super(panel, type.getTranslatableForCreativeMenu(), type.getCreativeIcon());
         this.typePanel = panel;
         this.type = type;
         this.filter = filter;
      }

      public ShopFiltersComponentTypePanel.Button setSelected(boolean value) {
         this.selected = value;
         return this;
      }

      public List<AbstractEntryTypeFilter<? extends AbstractEntryType>> getFilter() {
         return this.filter;
      }

      public ShopFiltersComponentTypePanel getTypePanel() {
         return this.typePanel;
      }

      public AbstractEntryType getType() {
         return this.type;
      }

      public void onClicked(MouseButton button) {
         this.typePanel.onSelected(this);
      }

      public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
         if (!this.isMouseOver && !this.selected) {
            ShopRenderingWrapper.drawRoundedRectNoBorder(graphics.m_280168_(), x, y, w, h, 4.0F, GUIShopMenu.BACKGROUND_INT);
         } else {
            ShopRenderingWrapper.drawRoundedRect(graphics.m_280168_(), x, y, w, h, 4.0F, 1.0F, GUIShopMenu.BACKGROUND_INT, GUIShopMenu.BORDER_3_INT);
         }
      }
   }
}
