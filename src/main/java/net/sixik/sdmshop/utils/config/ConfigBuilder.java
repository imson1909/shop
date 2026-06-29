package net.sixik.sdmshop.utils.config;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.ConfigValue;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import java.util.Collection;
import java.util.function.Function;
import net.minecraft.network.chat.Component;

public class ConfigBuilder<T> {
   protected ConfigGroup group;
   protected Function<ConfigGroup, ConfigValue<T>> fun;
   protected TooltipList list = new TooltipList();

   public ConfigBuilder(ConfigGroup group, Function<ConfigGroup, ConfigValue<T>> fun) {
      this.group = group;
   }

   public ConfigBuilder<T> addTooltip(Component component) {
      this.list.add(component);
      return this;
   }

   public ConfigBuilder<T> addTooltip(Component... component) {
      for (Component component1 : component) {
         this.list.add(component1);
      }

      return this;
   }

   public ConfigBuilder<T> addTooltip(Collection<Component> components) {
      for (Component component1 : components) {
         this.list.add(component1);
      }

      return this;
   }

   public ConfigValue<T> getValue() {
      ConfigValue<T> t = this.fun.apply(this.group);
      t.addInfo(this.list);
      return t;
   }
}
