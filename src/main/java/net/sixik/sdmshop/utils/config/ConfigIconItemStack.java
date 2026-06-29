package net.sixik.sdmshop.utils.config;

import dev.ftb.mods.ftblibrary.config.ConfigCallback;
import dev.ftb.mods.ftblibrary.config.ImageResourceConfig;
import dev.ftb.mods.ftblibrary.config.ItemStackConfig;
import dev.ftb.mods.ftblibrary.config.ui.SelectImageResourceScreen;
import dev.ftb.mods.ftblibrary.config.ui.SelectItemStackScreen;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftblibrary.ui.Widget;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.sixik.sdmshop.registers.ShopItemRegisters;
import org.jetbrains.annotations.Nullable;

public class ConfigIconItemStack extends ItemStackConfig {
   public ConfigIconItemStack() {
      super(false, true);
   }

   public void onClicked(Widget clickedWidget, MouseButton button, ConfigCallback callback) {
      if (this.getCanEdit()) {
         if (button.isRight()) {
            ImageResourceConfig imageConfig = new ImageResourceConfig();
            new SelectImageResourceScreen(imageConfig, accepted -> {
               if (accepted) {
                  if (!((ResourceLocation)imageConfig.getValue()).equals(ImageResourceConfig.NONE)) {
                     ItemStack stack = new ItemStack((ItemLike)ShopItemRegisters.CUSTOM_ICON.get());
                     stack.m_41700_("Icon", StringTag.m_129297_(((ResourceLocation)imageConfig.getValue()).toString()));
                     this.setCurrentValue(stack);
                  } else {
                     this.setCurrentValue(ItemStack.f_41583_);
                  }
               }

               callback.save(accepted);
            }).openGui();
         } else {
            new SelectItemStackScreen(this, callback).openGui();
         }
      }
   }

   public static class CustomIconItem extends Item {
      public CustomIconItem() {
         super(new Properties().m_41487_(1));
      }

      public void m_7373_(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
         tooltip.add(Component.m_237115_("item.ftbquests.custom_icon.tooltip").m_130940_(ChatFormatting.GRAY));
         if (stack.m_41782_() && stack.m_41783_().m_128441_("Icon")) {
            tooltip.add(Component.m_237113_(stack.m_41783_().m_128461_("Icon")).m_130940_(ChatFormatting.DARK_GRAY));
         } else {
            tooltip.add(Component.m_237113_("-").m_130940_(ChatFormatting.DARK_GRAY));
         }
      }

      public static Icon getIcon(ItemStack stack) {
         if (!(stack.m_41720_() instanceof ConfigIconItemStack.CustomIconItem)) {
            return ItemIcon.getItemIcon(stack);
         } else {
            return stack.m_41782_() && stack.m_41783_().m_128441_("Icon")
               ? Icon.getIcon(stack.m_41783_().m_128461_("Icon"))
               : Icon.getIcon("minecraft:textures/misc/unknown_pack.png");
         }
      }
   }
}
