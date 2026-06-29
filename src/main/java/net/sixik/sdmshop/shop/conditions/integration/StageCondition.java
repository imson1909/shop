package net.sixik.sdmshop.shop.conditions.integration;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.StringConfig;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.sixik.sdmshop.old_api.shop.AbstractShopCondition;
import net.sixik.sdmshop.old_api.shop.ShopObject;
import net.sixik.sdmshop.utils.ShopNBTUtils;
import net.sixik.sdmshop.utils.StagesUtils;

public class StageCondition extends AbstractShopCondition {
   protected List<String> stages;

   public StageCondition() {
      this(new ArrayList<>());
   }

   protected StageCondition(List<String> stages) {
      this.stages = stages;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public boolean isLocked(ShopObject shopObject) {
      return !this.stages.stream().allMatch(s -> StagesUtils.hasStage(Minecraft.m_91087_().f_91074_, s));
   }

   @Override
   public AbstractShopCondition copy() {
      return new StageCondition(this.stages);
   }

   @Override
   public void getConfig(ConfigGroup configGroup) {
      configGroup.addList("stages", this.stages, new StringConfig(), "").setNameKey("sdm.shop.conditions.stages");
   }

   @Override
   public String getId() {
      return "stageCondition";
   }

   public CompoundTag serialize() {
      CompoundTag nbt = new CompoundTag();
      ShopNBTUtils.putList(nbt, "stages", this.stages, StringTag::m_129297_);
      return nbt;
   }

   public void deserialize(CompoundTag tag) {
      ShopNBTUtils.getList(tag, "stages", Tag::m_7916_, this.stages);
   }
}
