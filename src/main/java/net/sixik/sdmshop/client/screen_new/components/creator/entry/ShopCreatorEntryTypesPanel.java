package net.sixik.sdmshop.client.screen_new.components.creator.entry;

import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.Widget;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.Function;
import net.minecraft.client.gui.GuiGraphics;
import net.sixik.sdmshop.client.screen_new.api.GUIShopMenu;
import net.sixik.sdmshop.client.screen_new.components.creator.data.SelectedCreatorEnum;
import net.sixik.sdmshop.client.screen_new.components.creator.data.ShopCreatorComponentData;
import net.sixik.sdmshop.old_api.shop.AbstractEntryType;
import net.sixik.sdmshop.registers.ShopContentRegister;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.utils.rendering.ShopRenderingWrapper;

public class ShopCreatorEntryTypesPanel extends Panel {
   public static List<AbstractEntryType> Cached = null;
   public static final ShopEntry EMPTY = new ShopEntry(null);
   private int padding = 6;
   private int spacingX = 6;
   private int spacingY = 6;
   public final ShopCreatorEntryPanel parentPanel;

   public ShopCreatorEntryTypesPanel(ShopCreatorEntryPanel panel) {
      super(panel);
      this.parentPanel = panel;
   }

   public boolean shouldDraw() {
      return ShopCreatorComponentData.Data.SelectedCreator == SelectedCreatorEnum.Entry;
   }

   public void addWidgets() {
      if (Cached == null) {
         Cached = new ObjectArrayList();

         for (Entry<String, Function<ShopEntry, AbstractEntryType>> entrySet : ShopContentRegister.getEntryTypes().entrySet()) {
            AbstractEntryType entryType = entrySet.getValue().apply(EMPTY);
            if (entryType != null && entryType.isModLoaded()) {
               Cached.add(entryType);
            }
         }

         Cached.sort((o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(o1.getId(), o2.getId()));
      }

      for (int i = 0; i < Cached.size(); i++) {
         this.add(new ShopCreatorEntryTypesPanel.Button(this, Cached.get(i)));
      }
   }

   public void alignWidgets() {
      int zoneW = this.width - this.padding * 2;
      if (zoneW <= 0) {
         this.setHeight(this.padding * 2);
      } else {
         List<Widget> list = this.widgets;
         if (list.isEmpty()) {
            this.setHeight(this.padding * 2);
         } else {
            int y = this.padding;
            boolean anyRow = false;
            List<Widget> row = new ObjectArrayList(16);
            int i = 0;

            while (i < list.size()) {
               row.clear();
               int rowW = 0;
               int rowH = 0;

               while (i < list.size()) {
                  Widget w = list.get(i++);
                  if (w.isEnabled()) {
                     int ww = Math.max(1, w.width);
                     int wh = Math.max(1, w.height);
                     if (ww > zoneW) {
                        ww = zoneW;
                        w.setSize(ww, wh);
                     }

                     int add = row.isEmpty() ? ww : this.spacingX + ww;
                     if (!row.isEmpty() && rowW + add > zoneW) {
                        i--;
                        break;
                     }

                     row.add(w);
                     rowW += add;
                     rowH = Math.max(rowH, wh);
                  }
               }

               if (!row.isEmpty()) {
                  anyRow = true;
                  int offsetX = Math.max(0, (zoneW - rowW) / 2);
                  int x = this.padding + offsetX;

                  for (int k = 0; k < row.size(); k++) {
                     Widget w = row.get(k);
                     int ww = Math.min(Math.max(1, w.width), zoneW);
                     w.posX = x;
                     w.posY = y;
                     x += ww + this.spacingX;
                  }

                  y += rowH + this.spacingY;
               }
            }

            if (!anyRow) {
               this.setHeight(this.padding * 2);
            } else {
               y -= this.spacingY;
               i = y + this.padding;
               if (this.height != i) {
                  this.setHeight(i);
               }
            }
         }
      }
   }

   public class Button extends SimpleTextButton {
      public final AbstractEntryType entryType;

      public Button(Panel panel, AbstractEntryType entryType) {
         super(panel, entryType.getTranslatableForCreativeMenu(), entryType.getCreativeIcon());
         this.entryType = entryType;
      }

      public boolean isSelected() {
         return Objects.equals(ShopCreatorComponentData.Data.Entry.selectedType, this.entryType);
      }

      public void onClicked(MouseButton button) {
         ShopCreatorComponentData.Data.Entry.selectedType = this.entryType;
         ShopCreatorEntryTypesPanel.this.parentPanel.modalPanel.onSelectEntryType();
      }

      public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
         if (this.isMouseOver) {
            ShopRenderingWrapper.drawRoundedRect(graphics.m_280168_(), x, y, w, h, 5.0F, 1.0F, -14737633, GUIShopMenu.BORDER_4_INT);
         } else if (this.isSelected()) {
            ShopRenderingWrapper.drawRoundedRect(graphics.m_280168_(), x, y, w, h, 5.0F, 1.0F, -14737633, GUIShopMenu.BORDER_3_INT);
         } else {
            ShopRenderingWrapper.drawRoundedRectNoBorder(graphics.m_280168_(), x, y, w, h, 5.0F, -14737633);
         }
      }
   }
}
