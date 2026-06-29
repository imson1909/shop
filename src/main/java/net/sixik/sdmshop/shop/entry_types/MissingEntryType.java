package net.sixik.sdmshop.shop.entry_types;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.sixik.sdmshop.old_api.CustomIcon;
import net.sixik.sdmshop.old_api.shop.AbstractEntryType;
import net.sixik.sdmshop.shop.ShopEntry;
import org.jetbrains.annotations.Nullable;

public class MissingEntryType extends AbstractEntryType implements CustomIcon {
   public static final String CURRENT_ID = "EMPTY";
   protected final String missingId;
   protected CompoundTag originalData;

   public MissingEntryType(ShopEntry shopEntry) {
      this(shopEntry, new CompoundTag());
   }

   public MissingEntryType(ShopEntry shopEntry, CompoundTag originalData) {
      super(shopEntry);
      this.missingId = originalData.m_128441_("type_id") ? originalData.m_128461_("type_id") : "TYPE_ID_MISSING";
      this.originalData = originalData;
   }

   @Override
   public AbstractEntryType copy() {
      return new MissingEntryType(this.shopEntry, this.originalData);
   }

   @Override
   public boolean onBuy(Player player, ShopEntry entry, int countBuy) {
      return false;
   }

   @Override
   public boolean onSell(Player player, ShopEntry entry, int countBuy) {
      return false;
   }

   @Override
   public boolean canExecute(Player player, ShopEntry entry, int countBuy) {
      return false;
   }

   @Override
   public int howMany(Player player, ShopEntry entry) {
      return 0;
   }

   @Override
   public Component getTranslatableForCreativeMenu() {
      return null;
   }

   @Override
   public void getConfig(ConfigGroup group) {
   }

   @Override
   public String getId() {
      return this.missingId;
   }

   @Override
   public boolean isSearch(String search) {
      return false;
   }

   @Override
   public void addEntryTooltip(TooltipList list, ShopEntry entry) {
      list.add(Component.m_237113_("'Type: " + this.missingId + "' not found!").m_130940_(ChatFormatting.RED));
   }

   public CompoundTag serialize() {
      return this.originalData;
   }

   public void deserialize(CompoundTag tag) {
      this.originalData = tag;
   }

   @Nullable
   @Override
   public Icon getCustomIcon(ShopEntry entry, int tick) {
      return Icons.REMOVE;
   }
}
