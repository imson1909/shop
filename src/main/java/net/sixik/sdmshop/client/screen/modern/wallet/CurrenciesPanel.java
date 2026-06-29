package net.sixik.sdmshop.client.screen.modern.wallet;

import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.gui.GuiGraphics;
import net.sixik.sdmeconomy.api.EconomyAPI;
import net.sixik.sdmeconomy.currencies.data.CurrencyPlayerData.PlayerCurrency;
import net.sixik.sdmuilib.client.utils.RenderHelper;
import net.sixik.sdmuilib.client.utils.misc.RGBA;

public class CurrenciesPanel extends Panel {
   public CurrencyRender currencyRender;

   public CurrenciesPanel(Panel panel) {
      super(panel);
   }

   public void addWidgets() {
      int i = 0;

      for (PlayerCurrency currency : EconomyAPI.getPlayerCurrencyClientData().currencies) {
         i++;
         this.add(this.currencyRender = new CurrencyRender(this, currency));
         this.currencyRender.setPosAndSize(2, 2 * i, this.parent.width / 2 - 11, 20);
      }
   }

   public void alignWidgets() {
   }

   public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
      RenderHelper.drawRoundedRect(graphics, x, y, w, h, 2, RGBA.create(0, 0, 0, 180));
   }
}
