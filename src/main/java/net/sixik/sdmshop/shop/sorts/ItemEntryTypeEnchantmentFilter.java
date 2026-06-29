package net.sixik.sdmshop.shop.sorts;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.PanelScrollBar;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.TextField;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.Widget;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.client.screen_new.api.FilterPanelWidget;
import net.sixik.sdmshop.client.screen_new.api.FilterRefreshWidget;
import net.sixik.sdmshop.client.screen_new.api.GUIShopMenu;
import net.sixik.sdmshop.old_api.shop.AbstractEntryType;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.shop.ShopTab;
import net.sixik.sdmshop.shop.entry_types.ItemEntryType;
import net.sixik.sdmshop.utils.ShopUtils;
import net.sixik.sdmshop.utils.rendering.ShopRenderingWrapper;
import net.sixik.sdmuilib.client.utils.misc.RGBA;

public class ItemEntryTypeEnchantmentFilter extends AbstractEntryTypeFilter<ItemEntryType> {
   protected final Object2ObjectOpenHashMap<Enchantment, Int2IntOpenHashMap> collected = new Object2ObjectOpenHashMap();
   protected final Object2ObjectOpenHashMap<Enchantment, Int2IntOpenHashMap> selected = new Object2ObjectOpenHashMap();

   public ItemEntryTypeEnchantmentFilter(Class<? extends AbstractEntryType> sortElementClass) {
      super(sortElementClass);
   }

   protected boolean isSupported(ItemEntryType entryType) {
      ItemStack item = entryType.getItemStack();
      return !item.m_41785_().isEmpty();
   }

   protected void collectFrom(ItemEntryType entryType) {
      ListTag tags = entryType.getItemStack().m_41785_();
      if (!tags.isEmpty()) {
         Map<Enchantment, Integer> one = ShopUtils.deserializeEnchantments(tags);
         if (!one.isEmpty()) {
            for (Entry<Enchantment, Integer> entry : one.entrySet()) {
               Enchantment ench = entry.getKey();
               int lvl = entry.getValue() != null ? entry.getValue() : 0;
               if (ench != null && lvl > 0) {
                  ((Int2IntOpenHashMap)this.collected.computeIfAbsent(ench, k -> new Int2IntOpenHashMap())).addTo(lvl, 1);
               }
            }
         }
      }
   }

   protected boolean sort(ShopEntry entry, ShopTab tab, ItemEntryType entryType) {
      ItemStack itemStack = entryType.getItemStack();
      if (this.selected.isEmpty()) {
         return true;
      }

      if (!itemStack.m_41793_()) {
         return false;
      }

      Map<Enchantment, Integer> itemMap = ShopUtils.deserializeEnchantments(itemStack.m_41785_());
      if (itemMap.isEmpty()) {
         return false;
      }

      for (Entry<Enchantment, Integer> it : itemMap.entrySet()) {
         Enchantment ench = it.getKey();
         int lvl = it.getValue() != null ? it.getValue() : 0;
         Int2IntOpenHashMap allowedLvls = (Int2IntOpenHashMap)this.selected.get(ench);
         if (allowedLvls != null && (allowedLvls.isEmpty() || allowedLvls.containsKey(lvl))) {
            return true;
         }
      }

      return false;
   }

   @Override
   public Component getTitle() {
      return Component.m_237113_("Enchantments");
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void addWidget(Panel panel) {
      panel.add(new ItemEntryTypeEnchantmentFilter.MainPanel(panel, this));
   }

   public Map<Enchantment, Int2IntOpenHashMap> getCollectedSorted() {
      LinkedHashMap<Enchantment, Int2IntOpenHashMap> out = new LinkedHashMap<>();
      this.collected.keySet().stream().sorted(Comparator.comparing(e -> String.valueOf(BuiltInRegistries.f_256876_.m_7981_(e)))).forEach(ench -> {
         Int2IntOpenHashMap levels = (Int2IntOpenHashMap)this.collected.get(ench);
         out.put(ench, levels);
      });
      return out;
   }

   public static class Button extends SimpleTextButton {
      protected final ItemEntryTypeEnchantmentFilter.EnchantmentListBox listBox;
      public boolean selected = false;

      public Button(ItemEntryTypeEnchantmentFilter.EnchantmentListBox panel, Component txt) {
         super(panel, txt, Icon.empty());
         this.listBox = panel;
      }

      public void setSelected(boolean selected) {
         this.selected = selected;
      }

      public boolean isSelected() {
         return this.selected;
      }

      public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
         if (!this.selected && !this.isMouseOver) {
            ShopRenderingWrapper.drawRoundedRectNoBorder(graphics.m_280168_(), x, y, w, h, 5.0F, -14737633);
         } else {
            ShopRenderingWrapper.drawRoundedRect(graphics.m_280168_(), x, y, w, h, 5.0F, 1.0F, -14737633, GUIShopMenu.BORDER_3_INT);
         }
      }

      public void onClicked(MouseButton button) {
      }

      public void updateSelected() {
      }
   }

   public static class EnchHeaderButton extends ItemEntryTypeEnchantmentFilter.Button {
      public final Enchantment ench;

      public EnchHeaderButton(ItemEntryTypeEnchantmentFilter.EnchantmentListBox panel, Component txt, Enchantment ench) {
         super(panel, txt);
         this.ench = ench;
         this.height = 12;
      }

      @Override
      public void onClicked(MouseButton button) {
         Object2ObjectOpenHashMap<Enchantment, Int2IntOpenHashMap> sel = this.listBox.filter.selected;
         if (sel.containsKey(this.ench)) {
            sel.remove(this.ench);
         } else {
            sel.put(this.ench, new Int2IntOpenHashMap());
         }

         this.listBox.updateAllSelected();
      }

      @Override
      public void updateSelected() {
         this.selected = this.listBox.filter.selected.get(this.ench) != null;
      }
   }

   public static class EnchLevelButton extends ItemEntryTypeEnchantmentFilter.Button {
      public final Enchantment ench;
      public final int level;
      public final int count;

      public EnchLevelButton(ItemEntryTypeEnchantmentFilter.EnchantmentListBox panel, Component txt, Enchantment ench, int level, int count) {
         super(panel, txt);
         this.ench = ench;
         this.level = level;
         this.count = count;
         this.height = 12;
      }

      @Override
      public void onClicked(MouseButton button) {
         Object2ObjectOpenHashMap<Enchantment, Int2IntOpenHashMap> sel = this.listBox.filter.selected;
         Int2IntOpenHashMap levels = (Int2IntOpenHashMap)sel.computeIfAbsent(this.ench, e -> new Int2IntOpenHashMap());

         try {
            if (levels.isEmpty()) {
               levels.put(this.level, 1);
               return;
            }

            if (levels.containsKey(this.level)) {
               levels.remove(this.level);
               if (levels.isEmpty()) {
                  sel.remove(this.ench);
               }
            } else {
               levels.put(this.level, 1);
            }
         } finally {
            this.listBox.updateAllSelected();
         }
      }

      @Override
      public void updateSelected() {
         Int2IntOpenHashMap levels = (Int2IntOpenHashMap)this.listBox.filter.selected.get(this.ench);
         if (levels == null) {
            this.selected = false;
         } else if (levels.isEmpty()) {
            this.selected = true;
         } else {
            this.selected = levels.containsKey(this.level);
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class EnchantmentListBox extends Panel {
      protected final ItemEntryTypeEnchantmentFilter filter;
      private Map<Enchantment, Int2IntOpenHashMap> sortEnch;

      public EnchantmentListBox(Panel panel, ItemEntryTypeEnchantmentFilter filter) {
         super(panel);
         this.filter = filter;
      }

      public void addWidgets() {
         if (this.sortEnch == null || this.sortEnch.isEmpty()) {
            this.sortEnch = this.filter.getCollectedSorted();
         }

         if (this.sortEnch != null && !this.sortEnch.isEmpty()) {
            for (Entry<Enchantment, Int2IntOpenHashMap> e : this.sortEnch.entrySet()) {
               Enchantment ench = e.getKey();
               Int2IntOpenHashMap levels = e.getValue();
               this.add(new ItemEntryTypeEnchantmentFilter.EnchHeaderButton(this, Component.m_237115_(ench.m_44704_()), ench));
               int[] ks = levels.keySet().toIntArray();
               Arrays.sort(ks);

               for (int lvl : ks) {
                  int count = levels.get(lvl);
                  this.add(new ItemEntryTypeEnchantmentFilter.EnchLevelButton(this, levelComponent(lvl), ench, lvl, count));
               }
            }
         }
      }

      private static Component levelComponent(int lvl) {
         return Component.m_237113_(String.valueOf(lvl));
      }

      public void alignWidgets() {
         this.width -= 8;
         this.posX = 4;
         int paddingX = 6;
         int paddingY = 6;
         int headerH = 12;
         int chipH = 12;
         int headerGap = 4;
         int chipGap = 4;
         int rowGap = 8;
         int zoneW = this.width - 12;
         if (zoneW > 0) {
            int x = 6;
            int y = 6;
            List<Widget> ws = this.getWidgets();
            if (!ws.isEmpty()) {
               Font font = Minecraft.m_91087_().f_91062_;
               boolean inGroup = false;
               boolean placedChip = false;

               for (int i = 0; i < ws.size(); i++) {
                  Widget w = ws.get(i);
                  if (w instanceof ItemEntryTypeEnchantmentFilter.EnchHeaderButton) {
                     if (inGroup) {
                        if (placedChip) {
                           y += 20;
                        } else {
                           y += 8;
                        }

                        placedChip = false;
                     }

                     w.setWidth(zoneW);
                     w.setHeight(12);
                     w.posX = 6;
                     w.posY = y;
                     y += 16;
                     x = 6;
                     inGroup = true;
                  } else {
                     placedChip = true;
                     String txt = w.getTitle().getString();
                     int wW = font.m_92895_(txt) + 10;
                     wW = Mth.m_14045_(wW, 18, zoneW);
                     w.setWidth(wW);
                     w.setHeight(12);
                     if (x != 6 && x + wW > 6 + zoneW) {
                        x = 6;
                        y += 16;
                     }

                     w.posX = x;
                     w.posY = y;
                     x += wW + 4;
                  }
               }

               if (inGroup) {
                  if (placedChip) {
                     y += 20;
                  } else {
                     y += 8;
                  }
               }

               TextField textField = new TextField(this) {
                  public void draw(GuiGraphics graphics, Theme theme, int xx, int yx, int w, int h) {
                  }
               };
               textField.setHeight((int)(9.0 * 1.5));
               textField.posY = ((Widget)this.widgets.get(this.widgets.size() - 1)).posY + 9;
               this.add(textField);
            }
         }
      }

      public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
         ShopRenderingWrapper.beginBatch(w, h, 4.0F, 1.0F);
         ShopRenderingWrapper.addBatchRect(graphics, x, y, w, h, GUIShopMenu.BACKGROUND_INT, GUIShopMenu.BORDER_INT);
         ShopRenderingWrapper.endBatch();
      }

      public void updateAllSelected() {
         List<Widget> list = this.getWidgets();

         for (int i = 0; i < list.size(); i++) {
            if (list.get(i) instanceof ItemEntryTypeEnchantmentFilter.Button button) {
               button.updateSelected();
            }
         }

         this.filter.applyChange();
      }
   }

   public static class MainPanel extends Panel implements FilterRefreshWidget, FilterPanelWidget {
      private final ItemEntryTypeEnchantmentFilter filter;
      protected ItemEntryTypeEnchantmentFilter.EnchantmentListBox listBox;
      protected PanelScrollBar listBoxScroll;

      public MainPanel(Panel panel, ItemEntryTypeEnchantmentFilter filter) {
         super(panel);
         this.filter = filter;
      }

      public void addWidgets() {
         this.add(this.listBox = new ItemEntryTypeEnchantmentFilter.EnchantmentListBox(this, this.filter));
         this.add(this.listBoxScroll = new PanelScrollBar(this, this.listBox) {
            public void drawScrollBar(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
               SDMShopClient.someColor.draw(graphics, x, y, w, h);
            }

            public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
               RGBA.create(0, 0, 0, 127).draw(graphics, x, y, w, h, 0.0F);
            }
         });
      }

      public void alignWidgets() {
         this.listBox.width = this.width;
         this.listBox.height = this.height - 1;
         this.listBoxScroll.setPosAndSize(this.listBox.getPosX() + this.listBox.getWidth() - 2, this.listBox.getPosY(), 2, this.listBox.getHeight());
         this.listBox.clearWidgets();
         this.listBox.addWidgets();
         this.listBox.alignWidgets();
      }

      @Override
      public int getPanelHeight(int panelH) {
         return panelH / 3;
      }

      @Override
      public void updateWidget() {
         this.clearWidgets();
         this.addWidgets();
         this.alignWidgets();
         this.listBox.updateAllSelected();
      }

      @Override
      public void setFocus(boolean value) {
      }
   }
}
