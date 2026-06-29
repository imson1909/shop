package net.sixik.sdmshop.client.screen.modern.wallet;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.SimpleButton;
import dev.ftb.mods.ftblibrary.ui.TextField;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sixik.sdmeconomy.currencies.BaseCurrency;
import net.sixik.sdmeconomy.currencies.CurrencySymbol;
import net.sixik.sdmeconomy.currencies.data.CurrencyPlayerData.PlayerCurrency;
import net.sixik.sdmuilib.client.utils.RenderHelper;
import net.sixik.sdmuilib.client.utils.misc.RGBA;

public class CurrencyRender extends Panel {
   public TextField nameLabel;
   public TextField balanceLabel;
   public SimpleButton select;
   public BaseCurrency currency;
   public String name;
   public CurrencySymbol symbol;
   public Double balance;

   public CurrencyRender(Panel panel, PlayerCurrency currency) {
      super(panel);
      this.currency = currency.currency;
      this.name = this.currency.getName();
      this.symbol = this.currency.symbol;
      this.balance = currency.balance;
   }

   public void addWidgets() {
      this.add(this.nameLabel = new TextField(this));
      this.add(this.balanceLabel = new TextField(this));
      this.add(this.select = new SimpleButton(this, Component.m_237113_("null"), Icon.empty(), (simpleButton, mouseButton) -> {
         if (PlayerWallet.currency == null) {
            PlayerWallet.currency = this.currency;
         } else {
            PlayerWallet.currency = null;
         }

         this.parent.getGui().refreshWidgets();
      }));
   }

   public void alignWidgets() {
      this.nameLabel.setText(this.name);
      this.nameLabel.setX(2);
      this.nameLabel.setY(1);
      this.balanceLabel.setText(this.balance + " " + this.symbol.value);
      this.balanceLabel.setX(2);
      this.balanceLabel.setY(this.nameLabel.getHeight() + 3);
      this.select.setPosAndSize(0, 0, 200, 22);
   }

   public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
      if (PlayerWallet.currency == this.currency) {
         RenderHelper.drawRoundedRect(graphics, x, y, w, h, 5, RGBA.create(255, 255, 255, 140));
      } else {
         RenderHelper.drawRoundedRect(graphics, x, y, w, h, 5, RGBA.create(255, 255, 255, 110));
      }
   }
}
