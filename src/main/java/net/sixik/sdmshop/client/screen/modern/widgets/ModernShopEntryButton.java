package net.sixik.sdmshop.client.screen.modern.widgets;

import dev.ftb.mods.ftblibrary.ui.GuiHelper;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sixik.sdmshop.client.screen.base.widgets.AbstractShopEntryButton;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.utils.ShopRenderUtils;
import net.sixik.sdmuilib.client.utils.GLHelper;
import net.sixik.sdmuilib.client.utils.TextHelper;
import net.sixik.sdmuilib.client.utils.math.Vector2;
import net.sixik.sdmuilib.client.utils.math.Vector2f;
import net.sixik.sdmuilib.client.utils.misc.RGBA;

public class ModernShopEntryButton extends AbstractShopEntryButton {
   public ModernShopEntryButton(Panel panel, ShopEntry entry) {
      super(panel, entry);
   }

   public ModernShopEntryButton(Panel panel, ShopEntry entry, boolean isEdit) {
      super(panel, entry, isEdit);
   }

   public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
      if (this.isSelected()) {
         RGBA.create(255, 255, 255, 85).drawRoundFill(graphics, x, y, w, h, 6);
      } else {
         RGBA.create(0, 0, 0, 85).drawRoundFill(graphics, x, y, w, h, 6);
      }
   }

   public void draw(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
      GuiHelper.setupDrawing();
      this.drawBackground(graphics, theme, x, y, w, h);
      if (this.shopEntry != null) {
         int size = 16;
         getIconFromEntry(this.shopEntry).draw(graphics, x + size / 2, y + 2, w - size, h - size);
         RGBA.create(0, 0, 0, 85).drawRoundFill(graphics, x + 2, y + w + 2, w - 4, 8, 2);
         Component component = this.shopEntry.getType().isSell() ? Component.m_237115_("sdm.shop.entry.sell") : Component.m_237115_("sdm.shop.entry.buy");
         Vector2f textSize = TextHelper.getTextRenderSize(component.getString(), w, 0.7F, 50);
         int textWidth = (int)TextHelper.getTextWidth(component.getString(), textSize.y);
         int centeredX = x + 2 + (w - 4 - textWidth) / 2;
         Vector2 pos = new Vector2(centeredX, y + h - 9 - 1);
         GLHelper.pushTransform(graphics, pos, new Vector2(1, 1), textSize.y, 0.0F);
         theme.drawString(graphics, component, pos.x, pos.y);
         GLHelper.popTransform(graphics);
         String textMoney = this.shopEntry.getEntrySellerType().moneyToString(this.shopEntry);
         textSize = TextHelper.getTextRenderSize(textMoney, w - 4, 0.7F, 50);
         textWidth = this.shopEntry.getEntrySellerType().getRenderWight(graphics, theme, pos.x, pos.y, w, 16, this.shopEntry.getPrice(), this, 0);
         centeredX = ShopRenderUtils.centerTextX(w - 4, textWidth, x, textSize.y);
         pos = new Vector2(centeredX, y + w + 2 + 1);
         GLHelper.pushTransform(graphics, pos, new Vector2(1, 1), textSize.y, 0.0F);
         this.shopEntry.getEntrySellerType().draw(graphics, theme, pos.x, pos.y, w, 16, this.shopEntry.getPrice(), this, 0);
         GLHelper.popTransform(graphics);
      } else {
         int size = this.height / 2;
         this.drawIcon(graphics, theme, x + size / 2, y + size / 2, w - size, h - size);
      }

      this.drawFavorite(graphics, x, y, w, h);
   }
}
