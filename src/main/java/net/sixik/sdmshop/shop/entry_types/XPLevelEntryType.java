package net.sixik.sdmshop.shop.entry_types;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.sixik.sdmshop.old_api.CustomIcon;
import net.sixik.sdmshop.old_api.shop.AbstractEntryType;
import net.sixik.sdmshop.shop.ShopEntry;
import org.jetbrains.annotations.Nullable;

public class XPLevelEntryType extends AbstractEntryType implements CustomIcon {
   protected int xpLevel;

   public XPLevelEntryType(ShopEntry shopEntry) {
      this(shopEntry, 1);
   }

   public XPLevelEntryType(ShopEntry shopEntry, int xpLevel) {
      super(shopEntry);
      this.xpLevel = xpLevel;
   }

   @Override
   public AbstractEntryType copy() {
      return new XPLevelEntryType(this.shopEntry, this.xpLevel);
   }

   @Override
   public boolean onBuy(Player player, ShopEntry entry, int countBuy) {
      if (player instanceof ServerPlayer serverPlayer) {
         serverPlayer.m_9174_(player.f_36078_ + this.xpLevel * countBuy);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean onSell(Player player, ShopEntry entry, int countBuy) {
      if (player instanceof ServerPlayer serverPlayer) {
         serverPlayer.m_9174_(player.f_36078_ - this.xpLevel * countBuy);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean canExecute(Player player, ShopEntry entry, int countBuy) {
      if (entry.getType().isSell()) {
         return player.f_36078_ >= countBuy * this.xpLevel;
      }

      double money = entry.getEntrySellerType().getMoney(player, entry);
      return money >= entry.getPrice() * countBuy;
   }

   @Override
   public int howMany(Player player, ShopEntry entry) {
      if (!entry.getType().isSell()) {
         double money = entry.getEntrySellerType().getMoney(player, entry);
         return entry.getPrice() == 0.0 ? 127 : (int)(money / entry.getPrice());
      } else {
         return player.f_36079_ != 0 && this.xpLevel != 0 ? player.f_36078_ / this.xpLevel : 0;
      }
   }

   @Override
   public Component getTranslatableForCreativeMenu() {
      return Component.m_237115_("sdm.shop.entry.creator.type.xp_level");
   }

   @Override
   public void getConfig(ConfigGroup group) {
      group.addInt("xp_level", this.xpLevel, v -> this.xpLevel = v, 1, 1, Integer.MAX_VALUE);
   }

   @Override
   public String getId() {
      return "xpLevelType";
   }

   @Override
   public boolean isSearch(String search) {
      boolean find = search.contains("xp") || search.contains("level");
      return find && search.contains(String.valueOf(this.xpLevel)) ? true : find;
   }

   public CompoundTag serialize() {
      CompoundTag nbt = new CompoundTag();
      nbt.m_128405_("level", this.xpLevel);
      return nbt;
   }

   public void deserialize(CompoundTag nbt) {
      if (nbt.m_128441_("level")) {
         this.xpLevel = nbt.m_128451_("level");
      }
   }

   @Nullable
   @Override
   public Icon getCustomIcon(ShopEntry entry, int tick) {
      return entry.getRenderComponent().getIcon().isEmpty() ? ItemIcon.getItemIcon(Items.f_42612_) : null;
   }

   @Override
   public void addEntryTooltip(TooltipList list, ShopEntry entry) {
      list.add(
         Component.m_237110_(
            "sdm.shop.entry.info.xp_level_entry", new Object[]{Component.m_237113_(String.valueOf(this.xpLevel)).m_130940_(ChatFormatting.GREEN)}
         )
      );
   }

   @Override
   public Icon getCreativeIcon() {
      return ItemIcon.getItemIcon(Items.f_42612_);
   }
}
