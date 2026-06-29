package net.sixik.sdmshop.client.screen_new;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.Widget;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.sixik.sdmshop.client.screen.modern.wallet.PlayerWallet;
import net.sixik.sdmshop.client.screen_new.components.creator.ShopCreatorComponentModalPanel;
import net.sixik.sdmshop.utils.ShopUtils;
import org.joml.Math;

public class MainShopToolPanel extends Panel {
   public int borderOffest = 2;
   public int spacing = 2;

   public MainShopToolPanel(Panel panel) {
      super(panel);
   }

   public void addWidgets() {
      if (ShopUtils.isEditModeClient()) {
         this.add(
            new MainShopToolPanel.ToolButton(
               this,
               Icons.ADD,
               () -> ShopCreatorComponentModalPanel.openCentered(this.getGui()),
               tooltipList -> tooltipList.add(Component.m_237113_("Create WIP"))
            )
         );
      }

      this.add(new MainShopToolPanel.ToolButton(this, Icons.MONEY, new PlayerWallet()::openGui, tooltipList -> tooltipList.add(Component.m_237113_("Money"))));
   }

   public void alignWidgets() {
      int ITEM_W = this.width / 4;
      int ITEM_H = ITEM_W;

      for (Widget widget : this.widgets) {
         widget.setSize(ITEM_W, ITEM_H);
      }

      int zoneW = this.width - this.borderOffest * 2;
      if (zoneW > 0) {
         int startX = this.borderOffest;
         int startY = this.borderOffest;
         List<Widget> list = this.widgets;
         if (!list.isEmpty()) {
            int y = startY;
            List<Widget> row = new ObjectArrayList(16);
            int i = 0;

            while (i < list.size()) {
               row.clear();
               int rowW = 0;
               int rowH = 0;

               while (i < list.size()) {
                  Widget w = list.get(i++);
                  if (w.isEnabled()) {
                     int wW = Math.clamp(w.width, 1, zoneW);
                     int wH = java.lang.Math.max(1, w.height);
                     int add = row.isEmpty() ? wW : this.spacing + wW;
                     if (!row.isEmpty() && rowW + add > zoneW) {
                        i--;
                        break;
                     }

                     row.add(w);
                     rowW += add;
                     rowH = java.lang.Math.max(rowH, wH);
                  }
               }

               if (!row.isEmpty()) {
                  int offsetX = java.lang.Math.max(0, (zoneW - rowW) / 2);
                  int x = startX + offsetX;

                  for (int k = 0; k < row.size(); k++) {
                     Widget w = row.get(k);
                     int wW = Math.clamp(w.width, 1, zoneW);
                     w.posX = x;
                     w.posY = y;
                     x += wW + this.spacing;
                  }

                  y += rowH + this.spacing;
               }
            }
         }
      }
   }

   public static class ToolButton extends SimpleTextButton {
      protected final Runnable onClick;
      protected final Consumer<TooltipList> onTooltip;

      public ToolButton(Panel panel, Component component, Runnable onClick, Consumer<TooltipList> onTooltip) {
         this(panel, component, Icon.empty(), onClick, onTooltip);
      }

      public ToolButton(Panel panel, Icon icon, Runnable onClick, Consumer<TooltipList> onTooltip) {
         this(panel, Component.m_237119_(), icon, onClick, onTooltip);
      }

      public ToolButton(Panel panel, Component component, Icon icon, Runnable onClick, Consumer<TooltipList> onTooltip) {
         super(panel, component, icon);
         this.onTooltip = onTooltip;
         this.onClick = onClick;
      }

      public void addMouseOverText(TooltipList list) {
         if (this.onTooltip != null) {
            this.onTooltip.accept(list);
         }
      }

      public void onClicked(MouseButton button) {
         if (button.isLeft()) {
            this.onClick.run();
         }
      }

      public void draw(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
         this.drawBackground(graphics, theme, x, y, w, h);
         int s = h >= 16 ? 16 : 8;
         int off = (h - s) / 2;
         FormattedText title = this.getTitle();
         int sw = title == null ? 0 : theme.getStringWidth(title);
         boolean hasTitle = sw > 0;
         boolean hasIcon = this.hasIcon();
         if (hasIcon && !hasTitle) {
            int ix = x + (w - s) / 2;
            int iy = y + (h - s) / 2;
            this.drawIcon(graphics, theme, ix, iy, s, s);
         } else {
            int textY = y + (h - theme.getFontHeight() + 1) / 2;
            int iconBlockW = hasIcon ? off + s : 0;
            int mw = w - iconBlockW - 6;
            if (hasTitle && sw > mw) {
               title = theme.trimStringToWidth(title, mw);
               sw = mw;
            }

            boolean centerTitle = !hasIcon || this.renderTitleInCenter();
            int textX;
            if (centerTitle) {
               if (hasIcon) {
                  textX = x + iconBlockW + (mw - sw + 6) / 2;
               } else {
                  textX = x + (w - sw) / 2;
               }
            } else {
               textX = x + 4 + iconBlockW;
            }

            if (hasIcon) {
               this.drawIcon(graphics, theme, x + off, y + off, s, s);
            }

            if (hasTitle) {
               theme.drawString(graphics, title, textX, textY, theme.getContentColor(this.getWidgetType()), 2);
            }
         }
      }
   }
}
