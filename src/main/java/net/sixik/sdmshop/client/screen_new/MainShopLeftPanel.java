package net.sixik.sdmshop.client.screen_new;

import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.Button;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.PanelScrollBar;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.TextBox;
import dev.ftb.mods.ftblibrary.ui.TextField;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.Widget;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import java.util.Collection;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.client.screen_new.api.GUIShopMenu;
import net.sixik.sdmshop.client.screen_new.api.GUIShopWidgets;
import net.sixik.sdmshop.client.screen_new.components.filters.ShopFiltersComponentModalPanel;
import net.sixik.sdmshop.client.screen_new.components.filters.ShopFiltersComponentTypePanel;
import net.sixik.sdmshop.old_api.shop.AbstractEntryType;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.shop.ShopTab;
import net.sixik.sdmshop.shop.sorts.AbstractEntryTypeFilter;
import net.sixik.sdmshop.utils.ShopUtils;
import net.sixik.sdmshop.utils.rendering.ShopRenderingWrapper;
import net.sixik.sdmshop.utils.rendering.widgets.EnumDropdownWidget;
import net.sixik.sdmuilib.client.utils.misc.RGBA;

public class MainShopLeftPanel extends Panel {
   public TextBox searchField;
   public TextField categoryBoxTitle;
   public GUIShopWidgets.EditCategoryButton categoryBoxEditButton;
   public GUIShopWidgets.CategoryBox categoryBox;
   protected double priceFrom = 0.0;
   protected double priceTo = 0.0;
   protected TextField priceTitle;
   protected TextBox priceBoxFrom;
   protected TextBox priceBoxTo;
   protected EnumDropdownWidget<MainShopLeftPanel.CategorySort> sortDropdown;
   protected MainShopToolPanel toolPanel;
   protected PanelScrollBar toolPanelScroll;
   protected Button moreFiltersButton;

   public MainShopLeftPanel(GUIShopMenu screen) {
      super(screen.self());
   }

   public void addWidgets() {
      this.add(this.searchField = new GUIShopWidgets.SearchBox(this, s -> MainShopScreen.Instance.onFilterApply()));
      this.add(this.categoryBoxTitle = new TextField(this));
      this.add(this.categoryBox = new GUIShopWidgets.CategoryBox(this, 4, 2));
      this.add(
         this.categoryBoxEditButton = new GUIShopWidgets.EditCategoryButton(this, Component.m_237115_("sdm.shop.gui.box.categories.edit"), this.categoryBox)
      );
      this.add(this.priceTitle = new TextField(this));
      this.add(this.priceBoxFrom = new TextBox(this) {
         public void onTextChanged() {
            String str = this.getText();
            MainShopLeftPanel.this.priceFrom = str.isEmpty() ? 0.0 : Double.parseDouble(str);
            MainShopScreen.Instance.onFilterApply();
         }
      });
      this.add(this.priceBoxTo = new TextBox(this) {
         public void onTextChanged() {
            String str = this.getText();
            MainShopLeftPanel.this.priceTo = str.isEmpty() ? 0.0 : Double.parseDouble(str);
            MainShopScreen.Instance.onFilterApply();
         }
      });
      this.add(this.moreFiltersButton = new SimpleTextButton(this, Component.m_237115_("sdm.shop.gui.box.categories.filters.title"), Icons.SETTINGS) {
         public void onClicked(MouseButton button) {
            ShopFiltersComponentModalPanel.openCentered(this.getGui());
         }
      });
      this.add(this.toolPanel = new MainShopToolPanel(this));
      this.add(this.toolPanelScroll = new PanelScrollBar(this, this.toolPanel) {
         public void drawScrollBar(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            SDMShopClient.someColor.draw(graphics, x, y, w, h);
         }

         public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            RGBA.create(0, 0, 0, 127).draw(graphics, x, y, w, h, 0.0F);
         }
      });
      this.priceBoxFrom.setFilter(ShopUtils.ONLY_DIGITS);
      this.priceBoxFrom.ghostText = "From";
      this.priceBoxTo.setFilter(ShopUtils.ONLY_DIGITS);
      this.priceBoxTo.ghostText = "To";
   }

   private void applySort(MainShopLeftPanel.CategorySort mode) {
   }

   public void alignWidgets() {
      int elementSize = this.width - this.width / 6;
      int centerPosElements = (this.width - elementSize) / 2;
      int maxCategoryH = this.height / 3;
      int fontH = 9;
      int fontHD = fontH / 2;
      int xOffset = 6;
      int xOffsetM = 12;
      this.searchField.setWidth(elementSize);
      this.searchField.setHeight(12);
      this.searchField.posX = centerPosElements;
      this.searchField.posY = this.searchField.posY + this.searchField.height / 6;
      this.categoryBoxTitle.setMaxWidth(elementSize);
      this.categoryBoxTitle.setText(Component.m_237115_("sdm.shop.gui.box.categories.title"));
      this.categoryBoxTitle.posX = 6;
      this.categoryBoxTitle.posY = this.categoryBoxTitle.posY + this.searchField.posY + 4;
      this.categoryBoxEditButton.posX = this.width - this.categoryBoxEditButton.width - 4;
      this.categoryBoxEditButton.posY = this.categoryBoxTitle.posY;
      this.categoryBoxEditButton.height = this.categoryBoxTitle.height;
      this.categoryBox.setWidth(elementSize);
      this.categoryBox.setHeight(50);
      this.categoryBox.posX = centerPosElements;
      this.categoryBox.posY = this.categoryBox.posY + (this.categoryBoxTitle.posY - this.categoryBoxTitle.height / 3);
      this.categoryBox.addWidgets();
      this.categoryBox.alignWidgets();
      this.categoryBox.height = maxCategoryH;
      int priceTitleOffset = 2;
      this.priceTitle.setWidth(elementSize);
      this.priceTitle.setText(Component.m_237115_("sdm.shop.entry.seller_type.price"));
      this.priceTitle.posX = 6;
      this.priceTitle.posY = this.categoryBox.posY + this.categoryBox.height + fontH + 2;
      int priceBW = this.width / 4;
      this.priceBoxFrom.setWidth(priceBW);
      this.priceBoxFrom.setHeight(12);
      this.priceBoxFrom.posY = this.priceTitle.posY - 2;
      this.priceBoxTo.setWidth(this.priceBoxFrom.width);
      this.priceBoxTo.setHeight(this.priceBoxFrom.height);
      this.priceBoxTo.posY = this.priceBoxFrom.posY;
      int priceWW = this.priceBoxFrom.width + this.priceBoxTo.width + this.width / 10;
      this.priceBoxFrom.posX = this.width - priceWW;
      this.priceBoxTo.posX = this.priceBoxFrom.posX + this.priceBoxFrom.width + 2;
      this.moreFiltersButton.setWidth(this.width - 12);
      this.moreFiltersButton.setHeight(12);
      this.moreFiltersButton.posX = (this.width - this.moreFiltersButton.width) / 2;
      this.moreFiltersButton.posY = this.priceBoxTo.posY + this.priceBoxTo.height + fontHD;
      this.toolPanel.width = this.categoryBox.width;
      this.toolPanel.height = this.categoryBox.height - this.categoryBox.height / 4;
      this.toolPanel.posX = this.categoryBox.posX;
      this.toolPanel.posY = this.moreFiltersButton.posY + this.moreFiltersButton.height + fontH;
      this.toolPanelScroll.setPosAndSize(this.toolPanel.getPosX() + this.toolPanel.getWidth() - 2, this.toolPanel.getPosY(), 2, this.toolPanel.getHeight());
      this.toolPanel.clearWidgets();
      this.toolPanel.addWidgets();
      this.toolPanel.alignWidgets();
   }

   public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
      ShopRenderingWrapper.beginBatch(w, h, 4.0F, 1.0F);
      ShopRenderingWrapper.addBatchRect(graphics, x, y, w, h, GUIShopMenu.BACKGROUND_INT, GUIShopMenu.BORDER_INT);
      ShopRenderingWrapper.endBatch();
   }

   public boolean isSearched(ShopEntry entry) {
      List<ShopTab> selectedCategory = this.categoryBox.getSelectedCategories();
      if (!selectedCategory.isEmpty()) {
         boolean find = false;

         for (int i = 0; i < selectedCategory.size(); i++) {
            if (selectedCategory.get(i).getId().equals(entry.getTab())) {
               find = true;
               break;
            }
         }

         if (!find) {
            return false;
         }
      }

      if (entry.getPrice() < this.priceFrom) {
         return false;
      }

      if (this.priceTo != 0.0 && entry.getPrice() > this.priceTo) {
         return false;
      }

      String searchTxt = this.searchField.getText();
      if (!searchTxt.isEmpty() && !entry.getEntryType().isSearch(this.searchField.getText())) {
         return false;
      }

      ShopTab shopTab = SDMShopClient.CurrentShop.getTab(entry.getTab());
      List<AbstractEntryTypeFilter<? extends AbstractEntryType>> filters = SDMShopClient.shopFilters
         .getOrDefault(entry.getEntryType().getClass(), ShopFiltersComponentTypePanel.NULL);
      if (!filters.isEmpty()) {
         for (int i = 0; i < filters.size(); i++) {
            if (!filters.get(i).sorting(entry, shopTab, entry.getEntryType())) {
               return false;
            }
         }
      }

      return true;
   }

   public void sortEntries(Collection<Widget> shopEntryButtons) {
   }

   public enum CategorySort {
      NONE,
      PRICE_ASC,
      PRICE_DESC,
      NAME_ASC,
      NAME_DESC;
   }
}
