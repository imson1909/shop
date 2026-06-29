package net.sixik.sdmshop.client.screen_new.components.creator.entry;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.TextField;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sixik.sdmshop.client.screen_new.api.GUIShopMenu;
import net.sixik.sdmshop.client.screen_new.components.creator.ShopCreatorComponentModalPanel;
import net.sixik.sdmshop.client.screen_new.components.creator.data.ShopCreatorComponentData;
import net.sixik.sdmshop.shop.ShopTab;
import net.sixik.v2.render.RenderHelper;

public class ShopCreatorEntrySelectedCategory extends Panel {
   protected final ShopCreatorEntryPanel entryPanel;
   protected ShopCreatorEntrySelectedCategory.CategoryButton categoryButton;
   protected TextField selectCategoryText;

   public ShopCreatorEntrySelectedCategory(ShopCreatorEntryPanel panel) {
      super(panel);
      this.entryPanel = panel;
      this.setHeight(22);
   }

   public void addWidgets() {
      this.add(this.selectCategoryText = new TextField(this));
      this.selectCategoryText.setText(Component.m_237115_("sdm.shop.gui.creator.text.selected_category"));
      this.add(this.categoryButton = new ShopCreatorEntrySelectedCategory.CategoryButton(this));
   }

   public void alignWidgets() {
      this.categoryButton.setTab(ShopCreatorComponentData.Data.Entry.selectedTab);
      this.selectCategoryText.posY = (this.height - 9) / 2;
      this.selectCategoryText.posX = 2;
      this.categoryButton.posX = this.width - this.categoryButton.width - 2;
      this.categoryButton.posY = 1;
   }

   public boolean shouldDraw() {
      return ShopCreatorComponentModalPanel.Data.Entry.selectedType != null;
   }

   public static boolean isTabSelected() {
      return ShopCreatorComponentData.Data.Entry.selectedTab != null;
   }

   public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
      RenderHelper.drawHollowRect(graphics, x, y, w, h, GUIShopMenu.BORDER, false);
   }

   public static class CategoryButton extends SimpleTextButton {
      protected final ShopCreatorEntrySelectedCategory entrySelectedCategory;

      public CategoryButton(ShopCreatorEntrySelectedCategory panel) {
         super(panel, Component.m_237115_("sdm.shop.gui.creator.text.select_category"), Icons.BOOK_RED);
         this.entrySelectedCategory = panel;
      }

      public ShopCreatorEntrySelectedCategory.CategoryButton setTab(ShopTab tab) {
         if (tab != null) {
            this.title = tab.title;
            this.icon = Icon.empty();
         }

         return this;
      }

      public CategoryButton(ShopCreatorEntrySelectedCategory panel, ShopTab tab) {
         this(panel);
         this.setTab(tab);
      }

      public void addMouseOverText(TooltipList list) {
         list.add(Component.m_237115_("sdm.shop.gui.creator.button.select_category.tooltip"));
      }

      public void onClicked(MouseButton button) {
         new ShopCreatorSelectCategoryScreen(this.entrySelectedCategory.entryPanel::alignWidgetsWithoutEntryTypes).openGui();
      }
   }
}
