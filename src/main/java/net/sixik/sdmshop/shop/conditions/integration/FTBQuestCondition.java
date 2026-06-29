package net.sixik.sdmshop.shop.conditions.integration;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftbquests.FTBQuestsAPIImpl;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.util.ConfigQuestObject;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.sixik.sdmshop.old_api.shop.AbstractShopCondition;
import net.sixik.sdmshop.old_api.shop.ShopObject;

public class FTBQuestCondition extends AbstractShopCondition {
   public static final String QUEST_ID_KEY = "quest_id";
   protected long questID;

   public FTBQuestCondition() {
      this(0L);
   }

   public FTBQuestCondition(long id) {
      this.questID = id;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public boolean isLocked(ShopObject shopObject) {
      TeamData data = TeamData.get(Minecraft.m_91087_().f_91074_);
      Quest quest = FTBQuestsAPIImpl.INSTANCE.getQuestFile(true).getQuest(this.questID);
      return quest == null ? true : !data.isCompleted(quest);
   }

   @Override
   public AbstractShopCondition copy() {
      return new FTBQuestCondition(this.questID);
   }

   @Override
   public void getConfig(ConfigGroup configGroup) {
      ((ConfigQuestObject)configGroup.add(
            "quest_id", new ConfigQuestObject(v -> v instanceof Quest obj), FTBQuestsAPIImpl.INSTANCE.getQuestFile(true).get(this.questID), v -> {
               if (v != null) {
                  this.questID = v.id;
               }
            }, null
         ))
         .setNameKey("sdm.shop.conditions.quest_id");
   }

   @Override
   public String getId() {
      return "questTypeCondition";
   }

   public CompoundTag serialize() {
      CompoundTag nbt = new CompoundTag();
      nbt.m_128356_("quest_id", this.questID);
      return nbt;
   }

   public void deserialize(CompoundTag tag) {
      this.questID = tag.m_128454_("quest_id");
   }
}
