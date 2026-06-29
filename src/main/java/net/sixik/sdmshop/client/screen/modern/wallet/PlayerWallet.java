package net.sixik.sdmshop.client.screen.modern.wallet;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.ui.BaseScreen;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.sixik.sdmeconomy.currencies.BaseCurrency;
import net.sixik.sdmuilib.client.utils.RenderHelper;
import net.sixik.sdmuilib.client.utils.misc.RGBA;

public class PlayerWallet extends BaseScreen {
   public CurrenciesPanel currenciesPanel;
   public PayPanel payPanel;
   public TransferPanel transferPanel = new TransferPanel();
   public ModernSimpleTextButton transaction;
   public static BaseCurrency currency;
   public static PlayerInfo recipient;

   public void addWidgets() {
      this.add(this.currenciesPanel = new CurrenciesPanel(this));
      this.add(this.payPanel = new PayPanel(this));
      if (currency != null && recipient != null) {
         this.add(this.transaction = new ModernSimpleTextButton(this, Component.m_237113_("transaction"), Icon.empty()) {
            @Override
            public void onClicked(MouseButton mouseButton) {
               if (!PlayerWallet.this.widgets.contains(PlayerWallet.this.transferPanel)) {
                  PlayerWallet.this.transferPanel.openGui();
               }
            }
         });
      }
   }

   public void alignWidgets() {
      this.currenciesPanel.setPosAndSize(4, 24, this.width / 2 - 7, this.height - 29);
      this.payPanel.setPosAndSize(this.currenciesPanel.width + 8, 24, this.width / 2 - 7, this.height - 29);
      if (currency != null && recipient != null) {
         this.transaction.setPosAndSize(this.width / 2 - 25, 7, 50, 10);
      }
   }

   public boolean drawDefaultBackground(GuiGraphics graphics) {
      return false;
   }

   public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
      RenderHelper.drawRoundedRect(graphics, x, y, w, h, 5, RGBA.create(0, 0, 0, 127));
      PlayerInfo info = new PlayerInfo(Minecraft.m_91087_().m_91094_().m_92548_(), false);
      graphics.m_280411_(info.m_105337_(), x + 3, y + 3, 15, 15, 8.0F, 8.0F, 8, 8, 64, 64);
      graphics.m_280411_(info.m_105337_(), x + 3, y + 3, 15, 15, 40.0F, 8.0F, 8, 8, 64, 64);
   }
}
