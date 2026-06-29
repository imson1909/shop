package net.sixik.sdmshop.client.screen_new;

import dev.ftb.mods.ftblibrary.ui.GuiHelper;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.TextField;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.Widget;
import dev.ftb.mods.ftblibrary.ui.Widget.DrawLayer;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.client.screen_new.api.GUIShopMenu;
import net.sixik.sdmshop.mixin.accessors.PanelAccessor;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.utils.mixin.WidgetPath;
import net.sixik.sdmshop.utils.rendering.ShopRenderingWrapper;

public class MainShopEntryPanel extends Panel {
   public static int PADDING = 8;
   public static int SPACING_X = 6;
   public static int SPACING_Y = 6;
   public static int MIN_ITEM_W = 72;
   public static int MAX_COLS = 6;
   public static int FIXED_ITEM_H = 72;
   protected final GUIShopMenu screen;
   protected final PanelAccessor panelAccessor;
   protected ObjectArrayList<MainShopEntryButton> entryButtons;

   public MainShopEntryPanel(GUIShopMenu screen) {
      super(screen.self());
      this.screen = screen;
      this.panelAccessor = (PanelAccessor)this;
   }

   public void addWidgets() {
      this.clearWidgets();
      List<ShopEntry> list = SDMShopClient.CurrentShop.getEntries();

      for (int i = 0; i < list.size(); i++) {
         ShopEntry entry = list.get(i);
         if (this.shouldShowEntry(entry)) {
            MainShopEntryButton button = new MainShopEntryButton(this, entry);
            this.add(button);
         }
      }
   }

   public void sort() {
      if (this.screen instanceof MainShopScreen mainShopScreen) {
         mainShopScreen.leftPanel.sortEntries(this.getWidgets());
      }
   }

   protected boolean shouldShowEntry(ShopEntry entry) {
      return this.screen instanceof MainShopScreen mainShopScreen ? mainShopScreen.leftPanel.isSearched(entry) : true;
   }

   public void alignWidgets() {
      int padding = PADDING;
      int spacingX = SPACING_X;
      int spacingY = SPACING_Y;
      int zoneW = this.width - padding * 2;
      if (zoneW > 0) {
         List<Widget> ws = this.getWidgets();
         if (!ws.isEmpty()) {
            int itemH;
            if (FIXED_ITEM_H > 0) {
               itemH = FIXED_ITEM_H;
            } else {
               itemH = 0;

               for (int i = 0; i < ws.size(); i++) {
                  itemH = Math.max(itemH, Math.max(1, ws.get(i).height));
               }

               if (itemH <= 0) {
                  itemH = 20;
               }
            }

            int minItemW = MIN_ITEM_W;
            int cols = Math.max(1, (zoneW + spacingX) / (minItemW + spacingX));
            cols = Math.min(cols, MAX_COLS);
            int itemW = Math.max(1, (zoneW - (cols - 1) * spacingX) / cols);
            int x = padding;
            int y = padding;
            int col = 0;

            for (int i = 0; i < ws.size(); i++) {
               Widget w = ws.get(i);
               w.setWidth(itemW);
               w.setHeight(itemH);
               w.posX = x;
               w.posY = y;
               if (++col >= cols) {
                  col = 0;
                  x = padding;
                  y += itemH + spacingY;
               } else {
                  x += itemW + spacingX;
               }
            }

            TextField empty = new TextField(this) {
               public void draw(GuiGraphics graphics, Theme theme, int xx, int yx, int w, int h) {
               }
            };
            empty.setHeight(20);
            empty.setWidth(this.width);
            if (!this.widgets.isEmpty()) {
               Widget lastW = (Widget)this.widgets.get(this.widgets.size() - 1);
               empty.posY = lastW.posY + lastW.height;
               this.add(empty);
            }
         }
      }
   }

   public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
      ShopRenderingWrapper.beginBatch(w, h, 4.0F, 1.0F);
      ShopRenderingWrapper.addBatchRect(graphics, x, y, w, h, GUIShopMenu.BACKGROUND_INT, GUIShopMenu.BORDER_INT);
      ShopRenderingWrapper.endBatch();
   }

   public void draw(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
      boolean renderInside = this.getOnlyRenderWidgetsInside();
      this.drawBackground(graphics, theme, x, y, w, h);
      if (renderInside) {
         GuiHelper.pushScissor(this.getScreen(), x, y, w, h);
      }

      this.setOffset(true);
      int offsetX = this.panelAccessor.offsetX();
      int offsetY = this.panelAccessor.offsetY();
      this.widgets
         .stream()
         .filter(widgetx -> ((WidgetPath)widgetx).sdm$shouldRenderInLayer(DrawLayer.BACKGROUND, x, y, w, h))
         .forEach(widgetx -> this.drawWidget(graphics, theme, widgetx, x + offsetX, y + offsetY, w, h));
      this.drawOffsetBackground(graphics, theme, x + offsetX, y + offsetY, w, h);
      if (MainShopScreen.Instance.shouldRenderWidgets) {
         ShopRenderingWrapper.beginBatch(MIN_ITEM_W, FIXED_ITEM_H, 4.0F, 1.0F);

         for (int i = 0; i < this.widgets.size(); i++) {
            Widget widget = (Widget)this.widgets.get(i);
            if (widget instanceof MainShopEntryButton button && ((WidgetPath)widget).sdm$shouldRenderInLayer(DrawLayer.FOREGROUND, x, y, w, h)) {
               button.drawBatch(graphics, theme, widget.getX(), widget.getY(), widget.getWidth(), widget.getHeight());
            }
         }

         ShopRenderingWrapper.endBatch();
      }

      this.widgets
         .stream()
         .filter(widgetx -> ((WidgetPath)widgetx).sdm$shouldRenderInLayer(DrawLayer.FOREGROUND, x, y, w, h))
         .forEach(widgetx -> this.drawWidget(graphics, theme, widgetx, x + offsetX, y + offsetY, w, h));
      this.setOffset(false);
      if (renderInside) {
         GuiHelper.popScissor(this.getScreen());
      }
   }
}
