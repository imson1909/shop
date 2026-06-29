package net.sixik.sdmshop.utils.rendering.widgets;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.Widget;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphics;

public class IconTooltipWidget extends Widget {
   protected final Icon icon;
   protected final Consumer<TooltipList> listConsumer;

   public IconTooltipWidget(Panel p, Icon icon, Consumer<TooltipList> listConsumer) {
      super(p);
      this.icon = icon;
      this.listConsumer = listConsumer;
   }

   public void addMouseOverText(TooltipList list) {
      this.listConsumer.accept(list);
   }

   public void draw(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
      if (!this.icon.isEmpty()) {
         this.icon.draw(graphics, x, y, w, h);
      }
   }
}
