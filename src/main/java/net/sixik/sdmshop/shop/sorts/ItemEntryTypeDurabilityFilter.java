package net.sixik.sdmshop.shop.sorts;

import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.TextBox;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.sixik.sdmshop.client.screen_new.api.FilterRefreshWidget;
import net.sixik.sdmshop.old_api.shop.AbstractEntryType;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.shop.ShopTab;
import net.sixik.sdmshop.shop.entry_types.ItemEntryType;
import net.sixik.sdmshop.utils.ShopUtils;

public class ItemEntryTypeDurabilityFilter extends AbstractEntryTypeFilter<ItemEntryType> {
   protected int damageFromPercent = 0;
   protected int damageToPercent = 100;

   public ItemEntryTypeDurabilityFilter(Class<? extends AbstractEntryType> sortElementClass) {
      super(sortElementClass);
   }

   protected boolean isSupported(ItemEntryType entryType) {
      return entryType.getItemStack().m_41763_();
   }

   protected boolean sort(ShopEntry entry, ShopTab tab, ItemEntryType entryType) {
      ItemStack stack = entryType.getItemStack();
      int max = stack.m_41776_();
      if (max <= 0) {
         return true;
      }

      int dmg = stack.m_41773_();
      int percent = (int)Math.round((max - dmg) * 100.0 / max);
      return percent >= this.damageFromPercent && percent <= this.damageToPercent;
   }

   @Override
   public Component getTitle() {
      return Component.m_237113_("Damage %");
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void addWidget(Panel panel) {
      panel.add(new ItemEntryTypeDurabilityFilter.InputPanel(panel, s -> {
         this.damageFromPercent = s;
         this.applyChange();
      }, s -> {
         this.damageToPercent = s;
         this.applyChange();
      }));
   }

   @OnlyIn(Dist.CLIENT)
   protected class InputPanel extends Panel implements FilterRefreshWidget {
      private final Consumer<Integer> from;
      private final Consumer<Integer> to;
      protected TextBox fromBox;
      protected TextBox toBox;
      private boolean syncing;

      public InputPanel(Panel panel, Consumer<Integer> from, Consumer<Integer> to) {
         super(panel);
         this.from = from;
         this.to = to;
      }

      public void addWidgets() {
         this.add(this.fromBox = new TextBox(this) {
            public void onTextChanged() {
               InputPanel.this.applyFromChanged();
            }
         });
         this.add(this.toBox = new TextBox(this) {
            public void onTextChanged() {
               InputPanel.this.applyToChanged();
            }
         });
         this.fromBox.setFilter(ShopUtils.DIGITS_0_100);
         this.toBox.setFilter(ShopUtils.DIGITS_0_100);
         this.fromBox.setText(String.valueOf(ItemEntryTypeDurabilityFilter.this.damageFromPercent));
         this.toBox.setText(String.valueOf(ItemEntryTypeDurabilityFilter.this.damageToPercent));
      }

      private void applyFromChanged() {
         if (!this.syncing) {
            String txt = this.fromBox.getText();
            if (txt != null && !txt.isEmpty()) {
               int newFrom = clamp0_100(parseIntSafe(txt, ItemEntryTypeDurabilityFilter.this.damageFromPercent));
               int curTo = ItemEntryTypeDurabilityFilter.this.damageToPercent;
               if (newFrom > curTo) {
                  curTo = newFrom;
               }

               this.syncing = true;

               try {
                  ItemEntryTypeDurabilityFilter.this.damageFromPercent = newFrom;
                  ItemEntryTypeDurabilityFilter.this.damageToPercent = curTo;
                  this.fromBox.setText(String.valueOf(newFrom));
                  this.toBox.setText(String.valueOf(curTo));
                  this.from.accept(newFrom);
                  this.to.accept(curTo);
               } finally {
                  this.syncing = false;
               }
            }
         }
      }

      private void applyToChanged() {
         if (!this.syncing) {
            String txt = this.toBox.getText();
            if (txt != null && !txt.isEmpty()) {
               int newTo = clamp0_100(parseIntSafe(txt, ItemEntryTypeDurabilityFilter.this.damageToPercent));
               int curFrom = ItemEntryTypeDurabilityFilter.this.damageFromPercent;
               if (newTo < curFrom) {
                  curFrom = newTo;
               }

               this.syncing = true;

               try {
                  ItemEntryTypeDurabilityFilter.this.damageFromPercent = curFrom;
                  ItemEntryTypeDurabilityFilter.this.damageToPercent = newTo;
                  this.fromBox.setText(String.valueOf(curFrom));
                  this.toBox.setText(String.valueOf(newTo));
                  this.from.accept(curFrom);
                  this.to.accept(newTo);
               } finally {
                  this.syncing = false;
               }
            }
         }
      }

      private static int clamp0_100(int v) {
         return v < 0 ? 0 : Math.min(v, 100);
      }

      private static int parseIntSafe(String s, int def) {
         try {
            return Integer.parseInt(s);
         } catch (Exception e) {
            return def;
         }
      }

      public void alignWidgets() {
         int elementW = this.width / 2 - 2;
         this.fromBox.setWidth(elementW);
         this.fromBox.setHeight(this.height);
         this.toBox.posX = elementW + 2;
         this.toBox.setWidth(elementW);
         this.toBox.setHeight(this.height);
      }

      @Override
      public void updateWidget() {
         this.clearWidgets();
         this.addWidgets();
         this.alignWidgets();
      }

      @Override
      public void setFocus(boolean value) {
         this.fromBox.setFocused(value);
         this.toBox.setFocused(value);
      }
   }
}
