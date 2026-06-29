package net.sixik.sdmshop.client.screen_new.components.creator;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.ui.BaseScreen;
import dev.ftb.mods.ftblibrary.ui.ModalPanel;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.PanelScrollBar;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.client.screen_new.MainShopScreen;
import net.sixik.sdmshop.client.screen_new.api.GUIShopMenu;
import net.sixik.sdmshop.client.screen_new.components.creator.category.ShopCreatorCategoryPanel;
import net.sixik.sdmshop.client.screen_new.components.creator.data.SelectedCreatorEnum;
import net.sixik.sdmshop.client.screen_new.components.creator.data.ShopCreatorComponentData;
import net.sixik.sdmshop.client.screen_new.components.creator.entry.ShopCreatorEntryPanel;
import net.sixik.sdmshop.client.screen_new.components.creator.entry.ShopCreatorEntrySelectedCategory;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.shop.ShopTab;
import net.sixik.sdmshop.utils.ShopUtilsClient;
import net.sixik.sdmshop.utils.rendering.ShopRenderingWrapper;
import net.sixik.sdmuilib.client.utils.misc.RGBA;

public class ShopCreatorComponentModalPanel extends ModalPanel {
   public static ShopCreatorComponentData Data;
   protected boolean center;
   protected ShopCreatorComponentModalPanel.Button entryCreator;
   protected ShopCreatorComponentModalPanel.Button categoryCreator;
   protected ShopCreatorComponentModalPanel.Button invalidateCacheButton;
   protected ShopCreatorComponentModalPanel.Button createButton;
   protected ShopCreatorComponentModalPanel.Button cancelButton;
   protected ShopCreatorEntryPanel entryContentPanel;
   protected PanelScrollBar entryContentPanelScroll;
   protected ShopCreatorCategoryPanel categoryContentPanel;
   protected PanelScrollBar categoryContentPanelScroll;
   protected ShopTab shopTab = new ShopTab(SDMShopClient.CurrentShop);
   protected ShopEntry shopEntry = new ShopEntry(SDMShopClient.CurrentShop, this.shopTab.getId());

   protected ShopCreatorComponentModalPanel(Panel panel) {
      super(panel);
      Data = ShopCreatorComponentData.Data;
   }

   public void addWidgets() {
      this.add(this.entryCreator = new ShopCreatorComponentModalPanel.Button(this, Component.m_237113_("Entry")) {
         public void onClicked(MouseButton button) {
            ShopCreatorComponentModalPanel.Data.SelectedCreator = SelectedCreatorEnum.Entry;
            ShopCreatorComponentModalPanel.this.refreshWidgets();
         }

         @Override
         public boolean isSelected() {
            return ShopCreatorComponentModalPanel.Data.SelectedCreator == SelectedCreatorEnum.Entry;
         }
      });
      this.add(this.categoryCreator = new ShopCreatorComponentModalPanel.Button(this, Component.m_237113_("Category")) {
         public void onClicked(MouseButton button) {
            ShopCreatorComponentModalPanel.Data.SelectedCreator = SelectedCreatorEnum.Category;
            ShopCreatorComponentModalPanel.this.refreshWidgets();
         }

         @Override
         public boolean isSelected() {
            return ShopCreatorComponentModalPanel.Data.SelectedCreator == SelectedCreatorEnum.Category;
         }
      });
      this.add(
         this.createButton = new ShopCreatorComponentModalPanel.Button(this, Component.m_237113_("Create")) {
            @Override
            public boolean isSelected() {
               return false;
            }

            public void onClicked(MouseButton button) {
               if (ShopCreatorComponentModalPanel.Data.SelectedCreator == SelectedCreatorEnum.Entry) {
                  ShopCreatorComponentModalPanel.this.onCreateEntry();
               } else {
                  ShopCreatorComponentModalPanel.this.onCreateCategory();
               }
            }

            public boolean shouldDraw() {
               return ShopCreatorComponentModalPanel.Data.SelectedCreator == SelectedCreatorEnum.Entry
                     && ShopCreatorEntrySelectedCategory.isTabSelected()
                     && ShopCreatorComponentModalPanel.Data.Entry.selectedType != null
                  || ShopCreatorComponentModalPanel.Data.SelectedCreator == SelectedCreatorEnum.Category;
            }
         }
      );
      this.add(this.cancelButton = new ShopCreatorComponentModalPanel.Button(this, Component.m_237113_("Cancel")) {
         @Override
         public boolean isSelected() {
            return false;
         }

         public void onClicked(MouseButton button) {
            this.getGui().popModalPanel();
         }
      });
      if (Data.SelectedCreator == SelectedCreatorEnum.Entry) {
         this.add(this.entryContentPanel = new ShopCreatorEntryPanel(this));
         this.add(this.entryContentPanelScroll = new PanelScrollBar(this, this.entryContentPanel) {
            public void drawScrollBar(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
               SDMShopClient.someColor.draw(graphics, x, y, w, h);
            }

            public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
               RGBA.create(0, 0, 0, 127).draw(graphics, x, y, w, h, 0.0F);
            }
         });
      } else {
         this.add(this.categoryContentPanel = new ShopCreatorCategoryPanel(this));
         this.add(this.categoryContentPanelScroll = new PanelScrollBar(this, this.categoryContentPanel) {
            public void drawScrollBar(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
               SDMShopClient.someColor.draw(graphics, x, y, w, h);
            }

            public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
               RGBA.create(0, 0, 0, 127).draw(graphics, x, y, w, h, 0.0F);
            }
         });
      }
   }

   public void alignWidgets() {
      int space = 6;
      int typeButtonsZone = this.width - 12;
      this.entryCreator.setWidth(typeButtonsZone / 2 - 3);
      this.categoryCreator.setWidth(this.entryCreator.width);
      this.entryCreator.setPos(6, 2);
      this.categoryCreator.setPos(6 + typeButtonsZone - this.categoryCreator.width, 2);
      this.entryCreator.setHeight(20);
      this.categoryCreator.setHeight(20);
      int fontH = Theme.DEFAULT.getFontHeight();
      int upperSize = this.categoryCreator.posY + this.categoryCreator.height + fontH / 2;
      if (Data.SelectedCreator == SelectedCreatorEnum.Entry) {
         this.entryContentPanel.width = this.width;
         this.entryContentPanel.posY = upperSize;
         this.entryContentPanel.height = this.height - 48;
         this.entryContentPanel.clearWidgets();
         this.entryContentPanel.addWidgets();
         this.entryContentPanel.alignWidgets();
         this.entryContentPanelScroll
            .setPosAndSize(
               this.entryContentPanel.getPosX() + this.entryContentPanel.getWidth() - 2,
               this.entryContentPanel.getPosY(),
               2,
               this.entryContentPanel.getHeight()
            );
      } else {
         this.categoryContentPanel.width = this.width;
         this.categoryContentPanel.posY = upperSize;
         this.categoryContentPanel.height = this.height - 48;
         this.categoryContentPanel.clearWidgets();
         this.categoryContentPanel.addWidgets();
         this.categoryContentPanel.alignWidgets();
         this.categoryContentPanelScroll
            .setPosAndSize(
               this.categoryContentPanel.getPosX() + this.categoryContentPanel.getWidth() - 2,
               this.categoryContentPanel.getPosY(),
               2,
               this.categoryContentPanel.getHeight()
            );
      }

      this.cancelButton.setWidth(this.entryCreator.width);
      this.cancelButton.posX = this.entryCreator.posX;
      this.cancelButton.posY = this.height - (this.cancelButton.height + 4);
      this.createButton.setWidth(this.categoryCreator.width);
      this.createButton.posX = this.categoryCreator.posX;
      this.createButton.posY = this.cancelButton.posY;
   }

   public void onSelectEntryType() {
      this.clearWidgets();
      this.addWidgets();
      this.alignWidgets();
   }

   public void onClosed() {
      if (MainShopScreen.Instance != null) {
         MainShopScreen.Instance.onModalClose(this);
      }

      super.onClosed();
   }

   public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
      ShopRenderingWrapper.beginBatch(w, h, 4.0F, 1.0F);
      ShopRenderingWrapper.addBatchRect(graphics, x, y, w, h, GUIShopMenu.BACKGROUND_INT, GUIShopMenu.BORDER_INT);
      ShopRenderingWrapper.endBatch();
   }

   public void onCreateEntry() {
      ShopEntry entry = ShopCreatorEntryPanel.shopEntry;
      if (entry != null) {
         entry.setTab(Data.Entry.selectedTab.getId());
         ShopUtilsClient.addEntry(entry.getOwnerShop(), entry);
         this.getGui().popModalPanel();
         this.refreshAll();
      }
   }

   public void onCreateCategory() {
      ShopTab tab = ShopCreatorCategoryPanel.shopTab;
      if (tab != null) {
         ShopUtilsClient.addTab(tab.getOwnerShop(), tab);
         this.getGui().popModalPanel();
         this.refreshAll();
      }
   }

   public void refreshAll() {
      BaseScreen gui = this.getGui();
      gui.refreshWidgets();
   }

   public static ShopCreatorComponentModalPanel openCentered(Panel panel) {
      ShopCreatorComponentModalPanel modal = openDefault(panel);
      modal.center = true;
      int sw = panel.getWidth();
      int sh = panel.getHeight();
      int w = modal.getWidth();
      int h = modal.getHeight();
      modal.setPos((sw - w) / 2, (sh - h) / 2);
      return modal;
   }

   public static ShopCreatorComponentModalPanel openDefault(Panel panel) {
      BaseScreen gui = panel.getGui();
      ShopCreatorComponentModalPanel modal = new ShopCreatorComponentModalPanel(panel);
      modal.setWidth(gui.width / 2);
      modal.setHeight(gui.height);
      gui.pushModalPanel(modal);
      if (MainShopScreen.Instance != null) {
         MainShopScreen.Instance.onModalOpen(modal);
      }

      return modal;
   }

   public abstract static class Button extends SimpleTextButton {
      public Button(Panel panel, Component txt) {
         super(panel, txt, Icon.empty());
      }

      public boolean renderTitleInCenter() {
         return true;
      }

      public abstract boolean isSelected();

      public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
         if (this.isMouseOver) {
            ShopRenderingWrapper.drawRoundedRect(graphics.m_280168_(), x, y, w, h, 5.0F, 1.0F, -14737633, GUIShopMenu.BORDER_4_INT);
         } else if (this.isSelected()) {
            ShopRenderingWrapper.drawRoundedRect(graphics.m_280168_(), x, y, w, h, 5.0F, 1.0F, -14737633, GUIShopMenu.BORDER_3_INT);
         } else {
            ShopRenderingWrapper.drawRoundedRectNoBorder(graphics.m_280168_(), x, y, w, h, 5.0F, -14737633);
         }
      }
   }
}
