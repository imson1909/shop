package net.sixik.sdmshop.client.screen.modern.wallet;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.SimpleButton;
import dev.ftb.mods.ftblibrary.ui.TextField;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.sixik.sdmuilib.client.utils.RenderHelper;
import net.sixik.sdmuilib.client.utils.misc.RGBA;

public class PayCardRender extends Panel {
   public PlayerInfo player;
   public TextField nick;
   public SimpleButton select;

   public PayCardRender(Panel panel, PlayerInfo player) {
      super(panel);
      this.player = player;
   }

   public void addWidgets() {
      this.add(this.nick = new TextField(this));
      this.add(this.select = new SimpleButton(this, Component.m_237113_("null"), Icon.empty(), (simpleButton, mouseButton) -> {
         if (PlayerWallet.recipient == null) {
            PlayerWallet.recipient = this.player;
         } else {
            PlayerWallet.recipient = null;
         }

         this.parent.getGui().refreshWidgets();
      }));
   }

   public void alignWidgets() {
      this.nick.setText(this.player.m_105312_().getName());
      this.nick.setPos(22, 3);
      this.select.setPosAndSize(0, 0, 200, 22);
   }

   public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
      if (PlayerWallet.recipient == this.player) {
         RenderHelper.drawRoundedRect(graphics, x, y, w, h, 5, RGBA.create(255, 255, 255, 140));
      } else {
         RenderHelper.drawRoundedRect(graphics, x, y, w, h, 5, RGBA.create(255, 255, 255, 110));
      }

      graphics.m_280411_(this.player.m_105337_(), x + 3, y + 3, 15, 15, 8.0F, 8.0F, 8, 8, 64, 64);
      graphics.m_280411_(this.player.m_105337_(), x + 3, y + 3, 15, 15, 40.0F, 8.0F, 8, 8, 64, 64);
   }
}
