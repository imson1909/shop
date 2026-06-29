package net.sixik.sdmshop.client.screen_new.components.categories;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.BaseScreen;
import dev.ftb.mods.ftblibrary.ui.Button;
import dev.ftb.mods.ftblibrary.ui.ModalPanel;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.PanelScrollBar;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.TextField;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.client.screen_new.MainShopScreen;
import net.sixik.sdmshop.client.screen_new.api.GUIShopMenu;
import net.sixik.sdmshop.client.screen_new.api.GUIShopWidgets;
import net.sixik.sdmshop.shop.ShopTab;
import net.sixik.sdmshop.utils.rendering.ShopRenderingWrapper;
import net.sixik.sdmuilib.client.utils.misc.RGBA;

public class ShopSelectCategoriesComponentModalPanel extends ModalPanel {
   protected static final int Offset = 8;
   protected final List<ShopTab> selectedCategories;
   protected final GUIShopWidgets.CategoryBox categoryBox;
   protected final GUIShopWidgets.EditCategoryButton button;
   protected final Panel gui;
   protected TextField titleField;
   protected int spacing = 2;
   protected int borderOffest = 4;
   protected boolean center;
   protected GUIShopWidgets.SearchBox listSearchBox;
   protected ShopSelectCategoryListBox listBox;
   protected PanelScrollBar listBoxScroll;
   protected TextField selectedListBoxTitle;
   protected Button selectedListBoxClearAllButton;
   protected ShopSelectCategorySelectedListBox selectedListBox;
   protected PanelScrollBar selectedListBoxScroll;
   protected Button closeButton;

   public static ShopSelectCategoriesComponentModalPanel openCentered(Panel panel, GUIShopWidgets.CategoryBox categoryBox, GUIShopWidgets.EditCategoryButton b) {
      ShopSelectCategoriesComponentModalPanel modal = openDefault(panel, categoryBox, b);
      modal.center = true;
      int sw = panel.getWidth();
      int sh = panel.getHeight();
      int w = modal.getWidth();
      int h = modal.getHeight();
      modal.setPos((sw - w) / 2, (sh - h) / 2);
      return modal;
   }

   public static ShopSelectCategoriesComponentModalPanel openDefault(
      Panel panel, GUIShopWidgets.CategoryBox categoryBox, GUIShopWidgets.EditCategoryButton button
   ) {
      BaseScreen gui = panel.getGui();
      ShopSelectCategoriesComponentModalPanel modal = new ShopSelectCategoriesComponentModalPanel(panel, categoryBox, button);
      modal.setWidth(gui.width * 2 / 6);
      modal.setHeight(gui.height * 4 / 4);
      gui.pushModalPanel(modal);
      if (MainShopScreen.Instance != null) {
         MainShopScreen.Instance.onModalOpen(modal);
      }

      return modal;
   }

   protected ShopSelectCategoriesComponentModalPanel(Panel panel, GUIShopWidgets.CategoryBox categoryBox, GUIShopWidgets.EditCategoryButton button) {
      super(panel);
      this.gui = panel;
      this.categoryBox = categoryBox;
      this.button = button;
      this.selectedCategories = categoryBox.getSelectedCategories();
   }

   public void addWidgets() {
      this.add(this.titleField = new TextField(this));
      this.add(this.listBox = new ShopSelectCategoryListBox(this, this.selectedCategories, this.borderOffest, this.spacing));
      this.add(this.listSearchBox = new GUIShopWidgets.SearchBox(this, this::onSearch));
      this.add(this.listBoxScroll = new PanelScrollBar(this, this.listBox) {
         public void drawScrollBar(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            SDMShopClient.someColor.draw(graphics, x, y, w, h);
         }

         public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            RGBA.create(0, 0, 0, 127).draw(graphics, x, y, w, h, 0.0F);
         }
      });
      this.add(this.selectedListBoxTitle = new TextField(this));
      this.add(
         this.selectedListBoxClearAllButton = new SimpleTextButton(
            this, Component.m_237115_("sdm.shop.gui.box.categories.edit.selected_list.clear_button"), Icon.empty()
         ) {
            public void onClicked(MouseButton button) {
               ShopSelectCategoriesComponentModalPanel.this.selectedCategories.clear();
               ShopSelectCategoriesComponentModalPanel.this.updateSelectedList();
            }
         }
      );
      this.add(this.selectedListBox = new ShopSelectCategorySelectedListBox(this, this.selectedCategories, this.borderOffest, this.spacing));
      this.add(this.selectedListBoxScroll = new PanelScrollBar(this, this.selectedListBox) {
         public void drawScrollBar(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            SDMShopClient.someColor.draw(graphics, x, y, w, h);
         }

         public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            RGBA.create(0, 0, 0, 127).draw(graphics, x, y, w, h, 0.0F);
         }
      });
      this.add(this.closeButton = new SimpleTextButton(this, Component.m_237115_("sdm.shop.entry.creator.back"), Icons.BACK) {
         public void onClicked(MouseButton button) {
            this.parent.getGui().popModalPanel();
         }
      });
      this.listSearchBox.ghostText = I18n.m_118938_("sdm.shop.gui.box.categories.edit.search", new Object[0]);
   }

   public void alignWidgets() {
      int oW = this.width - 16;
      int fontH = 9;
      this.titleField.setMaxWidth(this.width);
      this.titleField.setText(Component.m_237115_("sdm.shop.gui.box.categories.edit.title"));
      this.titleField.posY = 4;
      this.titleField.posX = (this.width - this.titleField.width) / 2;
      this.listSearchBox.width = oW;
      this.listSearchBox.height = 12;
      this.listSearchBox.posX = 8;
      this.listSearchBox.posY = this.titleField.posY + fontH * 2;
      this.listBox.width = oW;
      this.listBox.height = (int)(this.height / 2.5);
      this.listBox.posX = 8;
      this.listBox.posY = this.listSearchBox.posY + this.listSearchBox.height + fontH;
      this.updateList();
      this.selectedListBoxTitle.setWidth(oW);
      this.selectedListBoxTitle.setText(I18n.m_118938_("sdm.shop.gui.box.categories.edit.selected_list.title", new Object[0]));
      this.selectedListBoxTitle.posX = 8;
      this.selectedListBoxTitle.posY = this.listBox.posY + this.listBox.height + fontH * 2;
      this.selectedListBox.width = oW;
      this.selectedListBox.height = this.height / 4;
      this.selectedListBox.posX = 8;
      this.selectedListBox.posY = this.selectedListBoxTitle.posY + this.selectedListBoxTitle.height + 2;
      this.updateSelectedList();
      this.selectedListBoxClearAllButton.posX = this.width - (this.selectedListBoxClearAllButton.width + 8);
      this.selectedListBoxClearAllButton.posY = this.selectedListBoxTitle.posY;
      this.selectedListBoxClearAllButton.height = this.selectedListBoxTitle.height;
      this.listBoxScroll.setPosAndSize(this.listBox.getPosX() + this.listBox.getWidth() - 2, this.listBox.getPosY(), 2, this.listBox.getHeight());
      this.selectedListBoxScroll
         .setPosAndSize(
            this.selectedListBox.getPosX() + this.selectedListBox.getWidth() - 2, this.selectedListBox.getPosY(), 2, this.selectedListBox.getHeight()
         );
      int freeStartY = this.selectedListBox.posY + this.selectedListBox.height + this.spacing;
      int freeEndY = this.height - 8;
      this.closeButton.posX = (this.width - this.closeButton.width) / 2;
      this.closeButton.posY = freeStartY + Math.max(0, (freeEndY - freeStartY - this.closeButton.height) / 2);
   }

   public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
      ShopRenderingWrapper.beginBatch(w, h, 4.0F, 1.0F);
      ShopRenderingWrapper.addBatchRect(graphics, x, y, w, h, GUIShopMenu.BACKGROUND_INT, GUIShopMenu.BORDER_INT);
      ShopRenderingWrapper.endBatch();
   }

   public void onClosed() {
      this.categoryBox.selectNewCategories(this.selectedCategories);
      if (MainShopScreen.Instance != null) {
         MainShopScreen.Instance.onModalClose(this);
         MainShopScreen.Instance.onFilterApply();
      }

      super.onClosed();
   }

   public void updateList() {
      this.listBox.clearWidgets();
      this.listBox.addWidgets();
      this.listBox.alignWidgets();
      this.updateStatsOnLists();
   }

   public void updateSelectedList() {
      this.selectedListBox.clearWidgets();
      this.selectedListBox.addWidgets();
      this.selectedListBox.alignWidgets();
      this.updateStatsOnLists();
   }

   public void updateStatsOnLists() {
      this.listBox.updateStats();
      this.selectedListBox.updateStats();
   }

   public void onSearch(String text) {
      this.listBox.onSearch(text);
   }

   public static class AddedCategoriesButton extends SimpleTextButton {
      protected final ShopTab tab;

      public AddedCategoriesButton(Panel panel, ShopTab tab) {
         super(panel, tab.title, Icons.CLOSE);
         this.tab = tab;
      }

      public void onClicked(MouseButton button) {
      }

      public void draw(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
         this.drawBackground(graphics, theme, x, y, w, h);
         int padding = 4;
         int s = Math.max(8, h / 2);
         int iconY = y + (h - s) / 2;
         FormattedText title = this.getTitle();
         int textX = x;
         int textY = y + (h - theme.getFontHeight() + 1) / 2;
         int sw = theme.getStringWidth(title);
         int mw = w - 6 - (this.hasIcon() ? 4 + s + 4 : 0);
         if (sw > mw) {
            sw = mw;
            title = theme.trimStringToWidth(title, mw);
         }

         if (this.renderTitleInCenter()) {
            textX += (mw - sw + 6) / 2;
         } else {
            textX += 4;
         }

         if (this.hasIcon()) {
            int iconX = x + w - 4 - s;
            this.drawIcon(graphics, theme, iconX, iconY, s, s);
         }

         theme.drawString(graphics, title, textX, textY, theme.getContentColor(this.getWidgetType()), 2);
      }

      public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
         ShopRenderingWrapper.drawRoundedRectNoBorder(graphics.m_280168_(), x, y, w, h, 5.0F, -14737633);
      }
   }

   public static class SelectCategoriesButton extends GUIShopWidgets.CategoryBox.Button {
      public boolean selected = false;
      public boolean enabled = true;

      public SelectCategoriesButton(Panel panel, ShopTab category, BiConsumer<MouseButton, ShopTab> onClick) {
         super(panel, category, onClick);
      }

      public boolean isEnabled() {
         return this.enabled;
      }

      @Override
      public void draw(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
         if (this.enabled) {
            super.draw(graphics, theme, x, y, w, h);
         }
      }

      @Override
      public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
         if (this.isMouseOver) {
            ShopRenderingWrapper.drawRoundedRect(graphics.m_280168_(), x, y, w, h, 5.0F, 1.0F, -14737633, GUIShopMenu.BORDER_4_INT);
         } else if (this.selected) {
            ShopRenderingWrapper.drawRoundedRect(graphics.m_280168_(), x, y, w, h, 5.0F, 1.0F, -14737633, GUIShopMenu.BORDER_3_INT);
         } else {
            super.drawBackground(graphics, theme, x, y, w, h);
         }
      }
   }
}
