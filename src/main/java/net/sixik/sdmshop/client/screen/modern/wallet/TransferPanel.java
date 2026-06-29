package net.sixik.sdmshop.client.screen.modern.wallet;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.BaseScreen;
import dev.ftb.mods.ftblibrary.ui.TextField;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.sixik.sdmuilib.client.utils.RenderHelper;
import net.sixik.sdmuilib.client.utils.misc.RGBA;

public class TransferPanel extends BaseScreen {
   public TextField titel;
   public PlayerInfo player;
   public TextField label;
   public ModernTextBox count;
   public ModernSimpleTextButton accept;

   public boolean onInit() {
      this.setWidth(this.getScreen().m_85445_() / 3);
      this.setHeight(this.getScreen().m_85446_() / 3);
      return true;
   }

   public void addWidgets() {
      this.add(this.titel = new TextField(this));
      this.add(this.label = new TextField(this));
      this.add(this.count = new ModernTextBox(this));
      this.add(this.accept = new ModernSimpleTextButton(this, Component.m_237113_("null"), Icon.empty()) {
         @Override
         public void onClicked(MouseButton mouseButton) {
         }
      });
   }

   public void alignWidgets() {
      this.titel.setText(Component.m_237113_("Transfer of funds"));
      this.titel.setPos(this.width / 2 - this.titel.getWidth() / 2, 5);
      this.label.setText(Component.m_237113_("Transfer amount:"));
      this.label.setScale(0.7F);
      this.label.setPos(10, this.height / 2);
      this.count.setPosAndSize(this.label.width + 13, this.height / 2 - 2, 50, 10);
      this.accept.setPos(this.width / 2 - this.accept.width / 2, this.height - this.accept.height - 4);
   }

   public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
      RenderHelper.drawRoundedRect(graphics, x, y, w, h, 2, RGBA.create(0, 0, 0, 127));
      PlayerInfo info = new PlayerInfo(Minecraft.m_91087_().m_91094_().m_92548_(), false);
      graphics.m_280411_(info.m_105337_(), x + 20, y + h / 5, 15, 15, 8.0F, 8.0F, 8, 8, 64, 64);
      graphics.m_280411_(info.m_105337_(), x + 20, y + h / 5, 15, 15, 40.0F, 8.0F, 8, 8, 64, 64);
      Icons.RIGHT.draw(graphics, x + w / 2 - 10, y + h / 5, 20, 15);
      graphics.m_280411_(PlayerWallet.recipient.m_105337_(), x + w - 35, y + h / 5, 15, 15, 8.0F, 8.0F, 8, 8, 64, 64);
      graphics.m_280411_(PlayerWallet.recipient.m_105337_(), x + w - 35, y + h / 5, 15, 15, 40.0F, 8.0F, 8, 8, 64, 64);
   }
}
