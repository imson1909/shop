package net.sixik.sdmshop.client.screen.modern.wallet;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.SimpleButton;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.SimpleButton.Callback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;

public class OpenWalletButton extends SimpleButton {
   public float alpha = 1.0F;
   public boolean s = false;

   public OpenWalletButton(Panel panel, Component text, Icon icon, Callback c) {
      super(panel, text, icon, c);
   }

   public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
      PlayerInfo info = new PlayerInfo(Minecraft.m_91087_().m_91094_().m_92548_(), false);
      RenderSystem.enableBlend();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
      graphics.m_280411_(info.m_105337_(), x, y, w, h, 8.0F, 8.0F, 8, 8, 64, 64);
      graphics.m_280411_(info.m_105337_(), x, y, w, h, 40.0F, 8.0F, 8, 8, 64, 64);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.disableBlend();
      if (this.s) {
         this.alpha += 0.002F;
      } else {
         this.alpha -= 0.002F;
      }

      if (this.alpha >= 1.0F || this.alpha <= 0.5F) {
         this.s = !this.s;
      }
   }

   public boolean shouldAddMouseOverText() {
      return false;
   }
}
