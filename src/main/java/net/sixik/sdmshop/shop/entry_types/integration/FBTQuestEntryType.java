package net.sixik.sdmshop.shop.entry_types.integration;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftbquests.FTBQuestsAPIImpl;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.util.ConfigQuestObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.sixik.sdmshop.old_api.shop.AbstractEntryType;
import net.sixik.sdmshop.old_api.shop.EntryTypeProperty;
import net.sixik.sdmshop.shop.ShopEntry;

public class FBTQuestEntryType extends AbstractEntryType {
   protected long questID;

   public FBTQuestEntryType(ShopEntry shopEntry) {
      this(shopEntry, 0L);
   }

   public FBTQuestEntryType(ShopEntry shopEntry, long questID) {
      super(shopEntry);
      this.questID = questID;
   }

   @Override
   public AbstractEntryType copy() {
      return new FBTQuestEntryType(this.shopEntry, this.questID);
   }

   @Override
   public boolean onBuy(Player player, ShopEntry entry, int countBuy) {
      TeamData data = TeamData.get(player);
      Quest quest = FTBQuestsAPIImpl.INSTANCE.getQuestFile(false).getQuest(this.questID);
      if (quest == null) {
         return false;
      } else if (!data.isCompleted(quest)) {
         data.setCompleted(this.questID, new Date(System.currentTimeMillis()));
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean onSell(Player player, ShopEntry entry, int countBuy) {
      TeamData data = TeamData.get(player);
      Quest quest = FTBQuestsAPIImpl.INSTANCE.getQuestFile(false).getQuest(this.questID);
      if (quest == null) {
         return false;
      } else if (data.isCompleted(quest)) {
         data.setCompleted(this.questID, null);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean canExecute(Player player, ShopEntry entry, int countBuy) {
      TeamData data = TeamData.get(player);
      Quest quest = FTBQuestsAPIImpl.INSTANCE.getQuestFile(player.m_7578_()).getQuest(this.questID);
      if (quest != null && data.isCompleted(quest)) {
         return false;
      }

      double playerMoney = entry.getEntrySellerType().getMoney(player, entry);
      return playerMoney >= entry.getPrice() * countBuy;
   }

   @Override
   public int howMany(Player player, ShopEntry entry) {
      TeamData data = TeamData.get(player);
      Quest quest = FTBQuestsAPIImpl.INSTANCE.getQuestFile(player.m_7578_()).getQuest(this.questID);
      if (quest == null) {
         return 0;
      } else if (entry.getType().isSell()) {
         return !data.isCompleted(quest) ? 1 : 0;
      } else if (data.isCompleted(quest)) {
         return 0;
      } else {
         double playerMoney = entry.getEntrySellerType().getMoney(player, entry);
         if (entry.getPrice() == 0.0) {
            return 1;
         } else {
            return (int)(playerMoney / entry.getPrice()) >= 1 ? 1 : 0;
         }
      }
   }

   @Override
   public Component getTranslatableForCreativeMenu() {
      return Component.m_237115_("sdm.shop.entry.creator.type.integration.quest");
   }

   @Override
   public String getModNameForContextMenu() {
      return "FTB Quests";
   }

   @Override
   public String getModId() {
      return "ftbquests";
   }

   @Override
   public List<Component> getDescriptionForContextMenu() {
      List<Component> list = new ArrayList<>();
      list.add(Component.m_237115_("sdm.shop.entry.creator.type.questType.description"));
      list.add(Component.m_237115_("sdm.shop.entry.creator.type.questType.description_1"));
      return list;
   }

   @Override
   public void getConfig(ConfigGroup group) {
      group.add("quest_id", new ConfigQuestObject(v -> v instanceof Quest obj), FTBQuestsAPIImpl.INSTANCE.getQuestFile(true).get(this.questID), v -> {
         if (v != null) {
            this.questID = v.id;
         }
      }, null);
   }

   @Override
   public String getId() {
      return "questType";
   }

   @Override
   public boolean isSearch(String search) {
      return false;
   }

   public CompoundTag serialize() {
      CompoundTag nbt = new CompoundTag();
      nbt.m_128356_("questID", this.questID);
      return nbt;
   }

   public void deserialize(CompoundTag tag) {
      this.questID = tag.m_128454_("questID");
   }

   @Override
   public EntryTypeProperty getProperty() {
      return EntryTypeProperty.DEFAULT;
   }
}
