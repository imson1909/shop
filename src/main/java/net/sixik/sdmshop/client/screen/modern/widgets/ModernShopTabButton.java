package net.sixik.sdmshop.client.screen.modern.widgets;

import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import java.util.Objects;
import net.minecraft.client.gui.GuiGraphics;
import net.sixik.sdmshop.client.screen.base.widgets.AbstractShopTabButton;
import net.sixik.sdmshop.shop.ShopTab;
import net.sixik.sdmuilib.client.utils.GLHelper;
import net.sixik.sdmuilib.client.utils.math.Vector2;
import net.sixik.sdmuilib.client.utils.misc.RGBA;
import net.sixik.sdmuilib.client.widgetsFake.text.SingleLineFakeWidget;

public class ModernShopTabButton extends AbstractShopTabButton {
   public SingleLineFakeWidget fakeWidget = new SingleLineFakeWidget(this.title);

   public ModernShopTabButton(Panel panel, ShopTab shopTab) {
      this(panel, shopTab, false);
   }

   public ModernShopTabButton(Panel panel, ShopTab shopTab, boolean isEdit) {
      super(panel, shopTab, isEdit);
   }

   public void draw(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
      this.drawBackground(graphics, theme, x, y, w, h);
      int s = h >= 16 ? 16 : 8;
      int textX = x;
      int textY = y + (h - theme.getFontHeight() + 1) / 2;
      if (this.hasIcon()) {
         this.drawIcon(graphics, theme, x + 1, y + 1, h - 2, h - 2);
         textX += 1 + h - 2;
      }

      int k = 1 + h - 2 - 8;
      if (this.shopTab != null) {
         if (this.getShopScreen().selectedTab != null && Objects.equals(this.getShopScreen().selectedTab, this.shopTab.getId())) {
            RGBA.create(255, 255, 255, 85).drawRoundFill(graphics, textX + 4, y + 2, this.width - k * 3 - 2, h - 4, 2);
         } else {
            RGBA.create(0, 0, 0, 85).drawRoundFill(graphics, textX + 4, y + 2, this.width - k * 3 - 2, h - 4, 2);
         }
      } else {
         RGBA.create(0, 0, 0, 85).drawRoundFill(graphics, textX + 4, y + 2, this.width - k * 3 - 2, h - 4, 2);
      }

      Vector2 pos = new Vector2(textX + 6, textY + 1);
      GLHelper.pushScissor(graphics, pos.x, pos.y, this.width - k * 3 - 5, h - 4);
      GLHelper.pushTransform(graphics, pos, new Vector2(1, 1), 0.7F, 0.0F);
      theme.drawString(graphics, this.title, pos.x, pos.y, theme.getContentColor(this.getWidgetType()), 2);
      GLHelper.popTransform(graphics);
      GLHelper.popScissor(graphics);
   }

   public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
   }
}
