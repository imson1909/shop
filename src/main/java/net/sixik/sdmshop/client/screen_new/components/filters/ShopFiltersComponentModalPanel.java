package net.sixik.sdmshop.client.screen_new.components.filters;

import dev.ftb.mods.ftblibrary.ui.BaseScreen;
import dev.ftb.mods.ftblibrary.ui.ModalPanel;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.PanelScrollBar;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.gui.GuiGraphics;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.client.screen_new.MainShopScreen;
import net.sixik.sdmuilib.client.utils.misc.RGBA;

public class ShopFiltersComponentModalPanel extends ModalPanel {
   protected boolean center = false;
   protected ShopFiltersComponentTypePanel typePanel;
   protected PanelScrollBar typePanelScroll;
   protected ShopFiltersComponentConfigPanel configPanel;
   protected PanelScrollBar configPanelScroll;

   public static ShopFiltersComponentModalPanel openCentered(Panel panel) {
      ShopFiltersComponentModalPanel modal = openDefault(panel);
      modal.center = true;
      int sw = panel.getWidth();
      int sh = panel.getHeight();
      int w = modal.getWidth();
      int h = modal.getHeight();
      modal.setPos((sw - w) / 2, (sh - h) / 2);
      return modal;
   }

   public static ShopFiltersComponentModalPanel openDefault(Panel panel) {
      BaseScreen gui = panel.getGui();
      ShopFiltersComponentModalPanel modal = new ShopFiltersComponentModalPanel(panel);
      modal.setWidth(gui.width * 3 / 5);
      modal.setHeight(gui.height * 4 / 4);
      gui.pushModalPanel(modal);
      if (MainShopScreen.Instance != null) {
         MainShopScreen.Instance.onModalOpen(modal);
      }

      return modal;
   }

   protected ShopFiltersComponentModalPanel(Panel panel) {
      super(panel);
   }

   public void addWidgets() {
      this.add(this.typePanel = new ShopFiltersComponentTypePanel(this));
      this.add(this.typePanelScroll = new PanelScrollBar(this, this.typePanel) {
         public void drawScrollBar(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            SDMShopClient.someColor.draw(graphics, x, y, w, h);
         }

         public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            RGBA.create(0, 0, 0, 127).draw(graphics, x, y, w, h, 0.0F);
         }
      });
      this.add(this.configPanel = new ShopFiltersComponentConfigPanel(this));
      this.add(this.configPanelScroll = new PanelScrollBar(this, this.configPanel) {
         public void drawScrollBar(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            SDMShopClient.someColor.draw(graphics, x, y, w, h);
         }

         public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            RGBA.create(0, 0, 0, 127).draw(graphics, x, y, w, h, 0.0F);
         }
      });
   }

   public void alignWidgets() {
      this.typePanel.width = this.width / 3;
      this.typePanel.height = this.height - 2;
      this.typePanel.posX = this.width - this.typePanel.width;
      this.typePanelScroll.setPosAndSize(this.typePanel.getPosX() + this.typePanel.getWidth() - 2, this.typePanel.getPosY(), 2, this.typePanel.getHeight());
      this.typePanel.clearWidgets();
      this.typePanel.addWidgets();
      this.typePanel.alignWidgets();
      this.configPanel.width = this.width - this.typePanel.width - 2;
      this.configPanel.height = this.typePanel.height;
      this.configPanelScroll
         .setPosAndSize(this.configPanel.getPosX() + this.configPanel.getWidth() - 2, this.configPanel.getPosY(), 2, this.configPanel.getHeight());
      this.configPanel.clearWidgets();
      this.configPanel.addWidgets();
      this.configPanel.alignWidgets();
   }

   public void onClosed() {
      if (MainShopScreen.Instance != null) {
         MainShopScreen.Instance.onModalClose(this);
      }

      super.onClosed();
   }

   public void onSelected(ShopFiltersComponentTypePanel.Button button) {
      this.configPanel.onSelected(button);
   }
}
