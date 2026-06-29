package net.sixik.sdmshop.client.screen.modern;

import dev.ftb.mods.ftblibrary.ui.PanelScrollBar;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.Widget;
import dev.ftb.mods.ftblibrary.ui.ScrollBar.Plane;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.sixik.sdmshop.api.ShopBase;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.client.screen.base.AbstractShopScreen;
import net.sixik.sdmshop.client.screen.base.widgets.AbstractShopEntryButton;
import net.sixik.sdmshop.client.screen.modern.buyer.ModernBuyerScreen;
import net.sixik.sdmshop.client.screen.modern.create_entry.ModernCreateEntryScreen;
import net.sixik.sdmshop.client.screen.modern.panels.ModernShopEntriesPanel;
import net.sixik.sdmshop.client.screen.modern.panels.ModernShopPanels;
import net.sixik.sdmshop.client.screen.modern.panels.ModernShopTabPanel;
import net.sixik.sdmshop.client.screen.modern.widgets.ModernShopEntryButton;
import net.sixik.sdmshop.client.screen.modern.widgets.ModernShopTabButton;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.shop.ShopTab;
import net.sixik.sdmshop.utils.ShopUtils;
import net.sixik.sdmuilib.client.utils.math.Vector2;
import net.sixik.sdmuilib.client.utils.misc.RGBA;

public class ModernShopScreen extends AbstractShopScreen {
   public static final Vector2 DEFAULT_ENTRY_BUTTON_SIZE = new Vector2(38, 38);
   protected ModernShopPanels.TopEntriesPanel topEntriesPanel;
   protected ModernShopPanels.TopPanel topPanel;
   protected ModernShopPanels.BottomPanel bottomPanel;

   @Override
   public void addWidgets() {
   }

   public void alignWidgets() {
      this.onRefresh();
   }

   @Override
   public boolean _init() {
      this.setWidth(this.getScreen().m_85445_() * 4 / 5);
      this.setHeight(this.getScreen().m_85446_() * 4 / 5);
      this.closeContextMenu();
      return super._init();
   }

   @Override
   public void addEntriesButtons() {
      if (this.selectedTab != null) {
         List<AbstractShopEntryButton> widgets = new ArrayList<>();
         boolean isClientEdit = ShopUtils.isEditModeClient();
         boolean showBuyedEntries = this.currentShop.getParams().showEntryWitchCantBuy();

         for (ShopEntry entry : this.currentShop.getEntriesByTab(this.selectedTab)) {
            boolean canExecute = entry.getEntryType().canExecute(Minecraft.m_91087_().f_91074_, entry, 1);
            boolean needHide = showBuyedEntries && !canExecute;
            boolean needHideClient = SDMShopClient.userData.showEntryWitchCantBuy(this.currentShop.getId()) && !canExecute;
            if (!needHide && !needHideClient || isClientEdit) {
               boolean locked = entry.isLockedAll(entry);
               boolean limited = entry.isLimitReached(Minecraft.m_91087_().f_91074_);
               boolean searched = this.searchField.isEmpty() || entry.getEntryType().isSearch(this.searchField);
               if ((!locked && !limited || isClientEdit) && searched) {
                  ModernShopEntryButton button = new ModernShopEntryButton(this.entryPanel, entry);
                  button.setSize(DEFAULT_ENTRY_BUTTON_SIZE.x, DEFAULT_ENTRY_BUTTON_SIZE.y);
                  widgets.add(button);
               }
            }
         }

         widgets.sort(Comparator.comparing(AbstractShopEntryButton::isFavorite).reversed());
         if (isClientEdit) {
            ModernShopEntryButton button = new ModernShopEntryButton(this.entryPanel, null, true);
            button.setSize(DEFAULT_ENTRY_BUTTON_SIZE.x, DEFAULT_ENTRY_BUTTON_SIZE.y);
            widgets.add(button);
         }

         this.calculatePositions(widgets);
         this.entryPanel.getWidgets().clear();
         this.entryPanel.addAll(widgets);
         this.scrollEntryPanel.setValue(this.entryScrollPos);
      }
   }

   public void calculatePositions(List<AbstractShopEntryButton> entryButtons) {
      int maxElementsOnScreen = this.getCountInArray() - 1;
      int x = this.getStartPosX(maxElementsOnScreen);
      int y = 2;

      for (int i = 0; i < entryButtons.size(); i++) {
         AbstractShopEntryButton shopEntryButton = entryButtons.get(i);
         if (i > 0) {
            if (i % maxElementsOnScreen == 0) {
               y += DEFAULT_ENTRY_BUTTON_SIZE.y + 6 + 8;
               x = this.getStartPosX(maxElementsOnScreen);
            } else {
               x += DEFAULT_ENTRY_BUTTON_SIZE.x + 3;
            }

            shopEntryButton.setPos(x, y);
         } else {
            shopEntryButton.setPos(x, y);
         }
      }

      var d1 = new ModernShopEntryButton(this.entryPanel, null) {
         @Override
         public void draw(GuiGraphics graphics, Theme theme, int xx, int yx, int w, int h) {
         }

         public boolean checkMouseOver(int mouseX, int mouseY) {
            return false;
         }
      };
      d1.setPos(0, y + 40);
      entryButtons.add(d1);
   }

   public int getCountInArray() {
      int x1 = 0;
      int x = DEFAULT_ENTRY_BUTTON_SIZE.x;

      for (int i = 0; i < 1000; i++) {
         x1 = x * i + 3 * i;
         if (x1 > this.entryPanel.width) {
            return i - 1;
         }
      }

      return 0;
   }

   public int getStartPosX(int count) {
      int x = DEFAULT_ENTRY_BUTTON_SIZE.x;
      int x1 = 0;

      for (int i = 0; i < count + 1; i++) {
         x1 = x * i + 3 * i;
      }

      return this.entryPanel.width / 2 - x1 / 2;
   }

   @Override
   public void addTabsButtons() {
      List<Widget> widgetList = new ArrayList<>();
      boolean isEditMode = ShopUtils.isEditModeClient();
      int y = 0;

      for (ShopTab shopTab : this.currentShop.getTabs()) {
         if (!shopTab.isLockedAll(shopTab) && !shopTab.isLimitReached(Minecraft.m_91087_().f_91074_) || isEditMode) {
            ModernShopTabButton button = new ModernShopTabButton(this.tabPanel, shopTab);
            button.setSize(this.tabPanel.width - 2 - this.getScrollbarWidth(), 15);
            button.setPos(0, y);
            widgetList.add(button);
            y += button.height + 3;
         }
      }

      if (isEditMode) {
         ModernShopTabButton button = new ModernShopTabButton(this.tabPanel, null, true);
         button.setSize(this.tabPanel.width - 2 - this.getScrollbarWidth(), 15);
         button.setPos(0, y);
         widgetList.add(button);
      }

      this.tabPanel.getWidgets().clear();
      this.tabPanel.addAll(widgetList);
      this.scrollTabPanel.setValue(0.0);
   }

   private int getScrollbarWidth() {
      return 2;
   }

   @Override
   public void _onRefresh() {
      this.selectTab(this.selectedTab, false);
      this.getWidgets().clear();
      this.add(this.tabPanel = new ModernShopTabPanel(this));
      this.add(this.scrollTabPanel = new PanelScrollBar(this, Plane.VERTICAL, this.tabPanel) {
         public void drawScrollBar(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            SDMShopClient.someColor.draw(graphics, x, y, w, h);
         }

         public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            RGBA.create(0, 0, 0, 127).draw(graphics, x, y, w, h, 0.0F);
         }
      });
      this.add(this.bottomPanel = new ModernShopPanels.BottomPanel(this));
      this.add(this.topPanel = new ModernShopPanels.TopPanel(this));
      this.add(this.entryPanel = new ModernShopEntriesPanel(this));
      this.add(this.scrollEntryPanel = new PanelScrollBar(this, Plane.VERTICAL, this.entryPanel) {
         public void drawScrollBar(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            SDMShopClient.someColor.draw(graphics, x, y, w, h);
         }

         public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            RGBA.create(0, 0, 0, 127).draw(graphics, x, y, w, h, 0.0F);
         }
      });
      this.add(this.topEntriesPanel = new ModernShopPanels.TopEntriesPanel(this));
      this.tabPanel.setY(this.height / 7);
      this.tabPanel.setSize(this.width / 5, this.height - this.height / 7 * 2);
      this.topPanel.setPos(0, 0);
      this.topPanel.setSize(this.tabPanel.width, this.height / 7 - 2);
      this.topPanel.addWidgets();
      this.topPanel.alignWidgets();
      this.bottomPanel.setPos(0, this.tabPanel.getPosY() + 2 + this.tabPanel.height);
      this.bottomPanel.setSize(this.tabPanel.width, this.height / 7 - 2);
      this.bottomPanel.addWidgets();
      this.bottomPanel.alignWidgets();
      this.entryPanel.setPos(this.tabPanel.width + 2, this.tabPanel.posY);
      this.entryPanel.setSize(this.width - this.tabPanel.width - 2, this.height - this.topPanel.height - 2);
      this.topEntriesPanel.setPos(this.tabPanel.width + 2, 0);
      this.topEntriesPanel.setSize(this.width - this.tabPanel.width - 2, this.topPanel.height);
      this.topEntriesPanel.addWidgets();
      this.topEntriesPanel.alignWidgets();
      this.scrollTabPanel
         .setPosAndSize(
            this.tabPanel.getPosX() + this.tabPanel.getWidth() - this.getScrollbarWidth(),
            this.tabPanel.getPosY(),
            this.getScrollbarWidth(),
            this.tabPanel.getHeight()
         );
      this.scrollEntryPanel
         .setPosAndSize(
            this.entryPanel.getPosX() + this.entryPanel.getWidth() - this.getScrollbarWidth(),
            this.entryPanel.getPosY(),
            this.getScrollbarWidth(),
            this.entryPanel.getHeight() - 10
         );
      this.addTabsButtons();
      this.addEntriesButtons();
   }

   @Override
   public void handle(ShopBase base) {
      this.onRefresh();
   }

   @Override
   public void openBuyScreen(AbstractShopEntryButton entry) {
      new ModernBuyerScreen(this, entry).openGui();
   }

   @Override
   public void openCreateEntryScreen() {
      new ModernCreateEntryScreen(this).openGui();
   }
}
