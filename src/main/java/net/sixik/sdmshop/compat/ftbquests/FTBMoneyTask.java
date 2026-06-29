package net.sixik.sdmshop.compat.ftbquests;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.ISingleLongValueTask;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.sixik.sdmshop.currencies.SDMCoin;
import net.sixik.sdmshop.shop.seller_types.MoneySellerType;
import net.sixik.sdmshop.utils.ShopUtils;

public class FTBMoneyTask extends Task implements ISingleLongValueTask {
   public static TaskType TYPE;
   public long value = 1L;
   protected String money_id = SDMCoin.getId();

   public FTBMoneyTask(long id, Quest quest) {
      super(id, quest);
   }

   public TaskType getType() {
      return TYPE;
   }

   public long getMaxProgress() {
      return this.value;
   }

   public String formatMaxProgress() {
      return ShopUtils.moneyToString(this.value, this.money_id);
   }

   public String formatProgress(TeamData teamData, long progress) {
      return ShopUtils.moneyToString(progress, this.money_id);
   }

   public void writeData(CompoundTag nbt) {
      super.writeData(nbt);
      nbt.m_128359_("money_id", this.money_id);
      nbt.m_128356_("value", this.value);
   }

   public void readData(CompoundTag nbt) {
      super.readData(nbt);
      this.money_id = nbt.m_128461_("money_id");
      this.value = nbt.m_128454_("value");
   }

   public void writeNetData(FriendlyByteBuf buf) {
      super.writeNetData(buf);
      buf.m_130070_(this.money_id);
      buf.m_130103_(this.value);
   }

   public void readNetData(FriendlyByteBuf buf) {
      super.readNetData(buf);
      this.money_id = buf.m_130277_();
      this.value = buf.m_130258_();
   }

   public void setValue(long v) {
      this.value = v;
   }

   public void fillConfigGroup(ConfigGroup config) {
      super.fillConfigGroup(config);
      config.addLong("value", this.value, v -> this.value = v, 1L, 1L, Long.MAX_VALUE);
      config.addEnum("money_id", this.money_id, s -> this.money_id = s, MoneySellerType.getList());
   }

   public Component getAltTitle() {
      return Component.m_237113_(ShopUtils.moneyToString(this.value, this.money_id));
   }

   public boolean consumesResources() {
      return true;
   }

   @OnlyIn(Dist.CLIENT)
   public void addMouseOverText(TooltipList list, TeamData teamData) {
      super.addMouseOverText(list, teamData);
      list.add(
         Component.m_237115_("sdmshop.balance")
            .m_130946_(": ")
            .m_7220_(Component.m_237113_(ShopUtils.moneyToString(Minecraft.m_91087_().f_91074_, this.money_id)).m_130940_(ChatFormatting.GRAY))
      );
   }

   public void submitTask(TeamData teamData, ServerPlayer player, ItemStack craftedItem) {
      double money = ShopUtils.getMoney(player, this.money_id);
      double add = Math.min(money, this.value - teamData.getProgress(this));
      if (add > 0.0) {
         ShopUtils.setMoney(player, this.money_id, money - add);
         teamData.addProgress(this, (long)add);
      }
   }
}
