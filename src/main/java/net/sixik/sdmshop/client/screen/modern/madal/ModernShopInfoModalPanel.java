package net.sixik.sdmshop.client.screen.modern.madal;

import dev.ftb.mods.ftblibrary.ui.ModalPanel;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.gui.GuiGraphics;
import net.sixik.sdmuilib.client.utils.math.Vector2;
import net.sixik.sdmuilib.client.utils.misc.RGBA;

public class ModernShopInfoModalPanel extends ModalPanel {
   protected Vector2 position;

   public ModernShopInfoModalPanel(Panel panel) {
      super(panel);
      this.position = new Vector2(panel.width / 2, panel.height / 2);
      Vector2 size = new Vector2(panel.width / 8, panel.height / 8);
      this.setSize(this.position.x - size.x, this.position.y - size.y);
   }

   public void addWidgets() {
   }

   public void alignWidgets() {
   }

   public void drawBackground(GuiGraphics graphics, Theme theme, int _x, int _y, int w, int h) {
      int x = this.position.x - w / 2;
      int y = this.position.y - h / 2;
      RGBA.create(0, 0, 0, 127).draw(graphics, x, y, w, h, 0.0F);
   }
}
