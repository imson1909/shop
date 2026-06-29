package net.sixik.sdmshop.client.screen_new.components.buyer;

import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.ui.BaseScreen;
import dev.ftb.mods.ftblibrary.ui.Button;
import dev.ftb.mods.ftblibrary.ui.ModalPanel;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.TextBox;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sixik.sdmshop.api.ShopApi;
import net.sixik.sdmshop.client.screen_new.MainShopEntryButton;
import net.sixik.sdmshop.client.screen_new.MainShopScreen;
import net.sixik.sdmshop.client.screen_new.api.GUIShopMenu;
import net.sixik.sdmshop.old_api.ShopEntryType;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.utils.ShopRenderUtils;
import net.sixik.sdmshop.utils.ShopUtils;
import net.sixik.sdmshop.utils.rendering.ShopRenderingWrapper;
import net.sixik.sdmshop.utils.rendering.widgets.IconTooltipWidget;

public class ShopBuyProductComponentModalPanel extends ModalPanel {
   protected static final int fontH = Theme.DEFAULT.getFontHeight();
   protected static final int fontHD2 = fontH / 2;
   protected final int remaining;
   protected final String remainingTxt;
   protected final int remainingTxtL;
   protected final int totalLimit;
   protected final ShopEntryType shopEntryType;
   protected final ShopEntry shopEntry;
   protected final String moneyText;
   protected final int textL;
   protected final Icon icon;
   protected boolean center;
   protected int leftPanelW;
   protected int rightPanelW;
   protected int space = 4;
   protected Component title;
   protected int titleL;
   protected int iconSize;
   protected int iconSize3;
   protected Component priceBy;
   protected int priceByL;
   protected final int maxOfferSize;
   protected final String maxOfferSizeTxt;
   protected final int maxOfferSizeTxtL;
   protected IconTooltipWidget iconTooltipWidget;
   protected TextBox inputCountBox;
   protected Button minusButton;
   protected Button plusButton;
   protected Button maxButton;
   protected Button cancelButton;
   protected Button acceptButton;
   protected int currentPlayerOffer = 0;
   protected boolean leftPanelWidgetsSet = false;
   protected boolean rightPanelWidgetsSet = false;

   public static ShopBuyProductComponentModalPanel openCentered(Panel panel, ShopEntry shopEntry) {
      ShopBuyProductComponentModalPanel modal = openDefault(panel, shopEntry);
      modal.center = true;
      int sw = panel.getWidth();
      int sh = panel.getHeight();
      int w = modal.getWidth();
      int h = modal.getHeight();
      modal.setPos((sw - w) / 2, (sh - h) / 2);
      return modal;
   }

   public static ShopBuyProductComponentModalPanel openDefault(Panel panel, ShopEntry shopEntry) {
      BaseScreen gui = panel.getGui();
      ShopBuyProductComponentModalPanel modal = new ShopBuyProductComponentModalPanel(panel, shopEntry);
      modal.setSize(gui.width / 2, (int)(gui.height / 1.5));
      gui.pushModalPanel(modal);
      if (MainShopScreen.Instance != null) {
         MainShopScreen.Instance.onModalOpen(modal);
      }

      return modal;
   }

   protected ShopBuyProductComponentModalPanel(Panel panel, ShopEntry shopEntry) {
      super(panel);
      this.shopEntry = shopEntry;
      this.shopEntryType = shopEntry.getType();
      this.title = shopEntry.getTitle();
      this.titleL = Theme.DEFAULT.getStringWidth(this.title);
      this.moneyText = this.shopEntry.getEntrySellerType().moneyToString(this.shopEntry);
      this.textL = Theme.DEFAULT.getStringWidth(this.moneyText);
      this.icon = ShopRenderUtils.getIconFromEntry(this.shopEntry);
      this.priceBy = Component.m_237110_("sdm.shop.gui.buyer.text.price_per", new Object[]{shopEntry.getCount()});
      this.priceByL = Theme.DEFAULT.getStringWidth(this.priceBy);
      this.remaining = shopEntry.getObjectLimitLeft(Minecraft.m_91087_().f_91074_);
      this.totalLimit = shopEntry.getObjectLimit();
      this.remainingTxt = String.valueOf(this.remaining);
      this.remainingTxtL = Theme.DEFAULT.getStringWidth(this.remainingTxt);
      this.maxOfferSize = this.getMaxEntryOfferSize(this.remaining);
      this.maxOfferSizeTxt = String.valueOf(this.maxOfferSize);
      this.maxOfferSizeTxtL = Theme.DEFAULT.getStringWidth(this.maxOfferSizeTxt);
   }

   public void addWidgets() {
      this.leftPanelWidgetsSet = false;
      this.rightPanelWidgetsSet = false;
      this.add(this.iconTooltipWidget = new IconTooltipWidget(this, this.icon, s -> this.shopEntry.getEntryType().addEntryTooltip(s, this.shopEntry)));
      this.add(this.inputCountBox = new TextBox(this) {
         public boolean isValid(String txt) {
            return ShopUtils.isDigitsInRange(txt, 0, ShopBuyProductComponentModalPanel.this.maxOfferSize);
         }

         public void onTextChanged() {
            String txt = this.getText();
            if (!txt.isEmpty()) {
               ShopBuyProductComponentModalPanel.this.currentPlayerOffer = Integer.parseInt(txt);
            }
         }
      });
      this.inputCountBox.setFilter(ShopUtils.ONLY_DIGITS);
      this.inputCountBox.setText(String.valueOf(this.currentPlayerOffer));
      this.add(this.minusButton = new SimpleTextButton(this, Component.m_237113_("-"), Icon.empty()) {
         public void onClicked(MouseButton button) {
            ShopBuyProductComponentModalPanel.this.setUserCount(Math.max(0, ShopBuyProductComponentModalPanel.this.currentPlayerOffer - 1));
         }
      });
      this.add(
         this.plusButton = new SimpleTextButton(this, Component.m_237113_("+"), Icon.empty()) {
            public void onClicked(MouseButton button) {
               ShopBuyProductComponentModalPanel.this.setUserCount(
                  Math.min(ShopBuyProductComponentModalPanel.this.maxOfferSize, ShopBuyProductComponentModalPanel.this.currentPlayerOffer + 1)
               );
            }
         }
      );
      this.add(this.maxButton = new SimpleTextButton(this, Component.m_237113_("MAX"), Icon.empty()) {
         public void onClicked(MouseButton button) {
            ShopBuyProductComponentModalPanel.this.setUserCount(ShopBuyProductComponentModalPanel.this.maxOfferSize);
         }
      });
      this.add(this.cancelButton = new SimpleTextButton(this, Component.m_237113_("Cancel"), Icon.empty()) {
         public void onClicked(MouseButton button) {
            this.getGui().popModalPanel();
         }
      });
      this.add(this.acceptButton = new SimpleTextButton(this, Component.m_237113_("Accept"), Icon.empty()) {
         public boolean shouldDraw() {
            return ShopBuyProductComponentModalPanel.this.currentPlayerOffer > 0;
         }

         public void onClicked(MouseButton button) {
            ShopApi.sendBuyEntry(ShopBuyProductComponentModalPanel.this.shopEntry, ShopBuyProductComponentModalPanel.this.currentPlayerOffer);
            this.getGui().popModalPanel();
         }
      });
   }

   public void setUserCount(int value) {
      this.inputCountBox.setText(String.valueOf(value));
   }

   public void setWidth(int v) {
      super.setWidth(v);
      this.rightPanelW = this.width / 3 - this.space;
      this.leftPanelW = this.width / 2 + this.rightPanelW / 2;
      int shift = this.leftPanelW / 7;
      this.leftPanelW -= shift;
      this.rightPanelW += shift;
      this.iconSize = v / 8;
      this.iconSize3 = this.iconSize / 3;
   }

   public void alignWidgets() {
   }

   public void onClosed() {
      if (MainShopScreen.Instance != null) {
         MainShopScreen.Instance.onModalClose(this);
      }

      super.onClosed();
   }

   public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
      this.drawDefaultPanel(graphics, x, y, this.leftPanelW, h);
      this.drawDefaultPanel(graphics, x + w - this.rightPanelW - this.space, y, this.rightPanelW, h);
   }

   protected void drawDefaultPanel(GuiGraphics graphics, int x, int y, int w, int h) {
      ShopRenderingWrapper.beginBatch(w, h, 4.0F, 1.0F);
      ShopRenderingWrapper.addBatchRect(graphics, x, y, w, h, GUIShopMenu.BACKGROUND_FILL_INT, GUIShopMenu.BORDER_INT);
      ShopRenderingWrapper.endBatch();
   }

   public void drawOffsetBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
      this.drawLeftPanel(graphics, theme, x, y, this.leftPanelW, h);
      this.drawRightPanel(graphics, theme, x + this.leftPanelW + this.space, y, this.rightPanelW, h);
   }

   public void drawRightPanel(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
      int posY = y + 8;
      Component ymTxt = Component.m_237115_("sdm.shop.gui.buyer.text.player_money");
      int ymTxtL = theme.getStringWidth(ymTxt);
      theme.drawString(graphics, ymTxt, x + (w - ymTxtL) / 2, posY, Color4I.rgb(11059164), 0);
      posY += fontH + 2;
      double playerMoney = this.shopEntry.getEntrySellerType().getMoney(Minecraft.m_91087_().f_91074_, this.shopEntry);
      this.shopEntry.getEntrySellerType().drawCentered(graphics, theme, x, posY, w, h, playerMoney);
      posY += fontH + fontHD2;
      Component ysTxt = Component.m_237115_(this.shopEntryType.isBuy() ? "sdm.shop.gui.buyer.text.player_spend" : "sdm.shop.gui.buyer.text.player_receive");
      int ysTxtL = theme.getStringWidth(ysTxt);
      theme.drawString(graphics, ysTxt, x + (w - ysTxtL) / 2, posY, Color4I.rgb(11059164), 0);
      posY += fontH + 2;
      this.shopEntry.getEntrySellerType().drawCentered(graphics, theme, x, posY, w, h, this.shopEntry.getPrice() * this.currentPlayerOffer);
      posY += fontH + fontHD2;
      Component mlTxt = Component.m_237115_("sdm.shop.gui.buyer.text.player_money_left");
      int mlTxtL = theme.getStringWidth(mlTxt);
      theme.drawString(graphics, mlTxt, x + (w - mlTxtL) / 2, posY, Color4I.rgb(11059164), 0);
      posY += fontH + 2;
      this.shopEntry
         .getEntrySellerType()
         .drawCentered(
            graphics,
            theme,
            x,
            posY,
            w,
            h,
            this.shopEntryType.isSell()
               ? playerMoney + this.currentPlayerOffer * this.shopEntry.getPrice()
               : playerMoney - this.currentPlayerOffer * this.shopEntry.getPrice()
         );
      if (!this.rightPanelWidgetsSet) {
         int pad = 4;
         int gap = 4;
         int buttonH = fontH + fontHD2;
         int buttonW = Math.max(20, (this.rightPanelW - 8 - 4) / 2);
         this.cancelButton.setSize(buttonW, buttonH);
         this.acceptButton.setSize(buttonW, buttonH);
         int rightPanelStartX = this.width - this.rightPanelW;
         int buttonsY = h - 4 - buttonH;
         this.cancelButton.setPos(rightPanelStartX + 4, buttonsY);
         this.acceptButton.setPos(rightPanelStartX + 4 + buttonW, buttonsY);
         this.rightPanelWidgetsSet = true;
      }
   }

   public void drawLeftPanel(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
      int posY = y + this.iconSize3;
      if (!this.leftPanelWidgetsSet) {
         this.iconTooltipWidget.setSize(this.iconSize, this.iconSize);
         this.iconTooltipWidget.setPos((w - this.iconSize) / 2, this.iconSize3);
      }

      posY += this.iconSize + fontHD2;
      this.shopEntry.getEntryType().drawTitleCentered(this.shopEntry, graphics, theme, x, posY, w, h);
      posY += fontH * 2;
      theme.drawString(graphics, this.priceBy, x + 2, posY, Color4I.rgb(11059164), 0);
      if (this.shopEntry.getPrice() > 0.0) {
         int size = this.shopEntry.getEntrySellerType().getRenderSize(graphics, theme, x, posY, w, h, this.shopEntry.getPrice());
         this.shopEntry.getEntrySellerType().draw(graphics, theme, x + (w - size) - 4, posY, w, h, this.shopEntry.getPrice());
      } else {
         theme.drawString(graphics, MainShopEntryButton.FREE_COMPONENT, x + (w - MainShopEntryButton.FREE_COMPONENT_L) - 2, posY);
      }

      if (this.remaining != Integer.MAX_VALUE && this.totalLimit > 0) {
         posY += fontH + fontHD2;
         theme.drawString(graphics, Component.m_237115_("sdm.shop.gui.buyer.text.available_items"), x + 2, posY, Color4I.rgb(11059164), 0);
         theme.drawString(graphics, this.remainingTxt, x + (w - this.remainingTxtL) - 2, posY);
      }

      posY += fontH + fontHD2;
      theme.drawString(graphics, Component.m_237115_("sdm.shop.gui.buyer.text.max_buy"), x + 2, posY, Color4I.rgb(11059164), 0);
      theme.drawString(graphics, this.maxOfferSizeTxt, x + (w - this.maxOfferSizeTxtL) - 2, posY);
      posY += fontH + fontHD2;
      if (!this.leftPanelWidgetsSet) {
         int pad = 4;
         int gap = 4;
         int bY = posY - y;
         int bH = fontH + fontHD2;
         this.minusButton.setY(bY);
         this.minusButton.setHeight(bH);
         this.plusButton.setY(bY);
         this.plusButton.setHeight(bH);
         this.maxButton.setY(bY);
         this.maxButton.setHeight(bH);
         this.inputCountBox.setY(bY);
         this.inputCountBox.setHeight(bH);
         int mw = this.minusButton.getWidth();
         int pw = this.plusButton.getWidth();
         int xw = this.maxButton.getWidth();
         int minusX = 4;
         int maxX = w - 4 - xw;
         int plusX = maxX - 4 - pw;
         int inputX = 4 + mw + 4;
         int inputW = Math.max(0, plusX - 4 - inputX);
         this.minusButton.setX(4);
         this.inputCountBox.setX(inputX);
         this.inputCountBox.setWidth(inputW);
         this.plusButton.setX(plusX);
         this.maxButton.setX(maxX);
         this.leftPanelWidgetsSet = true;
      }
   }

   public int getMaxEntryOfferSize(int size) {
      return ShopUtils.getMaxEntryOfferSize(this.shopEntry, Minecraft.m_91087_().f_91074_, size != Integer.MAX_VALUE ? size : -1);
   }
}
