package net.sixik.sdmshop.utils.rendering.widgets;

import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.TextBox;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.sixik.sdmshop.utils.ShopUtils;

public class IntTextBox extends TextBox {
   protected int min;
   protected int max;
   protected int value;
   protected Consumer<Integer> onValueChange;

   public IntTextBox(Panel panel, int min, int max, int value, Consumer<Integer> onValueChange) {
      super(panel);
      this.min = min;
      this.max = max;
      this.value = value;
      this.onValueChange = onValueChange;
   }

   public boolean isValid(String txt) {
      return ShopUtils.isDigitsInRange(txt, this.min, this.max);
   }

   public void addMouseOverText(TooltipList list) {
      list.add(Component.m_237113_("From " + this.min + " to " + this.max));
   }

   public void onTextChanged() {
      String t = this.getText();
      if (ShopUtils.isDigitsInRange(t, this.min, this.max)) {
         this.onValueChange.accept(Integer.parseInt(t));
      }
   }
}
