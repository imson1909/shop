package net.sixik.sdmshop.client.screen_new;

import dev.ftb.mods.ftblibrary.ui.BaseScreen;
import dev.ftb.mods.ftblibrary.ui.ModalPanel;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.PanelScrollBar;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.Widget;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.gui.GuiGraphics;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.client.screen_new.api.GUIShopMenu;
import net.sixik.sdmshop.client.screen_new.api.ShopScreenEvents;
import net.sixik.sdmshop.shop.BaseShop;
import net.sixik.sdmuilib.client.utils.misc.RGBA;

public class MainShopScreen extends BaseScreen implements GUIShopMenu {
   public static MainShopScreen Instance;
   protected MainShopLeftPanel leftPanel;
   protected MainShopEntryPanel entryPanel;
   protected PanelScrollBar entryPanelScroll;
   protected double entryPanelScrollSafe = 0.0;
   public boolean shouldRenderWidgets = true;
   public ObjectArrayList<ShopScreenEvents.OnModalOpen> modalOpenEventListeners = new ObjectArrayList();
   public ObjectArrayList<ShopScreenEvents.OnModalClose> modalCloseEventListeners = new ObjectArrayList();

   public MainShopScreen() {
      SDMShopClient.shopFilters = GUIShopMenu.createFilters();
      this.getModalOpenListeners().add((ShopScreenEvents.OnModalOpen)s -> this.shouldRenderWidgets = false);
      this.getModalCloseListeners().add((ShopScreenEvents.OnModalClose)s -> this.shouldRenderWidgets = true);
   }

   public void onFilterApply() {
      this.entryPanel.clearWidgets();
      this.entryPanel.addWidgets();
      this.entryPanel.alignWidgets();
   }

   public boolean onInit() {
      if (getShop() == null) {
         return false;
      }

      int sw = this.getScreen().m_85445_();
      int sh = this.getScreen().m_85446_();
      int margin = 10;
      int availW = Math.max(1, sw - 20);
      int availH = Math.max(1, sh - 20);
      int w = (int)(availW * 0.95F);
      int h = (int)(availH * 0.95F);
      this.setWidth(w);
      this.setHeight(h);
      this.setX(10 + (availW - w) / 2);
      this.setY(10 + (availH - h) / 2);
      this.closeContextMenu();
      if (this.entryPanelScroll != null) {
         this.entryPanelScrollSafe = this.entryPanelScroll.getValue();
      }

      Instance = this;
      return super.onInit();
   }

   @Override
   public void addWidgets() {
      this.add(this.leftPanel = new MainShopLeftPanel(this));
      this.add(this.entryPanel = new MainShopEntryPanel(this));
      this.add(this.entryPanelScroll = new PanelScrollBar(this, this.entryPanel) {
         public void drawScrollBar(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            SDMShopClient.someColor.draw(graphics, x, y, w, h);
         }

         public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            RGBA.create(0, 0, 0, 127).draw(graphics, x, y, w, h, 0.0F);
         }
      });
   }

   @Override
   public void alignWidgets() {
      this.leftPanel.setWidth(this.width / 4);
      this.leftPanel.setHeight(this.height);
      this.entryPanel.setWidth(this.width - this.leftPanel.width - 2);
      this.entryPanel.setHeight(this.height);
      this.entryPanel.posX = this.leftPanel.width + 2;
      this.entryPanelScroll
         .setPosAndSize(this.entryPanel.getPosX() + this.entryPanel.getWidth() - 2, this.entryPanel.getPosY(), 2, this.entryPanel.getHeight());
      this.entryPanelScroll.setValue(this.entryPanelScrollSafe);

      for (Widget widget : this.getWidgets()) {
         if (widget instanceof Panel panel) {
            panel.alignWidgets();
         }
      }
   }

   @Override
   public BaseScreen self() {
      return this;
   }

   @Override
   public ObjectArrayList<ShopScreenEvents.OnModalOpen> getModalOpenListeners() {
      return this.modalOpenEventListeners;
   }

   @Override
   public ObjectArrayList<ShopScreenEvents.OnModalClose> getModalCloseListeners() {
      return this.modalCloseEventListeners;
   }

   public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
   }

   public void onModalOpen(ModalPanel panel) {
      for (int i = 0; i < this.modalOpenEventListeners.size(); i++) {
         ((ShopScreenEvents.OnModalOpen)this.modalOpenEventListeners.get(i)).handle(panel);
      }
   }

   public void onModalClose(ModalPanel panel) {
      for (int i = 0; i < this.modalCloseEventListeners.size(); i++) {
         ((ShopScreenEvents.OnModalClose)this.modalCloseEventListeners.get(i)).handle(panel);
      }
   }

   public static BaseShop getShop() {
      return SDMShopClient.CurrentShop;
   }
}
