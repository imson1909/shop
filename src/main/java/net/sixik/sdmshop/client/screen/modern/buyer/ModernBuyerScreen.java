package net.sixik.sdmshop.client.screen.modern.buyer;

import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.ui.TextBox;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sixik.sdmshop.client.render.BuyerRenderVariable;
import net.sixik.sdmshop.client.screen.base.AbstractShopScreen;
import net.sixik.sdmshop.client.screen.base.buyer.AbstractBuyerBuyButton;
import net.sixik.sdmshop.client.screen.base.buyer.AbstractBuyerCancelButton;
import net.sixik.sdmshop.client.screen.base.buyer.AbstractBuyerScreen;
import net.sixik.sdmshop.client.screen.base.widgets.AbstractShopEntryButton;
import net.sixik.sdmshop.shop.limiter.ShopLimiterData;
import net.sixik.sdmshop.utils.ShopRenderUtils;
import net.sixik.sdmuilib.client.utils.GLHelper;
import net.sixik.sdmuilib.client.utils.math.Vector2;
import net.sixik.sdmuilib.client.utils.misc.RGBA;

public class ModernBuyerScreen extends AbstractBuyerScreen {
   private static final RGBA BG_COLOR = RGBA.create(0, 0, 0, 127);
   private static final int PADDING = 5;
   protected int offerSize = -1;
   protected int limitValue;
   protected ShopLimiterData limitData;
   protected TextBox textBox;
   protected int sizeIcon;
   protected Component entryType = Component.m_237119_();

   public ModernBuyerScreen(AbstractShopScreen shopScreen, AbstractShopEntryButton shopEntry) {
      super(shopScreen, shopEntry);
      this.updateLimitData();
   }

   public void addWidgets() {
   }

   public void alignWidgets() {
      this.onRefresh();
   }

   @Override
   public void onRefresh() {
      this.getWidgets().clear();
      this.add(this.textBox = new TextBox(this) {
         public boolean isValid(String txt) {
            return ModernBuyerScreen.isDigitsInRange(txt, 1, ModernBuyerScreen.this.offerSize);
         }

         public void onTextChanged() {
            String t = this.getText();
            if (ModernBuyerScreen.isDigitsInRange(t, 1, ModernBuyerScreen.this.offerSize)) {
               ModernBuyerScreen.this.count = Integer.parseInt(t);
            }
         }

         public void drawTextBox(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            ModernBuyerScreen.BG_COLOR.drawRoundFill(graphics, x, y, w, h, 4);
         }
      });
      this.add(this.cancelButton = new ModernBuyerScreen.CancelButton(this));
      this.add(this.buyButton = new ModernBuyerScreen.BuyButton(this));
      int bsize = this.width / 2 - 10;
      this.sizeIcon = this.width >= 16 ? 16 : 8;
      this.cancelButton.setPosAndSize(8, this.height - 24, bsize, 16);
      this.buyButton.setPosAndSize(this.width - bsize - 8, this.height - 24, bsize, 16);
      this.textBox.setText(this.count > 0 ? String.valueOf(this.count) : "");
      this.textBox.ghostText = this.shopEntry.getType().isSell()
         ? Component.m_237115_("sdm.shop.modern.ui.buyer.entry.input.ghost.sell").getString()
         : Component.m_237115_("sdm.shop.modern.ui.buyer.entry.input.ghost.buy").getString();
      this.textBox.setPos(5, 5 + this.sizeIcon * 2 + 2 + (this.lineHeight + 1 + 2) * 2);
      this.textBox.setSize(this.width - 10, this.lineHeight + 1);
      this.updateButtons();
   }

   protected void updateButtons() {
      this.updateLimitData();
      this.entryType = this.shopEntry.getType().isSell()
         ? Component.m_237115_("sdm.shop.modern.ui.buyer.entry.sell")
         : Component.m_237115_("sdm.shop.modern.ui.buyer.entry.buy");
   }

   protected void updateLimitData() {
      this.limitData = this.getShopLimit();
      this.limitValue = this.limitData.value();
      this.offerSize = this.getMaxEntryOfferSize(this.limitValue);
   }

   public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
      BG_COLOR.drawRoundFill(graphics, x, y, w, h, 10);
      Vector2 pos = new Vector2(x + 5, y + 5);
      BG_COLOR.drawRoundFill(graphics, pos.x, pos.y, this.sizeIcon * 2, this.sizeIcon * 2, 8);
      AbstractShopEntryButton.getIconFromEntry(this.shopEntry)
         .draw(graphics, pos.x + this.sizeIcon / 2, pos.y + this.sizeIcon / 2, this.sizeIcon, this.sizeIcon);
      pos.setX(pos.x + this.sizeIcon * 2 + 2);
      BG_COLOR.drawRoundFill(graphics, pos.x, pos.y, this.width - 10 - 2 - this.sizeIcon * 2, this.lineHeight + 1, 4);
      BuyerRenderVariable variable = new BuyerRenderVariable(pos, this.sizeIcon);
      this.shopEntry.getEntryType().drawTitle(this.shopEntry, graphics, theme, x, y, w, h, variable, this);
      int d = this.shopEntry.getEntrySellerType().getRenderWight(graphics, theme, x, y, w, h, this.shopEntry.getCount(), this, 0);
      int w1 = this.width - 10 - 2 - this.sizeIcon * 2 - d;
      int w2 = w1 / 2;
      pos.setY(pos.y + this.sizeIcon);
      BG_COLOR.drawRoundFill(graphics, pos.x, pos.y, this.width - 10 - 2 - this.sizeIcon * 2, this.lineHeight + 1, 4);
      this.shopEntry.getEntrySellerType().draw(graphics, theme, pos.x + w2, pos.y + 1, w, 16, this.shopEntry.getPrice(), this, 0);
      pos.setPosition(x + 5, y + 5 + this.sizeIcon * 2 + 2);
      Vector2 size = new Vector2(this.width - 10, this.height - (5 + this.sizeIcon * 2 + 2 + 24 + 2));
      BG_COLOR.drawRoundFill(graphics, pos.x, pos.y, size.x / 2 - 2, this.lineHeight + 1, 4);
      GLHelper.pushScissor(graphics, pos.x, pos.y, size.x / 2 - 2, this.lineHeight + 1);
      theme.drawString(graphics, Component.m_237115_("sdm.shop.modern.ui.player_money"), pos.x + 2, pos.y + 1, Color4I.WHITE, 2);
      GLHelper.popScissor(graphics);
      BG_COLOR.drawRoundFill(graphics, pos.x + size.x / 2, pos.y, size.x / 2, this.lineHeight + 1, 4);
      GLHelper.pushScissor(graphics, pos.x + size.x / 2, pos.y, size.x / 2 - 2, this.lineHeight + 1);
      this.shopEntry
         .getEntrySellerType()
         .draw(
            graphics,
            theme,
            pos.x + size.x / 2 + 2,
            pos.y + 1,
            w,
            16,
            this.shopEntry.getEntrySellerType().getMoney(Minecraft.m_91087_().f_91074_, this.shopEntry),
            this,
            -2
         );
      GLHelper.popScissor(graphics);
      pos.setPosition(pos.x, pos.y + this.lineHeight + 1 + 2);
      ShopRenderUtils.drawLabel(graphics, theme, pos, size, this.entryType.getString(), String.valueOf(this.offerSize));
      String textMoney;
      if (this.limitValue >= 0 && this.limitValue != Integer.MAX_VALUE) {
         pos.setPosition(pos.x, pos.y + (this.lineHeight + 1 + 2) * 2);
         ShopRenderUtils.drawLabel(
            graphics, theme, pos, size, Component.m_237115_("sdm.shop.modern.ui.buyer.entry.limit").getString(), String.valueOf(this.limitValue)
         );
         pos.setPosition(pos.x, pos.y + this.lineHeight + 1 + 2);
         textMoney = this.shopEntry.getType().isSell()
            ? Component.m_237115_("sdm.shop.modern.ui.buyer.entry.output.sell").getString()
            : Component.m_237115_("sdm.shop.modern.ui.buyer.entry.output.buy").getString();
      } else {
         pos.setPosition(pos.x, pos.y + (this.lineHeight + 1 + 2) * 2);
         textMoney = this.shopEntry.getType().isSell()
            ? Component.m_237115_("sdm.shop.modern.ui.buyer.entry.output.sell").getString()
            : Component.m_237115_("sdm.shop.modern.ui.buyer.entry.output.buy").getString();
      }

      ShopRenderUtils.drawLabel(
         graphics,
         theme,
         pos,
         size,
         (graphics1, theme1, x1, y1, w3, h1) -> theme.drawString(graphics1, textMoney, x1 + 2, y1 + 1, Color4I.WHITE, 2),
         (graphics1, theme1, x1, y1, w3, h1) -> this.shopEntry
            .getEntrySellerType()
            .draw(graphics1, theme1, x1, y1, w3, 16, this.shopEntry.getPrice() * this.count, this, 0)
      );
   }

   protected static class BuyButton extends AbstractBuyerBuyButton {
      public BuyButton(ModernBuyerScreen modernBuyerScreen) {
         super(modernBuyerScreen);
      }

      public boolean renderTitleInCenter() {
         return true;
      }

      public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
         RGBA.create(0, 0, 0, 127).drawRoundFill(graphics, x, y, w, h, 6);
      }
   }

   protected static class CancelButton extends AbstractBuyerCancelButton {
      public CancelButton(ModernBuyerScreen modernBuyerScreen) {
         super(modernBuyerScreen);
      }

      public boolean renderTitleInCenter() {
         return true;
      }

      public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
         RGBA.create(0, 0, 0, 127).drawRoundFill(graphics, x, y, w, h, 6);
      }
   }
}
