package net.sixik.sdmshop.utils;

import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.Widget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.sixik.sdmshop.old_api.CustomIcon;
import net.sixik.sdmshop.old_api.screen.ShopUIRenderComponent;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmuilib.client.utils.GLHelper;
import net.sixik.sdmuilib.client.utils.math.Vector2;
import net.sixik.sdmuilib.client.utils.misc.RGBA;

public class ShopRenderUtils {
   private static final Font FONT = Minecraft.m_91087_().f_91062_;

   public static int centerTextX(Widget widget, String text, int x) {
      return centerTextX(widget, FONT.m_92895_(text), x);
   }

   public static int centerTextX(Widget widget, int textW, int x) {
      return centerTextX(widget.width, textW, x);
   }

   public static int centerTextX(int panelW, int textW, int x) {
      return x + (panelW - textW) / 2;
   }

   public static int centerTextX(int panelW, String text, int x) {
      return centerTextX(panelW, FONT.m_92895_(text), x);
   }

   public static int centerTextX(int panelW, String text, int x, float scale) {
      return centerTextX(panelW, FONT.m_92895_(text), x, scale);
   }

   public static int centerTextX(int panelW, int textW, int x, float scale) {
      int scaledTextW = (int)(textW * scale);
      return x + (panelW - scaledTextW) / 2;
   }

   public static int centerTextXFromMiddle(int panelW, String text, int x) {
      return centerTextXFromMiddle(panelW, Theme.DEFAULT.getStringWidth(text), x);
   }

   public static int centerTextXFromMiddle(int panelW, int textW, int x) {
      int panelCenterX = x + panelW / 2;
      int textCenterX = textW / 2;
      return panelCenterX - textCenterX;
   }

   public static void drawLabel(GuiGraphics graphics, Theme theme, Vector2 pos, Vector2 size, String left, String right) {
      int lineHeight = 9;
      RGBA.create(0, 0, 0, 127).drawRoundFill(graphics, pos.x, pos.y, size.x / 2 - 2, lineHeight + 1, 4);
      GLHelper.pushScissor(graphics, pos.x, pos.y, size.x / 2 - 2, lineHeight + 1);
      theme.drawString(graphics, left, pos.x + 2, pos.y + 1, Color4I.WHITE, 2);
      GLHelper.popScissor(graphics);
      RGBA.create(0, 0, 0, 127).drawRoundFill(graphics, pos.x + size.x / 2, pos.y, size.x / 2, lineHeight + 1, 4);
      GLHelper.pushScissor(graphics, pos.x + size.x / 2, pos.y, size.x / 2 - 2, lineHeight + 1);
      theme.drawString(graphics, right, pos.x + size.x / 2 + 2, pos.y + 1, Color4I.WHITE, 2);
      GLHelper.popScissor(graphics);
   }

   public static void drawLabel(GuiGraphics graphics, Theme theme, Vector2 pos, Vector2 size, ShopUIRenderComponent left, ShopUIRenderComponent right) {
      int lineHeight = 9;
      RGBA.create(0, 0, 0, 127).drawRoundFill(graphics, pos.x, pos.y, size.x / 2 - 2, lineHeight + 1, 4);
      GLHelper.pushScissor(graphics, pos.x, pos.y + 1, size.x / 2 - 2, lineHeight + 1);
      left.draw(graphics, theme, pos.x, pos.y, size.x, size.y);
      GLHelper.popScissor(graphics);
      RGBA.create(0, 0, 0, 127).drawRoundFill(graphics, pos.x + size.x / 2, pos.y, size.x / 2, lineHeight + 1, 4);
      GLHelper.pushScissor(graphics, pos.x + size.x / 2, pos.y, size.x / 2 - 2, lineHeight + 1);
      right.draw(graphics, theme, pos.x + size.x / 2, pos.y + 1, size.x, size.y);
      GLHelper.popScissor(graphics);
   }

   public static Icon getIconFromEntry(ShopEntry entry) {
      return getIconFromEntry(entry, ShopUtilsClient.getTick());
   }

   public static Icon getIconFromEntry(ShopEntry entry, int tick) {
      if (entry == null) {
         return Icon.empty();
      }

      Icon i1 = null;
      if ((entry.getRenderComponent().getIcon().isEmpty() || entry.getRenderComponent().getIcon() instanceof ItemIcon itemIcon && itemIcon.isEmpty())
         && entry.getEntryType() instanceof CustomIcon customIcon) {
         i1 = customIcon.getCustomIcon(entry, tick);
      }

      if (i1 == null) {
         i1 = entry.getRenderComponent().getIcon();
      }

      return i1;
   }
}
