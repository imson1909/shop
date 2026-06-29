package net.sixik.sdmshop.client.screen.base.buyer;

import dev.ftb.mods.ftblibrary.ui.BaseScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.sixik.sdmshop.client.screen.base.AbstractShopScreen;
import net.sixik.sdmshop.client.screen.base.widgets.AbstractShopEntryButton;
import net.sixik.sdmshop.old_api.screen.RefreshSupport;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.shop.ShopTab;
import net.sixik.sdmshop.shop.exceptions.TabNotFoundException;
import net.sixik.sdmshop.shop.limiter.ShopLimiterData;
import net.sixik.sdmshop.utils.ShopUtils;

public abstract class AbstractBuyerScreen extends BaseScreen implements RefreshSupport {
   protected final Minecraft minecraft = Minecraft.m_91087_();
   protected final int lineHeight = 9;
   protected AbstractBuyerBuyButton buyButton;
   protected AbstractBuyerCancelButton cancelButton;
   protected ShopTab shopTab;
   protected ShopEntry shopEntry;
   protected int count = 0;
   protected AbstractShopScreen shopScreen;

   public boolean drawDefaultBackground(GuiGraphics graphics) {
      return false;
   }

   public AbstractBuyerScreen(AbstractShopScreen shopScreen, AbstractShopEntryButton shopEntry) {
      this.shopScreen = shopScreen;
      this.shopEntry = shopEntry.getShopEntry();
      if (this.shopEntry != null) {
         this.shopTab = shopScreen.currentShop.getTabOptional(this.shopEntry.getTab()).orElseThrow(TabNotFoundException::new);
      }
   }

   public void onClosed() {
      super.onClosed();
      this.shopScreen.currentShop.onChange();
   }

   public ShopLimiterData getShopLimit() {
      return ShopUtils.getShopLimit(this.shopTab, this.shopEntry, Minecraft.m_91087_().f_91074_);
   }

   public int getMaxEntryOfferSize() {
      return this.getMaxEntryOfferSize(this.getShopLimit().value());
   }

   public int getMaxEntryOfferSize(int size) {
      return ShopUtils.getMaxEntryOfferSize(this.shopEntry, Minecraft.m_91087_().f_91074_, size);
   }

   protected static boolean isDigitsInRange(String s, int min, int max) {
      return ShopUtils.isDigitsInRange(s, min, max);
   }
}
