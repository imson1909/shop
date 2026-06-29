package net.sixik.sdmshop.utils;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.ConfigValue;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.sixik.sdmshop.old_api.ConfigSupport;
import net.sixik.sdmshop.old_api.IconRenderSupport;
import net.sixik.sdmshop.registers.ShopItemRegisters;
import net.sixik.sdmshop.utils.config.ConfigIconItemStack;

public class RenderComponent implements IconRenderSupport, ConfigSupport, DataSerializerCompoundTag {
   public static final String KEY = "render_component";
   protected ItemStack icon = ItemStack.f_41583_;

   public RenderComponent updateIcon(ItemStack icon) {
      this.icon = icon;
      return this;
   }

   @Override
   public Icon getIcon() {
      return this.icon.m_150930_((Item)ShopItemRegisters.CUSTOM_ICON.get())
         ? ConfigIconItemStack.CustomIconItem.getIcon(this.icon)
         : ItemIcon.getItemIcon(this.icon);
   }

   @Override
   public void getConfig(ConfigGroup group) {
      ConfigGroup renderGroup = group.getOrCreateSubgroup("render").setNameKey("sdm.shop.render");
      ConfigValue<ItemStack> value = ((ConfigIconItemStack)renderGroup.add(
            "icon", new ConfigIconItemStack(), this.icon, v -> this.icon = v, Items.f_42127_.m_7968_()
         ))
         .setNameKey("sdm.shop.render.icon");
   }

   public CompoundTag serialize() {
      CompoundTag nbt = new CompoundTag();
      if (this.icon != ItemStack.f_41583_) {
         ShopNBTUtils.putItemStack(nbt, "icon", this.icon);
      }

      return nbt;
   }

   public void deserialize(CompoundTag tag) {
      if (tag.m_128441_("icon")) {
         this.icon = ShopNBTUtils.getItemStack(tag, "icon");
      }
   }
}
