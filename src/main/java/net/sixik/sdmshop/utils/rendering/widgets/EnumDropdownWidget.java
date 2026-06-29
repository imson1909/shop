package net.sixik.sdmshop.utils.rendering.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.Widget;
import dev.ftb.mods.ftblibrary.ui.input.Key;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sixik.sdmshop.client.screen_new.api.GUIShopMenu;
import net.sixik.sdmshop.utils.rendering.ShopRenderingWrapper;

public class EnumDropdownWidget<E extends Enum<E>> extends Widget {
   private final Class<E> enumClass;
   private final E[] values;
   private E value;
   private boolean open;
   private int itemHeight = 12;
   private int maxVisibleItems = 8;
   private Function<E, Component> label = e -> Component.m_237113_(e.name());
   private Consumer<E> onChange = e -> {};

   public EnumDropdownWidget(Panel p, Class<E> enumClass, E initial) {
      super(p);
      this.enumClass = enumClass;
      this.values = enumClass.getEnumConstants();
      this.value = initial;
   }

   public EnumDropdownWidget<E> setLabel(Function<E, Component> label) {
      this.label = label != null ? label : e -> Component.m_237113_(e.name());
      return this;
   }

   public EnumDropdownWidget<E> onChange(Consumer<E> cb) {
      this.onChange = cb != null ? cb : e -> {};
      return this;
   }

   public E getValue() {
      return this.value;
   }

   public void setValue(E v) {
      if (v != null && v != this.value) {
         this.value = v;
         this.onChange.accept(v);
      }
   }

   public boolean isOpen() {
      return this.open;
   }

   public void setOpen(boolean v) {
      this.open = v;
   }

   public Component getTitle() {
      return this.label.apply(this.value);
   }

   public void draw(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
      theme.drawButton(graphics, x, y, w, h, this.getWidgetType());
      Component t = this.label.apply(this.value);
      theme.drawString(graphics, t, x + 4, y + (h - theme.getFontHeight() + 1) / 2, theme.getContentColor(this.getWidgetType()), 2);
      theme.drawString(
         graphics,
         Component.m_237113_(this.open ? "▲" : "▼"),
         x + w - 10,
         y + (h - theme.getFontHeight() + 1) / 2,
         theme.getContentColor(this.getWidgetType()),
         0
      );
      if (this.open) {
         PoseStack pose = graphics.m_280168_();
         pose.m_85836_();
         pose.m_252880_(0.0F, 0.0F, 200.0F);
         int listX = x;
         int listY = y + h + 2;
         int visible = Math.min(this.values.length, this.maxVisibleItems);
         int listH = visible * this.itemHeight;
         theme.drawPanelBackground(graphics, listX, listY, w, listH);
         int mouseX = this.getMouseX();
         int mouseY = this.getMouseY();

         for (int i = 0; i < visible; i++) {
            int iy = listY + i * this.itemHeight;
            boolean hover = mouseX >= listX && mouseX < listX + w && mouseY >= iy && mouseY < iy + this.itemHeight;
            if (hover) {
               ShopRenderingWrapper.drawRoundedRect(
                  graphics.m_280168_(), listX, iy, w, this.itemHeight, 2.0F, 1.0F, GUIShopMenu.BACKGROUND_INT, GUIShopMenu.BORDER_3_INT
               );
            }

            Component it = this.label.apply(this.values[i]);
            theme.drawString(graphics, it, listX + 4, iy + (this.itemHeight - theme.getFontHeight() + 1) / 2, theme.getContentColor(this.getWidgetType()), 0);
         }

         pose.m_85849_();
      }
   }

   public boolean mousePressed(MouseButton button) {
      if (!this.isEnabled()) {
         return false;
      }

      int x = this.getX();
      int y = this.getY();
      if (this.isMouseOver()) {
         this.open = !this.open;
         this.playClickSound();
         return true;
      }

      if (this.open) {
         int listX = x;
         int listY = y + this.height + 2;
         int visible = Math.min(this.values.length, this.maxVisibleItems);
         int listH = visible * this.itemHeight;
         int mx = this.getMouseX();
         int my = this.getMouseY();
         boolean insideList = mx >= listX && mx < listX + this.width && my >= listY && my < listY + listH;
         if (!insideList) {
            this.open = false;
            return false;
         }

         int idx = (my - listY) / this.itemHeight;
         if (idx >= 0 && idx < visible) {
            this.setValue(this.values[idx]);
            this.open = false;
            this.playClickSound();
            return true;
         }
      }

      return false;
   }

   public boolean keyPressed(Key key) {
      if (!this.open) {
         return false;
      } else if (key.keyCode == 256) {
         this.open = false;
         return true;
      } else {
         return false;
      }
   }
}
