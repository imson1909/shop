package net.sixik.sdmshop.compat.ftbquests;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftbquests.net.DisplayRewardToastMessage;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.reward.Reward;
import dev.ftb.mods.ftbquests.quest.reward.RewardType;
import java.util.Random;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.sixik.sdmshop.currencies.SDMCoin;
import net.sixik.sdmshop.shop.seller_types.MoneySellerType;
import net.sixik.sdmshop.utils.ShopUtils;

public class FTBMoneyReward extends Reward {
   public static RewardType TYPE;
   protected double value = 1.0;
   protected String money_id = SDMCoin.getId();
   protected double randomBonus = 0.0;

   public FTBMoneyReward(long id, Quest q) {
      super(id, q);
   }

   public RewardType getType() {
      return TYPE;
   }

   public void writeData(CompoundTag nbt) {
      super.writeData(nbt);
      nbt.m_128359_("money_id", this.money_id);
      nbt.m_128347_("ftb_money", this.value);
      if (this.randomBonus > 0.0) {
         nbt.m_128347_("random_bonus", this.randomBonus);
      }
   }

   public void readData(CompoundTag nbt) {
      super.readData(nbt);
      this.value = nbt.m_128454_("ftb_money");
      this.randomBonus = nbt.m_128451_("random_bonus");
   }

   public void writeNetData(FriendlyByteBuf buf) {
      super.writeNetData(buf);
      buf.m_130070_(this.money_id);
      buf.writeDouble(this.value);
      buf.writeDouble(this.randomBonus);
   }

   public void readNetData(FriendlyByteBuf buf) {
      super.readNetData(buf);
      this.money_id = buf.m_130277_();
      this.value = buf.m_130258_();
      this.randomBonus = buf.m_130242_();
   }

   public void fillConfigGroup(ConfigGroup config) {
      super.fillConfigGroup(config);
      config.addDouble("value", this.value, v -> this.value = v, 1.0, 1.0, 9.223372E18F);
      config.addDouble("random_bonus", this.randomBonus, v -> this.randomBonus = v, 0.0, 0.0, 2.147483647E9);
      config.addEnum("money_id", this.money_id, s -> this.money_id = s, MoneySellerType.getList());
   }

   public void claim(ServerPlayer player, boolean notify) {
      double money = ShopUtils.getMoney(player, this.money_id);
      double added = this.value + new Random().nextDouble(this.randomBonus + 1.0);
      ShopUtils.setMoney(player, this.money_id, money + added);
      if (notify) {
         new DisplayRewardToastMessage(
               this.id, Component.m_237113_(ShopUtils.moneyToString(added, this.money_id)), Icon.getIcon(ShopUtils.location("textures/icons/money.png"))
            )
            .sendTo(player);
      }
   }

   public Component getAltTitle() {
      return this.randomBonus > 0.0
         ? Component.m_237113_(ShopUtils.moneyToString(this.value, this.money_id))
            .m_130946_(" - ")
            .m_7220_(Component.m_237113_(ShopUtils.moneyToString(this.value + this.randomBonus, this.money_id)).m_130940_(ChatFormatting.GOLD))
         : Component.m_237113_(ShopUtils.moneyToString(this.value, this.money_id));
   }

   public String getButtonText() {
      return this.randomBonus > 0.0 ? this.randomBonus + "-" + (this.value + this.randomBonus) : String.valueOf(this.value);
   }
}
