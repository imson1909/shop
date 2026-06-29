package net.sixik.sdmshop.shop.entry_types.configs;

import dev.ftb.mods.ftblibrary.config.ItemStackConfig;
import dev.ftb.mods.ftblibrary.config.ui.SelectItemStackScreen;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftblibrary.ui.GuiHelper;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.sixik.sdmshop.client.screen_new.components.creator.custom.CustomEntryConfig;
import net.sixik.sdmshop.client.screen_new.components.creator.data.ShopCreatorComponentData;
import net.sixik.sdmshop.client.screen_new.components.creator.entry.ShopCreatorEntryPanel;
import net.sixik.sdmshop.client.screen_new.components.creator.entry.ShopCreatorEntryTypesPanel;
import net.sixik.sdmshop.shop.ShopEntry;

public class ItemEntryTypeConfig extends CustomEntryConfig {
   private static final List<Component> tooltips = new ObjectArrayList();
   protected SimpleTextButton itemButton;
   protected SelectItemStackScreen selectItemStackScreen;

   @OnlyIn(Dist.CLIENT)
   @Override
   public void addWidgets(ShopCreatorEntryPanel panel, BooleanSupplier musBeRender, ShopCreatorComponentData data) {
      ItemStack lastItem = data.Entry.lastSelectedItemStack;
      Icon itemIcon;
      if (lastItem.m_41619_()) {
         itemIcon = Icons.ADD;
      } else {
         itemIcon = ItemIcon.getItemIcon(lastItem);
      }

      panel.add(this.itemButton = new SimpleTextButton(panel, Component.m_237119_(), itemIcon) {
         public void onClicked(MouseButton button) {
            ItemStackConfig config = new ItemStackConfig(false, false);
            config.setValue(data.Entry.lastSelectedItemStack);
            ItemEntryTypeConfig.this.selectItemStackScreen = new SelectItemStackScreen(config, callback -> {
               if (callback) {
                  ItemStack item = config.getValue();
                  data.Entry.lastSelectedItemStack = item;
                  ItemEntryTypeConfig.this.itemButton.setIcon(ItemIcon.getItemIcon(item));
                  ItemEntryTypeConfig.tooltips.clear();
                  GuiHelper.addStackTooltip(item, ItemEntryTypeConfig.tooltips);
               }

               ItemEntryTypeConfig.this.selectItemStackScreen.closeGui();
            });
            ItemEntryTypeConfig.this.selectItemStackScreen.openGui();
         }

         public void addMouseOverText(TooltipList list) {
            if (data.Entry.lastSelectedItemStack.m_41619_()) {
               list.add(Component.m_237113_("Select Item"));
            } else {
               ItemEntryTypeConfig.tooltips.forEach(list::add);
            }
         }
      });
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void alignWidgets(ShopCreatorEntryPanel panel, ShopCreatorEntryTypesPanel entryTypesPanel, ShopCreatorComponentData data) {
      this.itemButton.posX = 4;
      this.itemButton.posY = entryTypesPanel.height + 4;
      this.itemButton.setSize(32, 32);
   }

   @Override
   public void applyCreate(ShopCreatorComponentData data, ShopEntry shopEntry) {
   }
}
