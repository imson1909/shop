package net.sixik.sdmshop.client.screen.modern.panels;

import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.gui.GuiGraphics;
import net.sixik.sdmshop.client.screen.base.panels.AbstractShopTabPanel;
import net.sixik.sdmuilib.client.utils.GLHelper;
import net.sixik.sdmuilib.client.utils.misc.RGBA;

public class ModernShopTabPanel extends AbstractShopTabPanel {
   public ModernShopTabPanel(Panel panel) {
      super(panel);
   }

   public void addWidgets() {
   }

   public void alignWidgets() {
   }

   public void draw(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
      GLHelper.pushScissor(graphics, x, y, w, h);
      super.draw(graphics, theme, x, y, w, h);
      GLHelper.popScissor(graphics);
   }

   public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
      RGBA.create(0, 0, 0, 127).draw(graphics, x, y, w, h, 0.0F);
   }
}
