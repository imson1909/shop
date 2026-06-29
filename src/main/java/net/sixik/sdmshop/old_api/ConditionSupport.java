package net.sixik.sdmshop.old_api;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Map.Entry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.sixik.sdmshop.old_api.shop.AbstractShopCondition;
import net.sixik.sdmshop.old_api.shop.ShopObject;
import net.sixik.sdmshop.registers.ShopContentRegister;
import net.sixik.sdmshop.shop.BaseShop;

public interface ConditionSupport {
   String CONDITION_KEY = "conditions";

   List<AbstractShopCondition> getConditions();

   default boolean addCondition(AbstractShopCondition condition) {
      return condition == null ? false : this.getConditions().add(condition);
   }

   default boolean removeCondition(AbstractShopCondition condition) {
      boolean result = this.getConditions().removeIf(s -> s.equals(condition));
      if (!result) {
         result = this.getConditions().removeIf(s -> s.getId().equals(condition.getId()));
      }

      return result;
   }

   default Optional<AbstractShopCondition> getCondition(int index) {
      int size = this.getConditions().size();
      return index >= 0 && index < size ? Optional.ofNullable(this.getConditions().get(index)) : Optional.empty();
   }

   default boolean isLockedAny(ShopObject shopObject) {
      return !this.getConditions().isEmpty() && this.getConditions().stream().anyMatch(s -> s.isLocked(shopObject));
   }

   default boolean isLockedAll(ShopObject shopObject) {
      return !this.getConditions().isEmpty() && this.getConditions().stream().allMatch(s -> s.isLocked(shopObject));
   }

   default void serializeConditions(CompoundTag nbt) {
      ListTag listTag = new ListTag();

      for (AbstractShopCondition condition : this.getConditions()) {
         CompoundTag conditionNbt = new CompoundTag();
         conditionNbt.m_128359_("id", condition.getId());
         conditionNbt.m_128365_("data", condition.serialize());
         listTag.add(conditionNbt);
      }

      nbt.m_128365_("conditions", listTag);
   }

   default void deserializeConditions(CompoundTag tag, BaseShop shopBase) {
      this.getConditions().clear();
      List<String> conditionsIds = new ArrayList<>();
      if (tag.m_128441_("conditions")) {
         for (Tag tag1 : (ListTag)tag.m_128423_("conditions")) {
            CompoundTag conditionNbt = (CompoundTag)tag1;
            String id = conditionNbt.m_128461_("id");
            Optional<Constructor<? extends AbstractShopCondition>> find = ShopContentRegister.getCondition(id);
            if (!find.isEmpty()) {
               AbstractShopCondition condition = find.get().createDefaultInstance();
               conditionsIds.add(id);
               condition.deserialize(conditionNbt.m_128469_("data"));
               condition.setShop(shopBase);
               this.addCondition(condition);
            }
         }
      }

      for (Entry<String, Constructor<? extends AbstractShopCondition>> entry : ShopContentRegister.getConditions().entrySet()) {
         if (!conditionsIds.contains(entry.getKey())) {
            AbstractShopCondition value = entry.getValue().createDefaultInstance();
            value.setShop(shopBase);
            this.addCondition(value);
         }
      }
   }

   default void getConditionConfig(ConfigGroup group) {
      ConfigGroup conditionGroup = group.getOrCreateSubgroup("conditions").setNameKey("sdm.shop.conditions");

      for (AbstractShopCondition condition : this.getConditions()) {
         condition.getConfig(conditionGroup);
      }
   }
}
