package net.sixik.sdmshop.shop.entry_types;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.sixik.sdmshop.old_api.CustomIcon;
import net.sixik.sdmshop.old_api.shop.AbstractEntryType;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.utils.ShopUtils;
import org.jetbrains.annotations.Nullable;

public class XPEntryType extends AbstractEntryType implements CustomIcon {
   protected int xpCount;

   public XPEntryType(ShopEntry shopEntry) {
      this(shopEntry, 1);
   }

   public XPEntryType(ShopEntry shopEntry, int xpCount) {
      super(shopEntry);
      this.xpCount = xpCount;
   }

   @Override
   public AbstractEntryType copy() {
      return new XPEntryType(this.shopEntry, this.xpCount);
   }

   @Override
   public boolean onBuy(Player player, ShopEntry entry, int countBuy) {
      double money = entry.getEntrySellerType().getMoney(player, entry);
      if (entry.getPrice() * countBuy > money) {
         return false;
      }

      int experience = ShopUtils.getPlayerXP(player) + this.xpCount * countBuy;
      player.f_36079_ = experience;
      player.f_36078_ = ShopUtils.getLevelForExperience(experience);
      int expForLevel = ShopUtils.getExperienceForLevel(player.f_36078_);
      player.f_36080_ = (float)(experience - expForLevel) / player.m_36323_();
      return false;
   }

   @Override
   public boolean onSell(Player player, ShopEntry entry, int countBuy) {
      int experience = ShopUtils.getPlayerXP(player) - this.xpCount * countBuy;
      player.f_36079_ = experience;
      player.f_36078_ = ShopUtils.getLevelForExperience(experience);
      int expForLevel = ShopUtils.getExperienceForLevel(player.f_36078_);
      player.f_36080_ = (float)(experience - expForLevel) / player.m_36323_();
      return true;
   }

   @Override
   public boolean canExecute(Player player, ShopEntry entry, int countBuy) {
      double money = this.shopEntry.getEntrySellerType().getMoney(player, entry);
      return entry.getPrice() * countBuy <= money && this.howMany(player, entry) > 0;
   }

   @Override
   public int howMany(Player player, ShopEntry entry) {
      if (!entry.getType().isSell()) {
         return entry.getPrice() == 0.0 ? 127 : (int)(this.shopEntry.getEntrySellerType().getMoney(player, entry) / entry.getPrice());
      } else {
         return player.f_36079_ != 0 && this.xpCount != 0 ? player.f_36079_ / this.xpCount : 0;
      }
   }

   @Override
   public void addEntryTooltip(TooltipList list, ShopEntry entry) {
      list.add(
         Component.m_237110_("sdm.shop.entry.info.xp_entry", new Object[]{Component.m_237113_(String.valueOf(this.xpCount)).m_130940_(ChatFormatting.GREEN)})
      );
   }

   @Override
   public Component getTranslatableForCreativeMenu() {
      return Component.m_237115_("sdm.shop.entry.creator.type.xp");
   }

   @Override
   public void getConfig(ConfigGroup group) {
      group.addInt("xp", this.xpCount, v -> this.xpCount = v, 1, 1, Integer.MAX_VALUE);
   }

   @Override
   public String getId() {
      return "xpType";
   }

   @Override
   public boolean isSearch(String search) {
      boolean find = search.contains("xp");
      return find && search.contains(String.valueOf(this.xpCount)) ? true : find;
   }

   public CompoundTag serialize() {
      CompoundTag nbt = new CompoundTag();
      nbt.m_128405_("xp", this.xpCount);
      return nbt;
   }

   public void deserialize(CompoundTag tag) {
      if (tag.m_128441_("xp")) {
         this.xpCount = tag.m_128451_("xp");
      }
   }

   @Nullable
   @Override
   public Icon getCustomIcon(ShopEntry entry, int tick) {
      return entry.getRenderComponent().getIcon().isEmpty() ? ItemIcon.getItemIcon(Items.f_42612_) : null;
   }

   @Override
   public Icon getCreativeIcon() {
      return ItemIcon.getItemIcon(Items.f_42612_);
   }
}
