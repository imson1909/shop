package net.sixik.sdmshop.client.screen_new.api;

import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.TextBox;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.Widget;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.ui.misc.NordColors;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.client.screen_new.components.categories.ShopSelectCategoriesComponentModalPanel;
import net.sixik.sdmshop.shop.ShopTab;
import net.sixik.sdmshop.utils.rendering.ShopRenderingWrapper;
import org.jetbrains.annotations.Nullable;

public class GUIShopWidgets {
   public static class CategoryBox extends Panel {
      protected int borderOffest;
      protected int spacing;
      protected ObjectArrayList<ShopTab> selectedCategories = new ObjectArrayList();

      public CategoryBox(Panel panel) {
         this(panel, 2);
      }

      public CategoryBox(Panel panel, int spacing) {
         this(panel, 0, spacing);
      }

      public CategoryBox(Panel panel, int borderOffest, int spacing) {
         super(panel);
         this.borderOffest = borderOffest;
         this.spacing = spacing;
      }

      public void selectNewCategories(List<ShopTab> categories) {
         this.addWidgets();
         this.alignWidgets();
      }

      public void addWidgets() {
         this.clearWidgets();
         int size = (this.width - this.borderOffest * 2) / 2 - this.spacing;
         ObjectArrayList<ShopTab> list = this.selectedCategories;
         if (!list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
               GUIShopWidgets.CategoryBox.Button button = new GUIShopWidgets.CategoryBox.Button(this, (ShopTab)list.get(i), (s, b) -> {}) {
                  public boolean checkMouseOver(int mouseX, int mouseY) {
                     return false;
                  }
               };
               this.add(button);
               button.width = Math.min(button.width, size);
            }
         } else {
            GUIShopWidgets.CategoryBox.Button button = new GUIShopWidgets.CategoryBox.Button(this, null, (s, b) -> {}) {
               public boolean checkMouseOver(int mouseX, int mouseY) {
                  return false;
               }
            };
            this.add(button);
            button.width = Math.min(button.width, size);
         }
      }

      public void alignWidgets() {
         int zoneW = this.width - this.borderOffest * 2;
         int startX = this.borderOffest;
         int startY = this.borderOffest;
         List<Widget> list = this.getWidgets();
         if (!list.isEmpty() && zoneW > 0) {
            int x = startX;
            int y = startY;
            int rowH = 0;
            int line = 0;

            for (int i = 0; i < list.size(); i++) {
               Widget w = list.get(i);
               int wW = org.joml.Math.clamp(w.width, 1, this.width);
               int wH = Math.max(1, w.height);
               if (x != startX && x - startX + wW > zoneW) {
                  x = startX;
                  y += rowH + this.spacing;
                  rowH = 0;
                  line++;
               }

               w.posX = x;
               w.posY = y;
               x += wW + this.spacing;
               rowH = Math.max(rowH, wH);
            }
         }
      }

      public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
         ShopRenderingWrapper.beginBatch(w, h, 1.0F, 1.0F);
         ShopRenderingWrapper.addBatchRect(graphics, x - 1, y - 1, w + 2, h + 2, GUIShopMenu.EMPTY_INT, GUIShopMenu.BORDER_INT);
         ShopRenderingWrapper.endBatch();
      }

      public List<ShopTab> getSelectedCategories() {
         return this.selectedCategories;
      }

      public List<ShopTab> getCategories() {
         return SDMShopClient.CurrentShop.getTabs();
      }

      public static class Button extends SimpleTextButton {
         protected final Panel categoryBox;
         public final BiConsumer<MouseButton, ShopTab> onClick;
         public final ShopTab category;

         public Button(Panel panel, @Nullable ShopTab category, BiConsumer<MouseButton, ShopTab> onClick) {
            super(panel, (Component)(category == null ? Component.m_237115_("sdm.shop.gui.box.categories.empty_element") : category.title), Icon.empty());
            this.categoryBox = panel;
            this.category = category;
            this.onClick = onClick;
         }

         public void onClicked(MouseButton button) {
            this.onClick.accept(button, this.category);
         }

         public boolean renderTitleInCenter() {
            return true;
         }

         public void draw(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            this.drawBackground(graphics, theme, x, y, w, h);
            int s = h >= 20 ? 16 : 8;
            int off = (h - s) / 2;
            FormattedText title = this.getTitle();
            int textX = x;
            int mw = w - (this.hasIcon() ? off + s : 0) - 6;
            int rawW = theme.getStringWidth(title);
            float scale = 1.0F;
            if (rawW > mw && mw > 0) {
               float required = (float)mw / rawW;
               if (required >= 0.7F) {
                  scale = required;
               } else {
                  scale = 0.7F;
                  int maxRawWidth = (int)Math.floor(mw / scale);
                  title = theme.trimStringToWidth(title, Math.max(0, maxRawWidth));
                  rawW = theme.getStringWidth(title);
               }
            }

            float scaledW = rawW * scale;
            if (this.renderTitleInCenter()) {
               textX += (int)((mw - scaledW + 6.0F) / 2.0F);
            } else {
               textX += 4;
            }

            if (this.hasIcon()) {
               this.drawIcon(graphics, theme, x + off, y + off, s, s);
               textX += off + s;
            }

            int fontH = theme.getFontHeight();
            int scaledFontH = (int)Math.ceil(fontH * scale);
            int textY = y + (h - scaledFontH + 1) / 2;
            Color4I color = this.isMouseOver() ? NordColors.SNOW_STORM_3 : NordColors.SNOW_STORM_1;
            if (scale != 1.0F) {
               graphics.m_280168_().m_85836_();
               graphics.m_280168_().m_252880_(textX, textY, 0.0F);
               graphics.m_280168_().m_85841_(scale, scale, 1.0F);
               theme.drawString(graphics, title, 0, 0, color, 0);
               graphics.m_280168_().m_85849_();
            } else {
               theme.drawString(graphics, title, textX, textY, color, 0);
            }
         }

         public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            ShopRenderingWrapper.drawRoundedRectNoBorder(graphics.m_280168_(), x, y, w, h, 5.0F, -14737633);
         }
      }
   }

   public static class EditCategoryButton extends SimpleTextButton {
      private GUIShopWidgets.CategoryBox categoryBox;

      public EditCategoryButton(Panel panel, Component txt, GUIShopWidgets.CategoryBox categoryBox) {
         super(panel, txt, Icons.SETTINGS);
         this.categoryBox = categoryBox;
      }

      public void onClicked(MouseButton button) {
         ShopSelectCategoriesComponentModalPanel.openCentered(this.getGui(), this.categoryBox, this);
      }
   }

   public static class SearchBox extends TextBox {
      protected final Consumer<String> onTyped;

      public SearchBox(Panel panel, Consumer<String> onTyped) {
         super(panel);
         this.onTyped = onTyped;
         this.ghostText = I18n.m_118938_("sdm.shop.gui.box.search.ghost_text", new Object[0]);
      }

      public void drawTextBox(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
         ShopRenderingWrapper.drawRoundedRectNoBorder(graphics.m_280168_(), x, y, w, h, 6.0F, -14737633);
      }

      public void onTextChanged() {
         this.onTyped.accept(this.getText());
      }
   }
}
