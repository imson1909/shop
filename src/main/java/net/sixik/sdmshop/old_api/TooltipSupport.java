package net.sixik.sdmshop.old_api;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.StringConfig;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;

public interface TooltipSupport {
   List<String> getTooltips();

   default void getTooltipConfig(ConfigGroup group) {
      ConfigGroup tooltipGroup = group.getOrCreateSubgroup("tooltips").setNameKey("sdm.shop.tooltips");
      tooltipGroup.addList("tooltip", this.getTooltips(), new StringConfig(null), "").setNameKey("sdm.shop.tooltips.tooltip");
   }

   default void serializeTooltips(CompoundTag nbt) {
      ListTag listTag = new ListTag();

      for (String tooltip : this.getTooltips()) {
         listTag.add(StringTag.m_129297_(tooltip));
      }

      nbt.m_128365_("tooltip_list", listTag);
   }

   default void deserializeTooltips(CompoundTag nbt) {
      if (nbt.m_128441_("tooltip_list")) {
         ListTag listTag = (ListTag)nbt.m_128423_("tooltip_list");
         this.getTooltips().clear();

         for (Tag tag : listTag) {
            this.getTooltips().add(tag.m_7916_());
         }
      }
   }

   default void addTooltipToList(TooltipList list) {
      for (String tooltip : this.getTooltips()) {
         list.add(Component.m_237115_(tooltip));
      }
   }
}
