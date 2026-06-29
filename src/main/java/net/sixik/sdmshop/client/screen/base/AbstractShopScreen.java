package net.sixik.sdmshop.client.screen.base;

import dev.ftb.mods.ftblibrary.ui.BaseScreen;
import dev.ftb.mods.ftblibrary.ui.PanelScrollBar;
import dev.ftb.mods.ftblibrary.ui.Theme;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sixik.sdmshop.api.ShopBase;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.client.screen.base.panels.AbstractShopEntryPanel;
import net.sixik.sdmshop.client.screen.base.panels.AbstractShopTabPanel;
import net.sixik.sdmshop.client.screen.base.widgets.AbstractShopEntrySearch;
import net.sixik.sdmshop.old_api.screen.BuyerScreenSupport;
import net.sixik.sdmshop.old_api.screen.EntryCreateScreenSupport;
import net.sixik.sdmshop.old_api.screen.InfoButtonSupport;
import net.sixik.sdmshop.old_api.screen.RefreshSupport;
import net.sixik.sdmshop.shop.BaseShop;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.shop.ShopTab;
import net.sixik.sdmshop.utils.ShopUtils;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractShopScreen
   extends BaseScreen
   implements EntryCreateScreenSupport,
   ShopBase.ShopChangeListener,
   BuyerScreenSupport,
   RefreshSupport,
   InfoButtonSupport {
   protected double entryScrollPos = 0.0;
   protected double tabScrollPos = 0.0;
   @Nullable
   public UUID selectedEntryId;
   @Nullable
   public UUID selectedShopTab;
   protected PanelScrollBar scrollEntryPanel;
   protected PanelScrollBar scrollTabPanel;
   protected AbstractShopEntryPanel entryPanel;
   protected AbstractShopTabPanel tabPanel;
   protected AbstractShopEntrySearch entrySearch;
   public BaseShop currentShop;
   public UUID selectedTab;
   public String searchField = "";

   public boolean drawDefaultBackground(GuiGraphics graphics) {
      return false;
   }

   public AbstractShopScreen() {
      this.currentShop = Objects.requireNonNull(SDMShopClient.CurrentShop);
   }

   public final boolean onInit() {
      boolean value = super.onInit() && this._init();
      if (value) {
         this.currentShop.getShopChangeListeners().add(this);
      }

      return value;
   }

   public void onClosed() {
      this.currentShop.getShopChangeListeners().remove(this);
      super.onClosed();
   }

   public boolean _init() {
      this.onConstruct();
      return true;
   }

   protected void onConstruct() {
      if (this.selectedTab == null) {
         for (ShopTab shopTab : this.currentShop.getTabs()) {
            if (!shopTab.isLockedAll(shopTab) || ShopUtils.isEditModeClient()) {
               this.selectedTab = shopTab.getId();
               break;
            }
         }
      }
   }

   public void addWidgets() {
   }

   @Nullable
   public UUID getCurrentTabUuid() {
      return this.selectedTab;
   }

   public Optional<ShopTab> getCurrentTab() {
      return this.currentShop.getTabOptional(this.selectedTab);
   }

   public Optional<ShopTab> getSelectedTabId() {
      return this.currentShop.getTabOptional(this.selectedShopTab);
   }

   public Optional<ShopEntry> getSelectedEntryId() {
      return this.currentShop.getEntryOptional(this.selectedEntryId);
   }

   public void selectTab(UUID uuid) {
      this.selectTab(uuid, true);
   }

   public void selectTab(UUID uuid, boolean recreate) {
      if (Objects.equals(this.selectedTab, uuid)) {
         if (this.scrollEntryPanel != null) {
            this.entryScrollPos = Math.min(this.scrollEntryPanel.getValue(), this.scrollEntryPanel.getMaxValue());
         }
      } else {
         this.entryScrollPos = 0.0;
      }

      this.selectedTab = uuid;
      this.selectedEntryId = null;
      if (this.selectedTab != null && recreate) {
         this.addEntriesButtons();
      }
   }

   public abstract void addEntriesButtons();

   public abstract void addTabsButtons();

   public static void wortInProgress() {
      Minecraft.m_91087_().f_91074_.m_213846_(Component.m_237113_("Work in progress").m_130940_(ChatFormatting.RED));
   }

   @Override
   public void openInfoScreen() {
   }

   public abstract void _onRefresh();

   @Override
   public void onRefresh() {
      RefreshSupport.refreshIfOpened();
      this._onRefresh();
   }

   public static void refreshIfOpen() {
      RefreshSupport.refreshIfOpened(AbstractShopScreen.class);
   }

   public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
   }
}
