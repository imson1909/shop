package net.sixik.sdmshop.shop.entry_types;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.sixik.sdmshop.old_api.shop.AbstractEntryType;
import net.sixik.sdmshop.old_api.shop.EntryTypeProperty;
import net.sixik.sdmshop.shop.ShopEntry;
import org.apache.commons.lang3.NotImplementedException;

public class CommandEntryType extends AbstractEntryType {
   protected static final String DEFAULT_KEY = "/time set day";
   protected String command;
   protected boolean elevatePerms;
   protected boolean silent;
   protected int maxBuyCount;

   public CommandEntryType(ShopEntry shopEntry) {
      this(shopEntry, "/time set day", true, true, 1);
   }

   public CommandEntryType(ShopEntry shopEntry, String command, boolean elevatePerms, boolean silent, int maxBuyCount) {
      super(shopEntry);
      this.command = command;
      this.elevatePerms = elevatePerms;
      this.silent = silent;
      this.maxBuyCount = maxBuyCount;
   }

   @Override
   public AbstractEntryType copy() {
      return new CommandEntryType(this.shopEntry, this.command, this.elevatePerms, this.silent, this.maxBuyCount);
   }

   @Override
   public boolean onBuy(Player player, ShopEntry entry, int countBuy) {
      if (!(player instanceof ServerPlayer serverPlayer)) {
         return false;
      } else {
         if (this.command.isEmpty()) {
            return false;
         }

         String copyCommand = this.command;
         if (copyCommand.contains("{player}")) {
            copyCommand = copyCommand.replace("{player}", serverPlayer.m_7755_().getString());
         }

         CommandSourceStack commandSource = serverPlayer.m_20203_();
         if (this.elevatePerms) {
            commandSource = commandSource.m_81325_(2);
         }

         if (this.silent) {
            commandSource = commandSource.m_81324_();
         }

         for (int i = 0; i < countBuy; i++) {
            player.m_20194_().m_129892_().m_230957_(commandSource, copyCommand);
         }

         return true;
      }
   }

   @Override
   public boolean onSell(Player player, ShopEntry entry, int countBuy) {
      throw new NotImplementedException();
   }

   @Override
   public boolean canExecute(Player player, ShopEntry entry, int countBuy) {
      return this.howMany(player, entry) > 0;
   }

   @Override
   public int howMany(Player player, ShopEntry entry) {
      double price = entry.getEntrySellerType().getMoney(player, entry);
      return (int)Math.max(0.0, Math.min(price / entry.getPrice(), this.maxBuyCount));
   }

   @Override
   public Component getTranslatableForCreativeMenu() {
      return Component.m_237115_("sdm.shop.entry.creator.type.command");
   }

   @Override
   public List<Component> getDescriptionForContextMenu() {
      List<Component> list = new ArrayList<>();
      list.add(Component.m_237115_("sdm.shop.entry.creator.type.command.description"));
      return list;
   }

   @Override
   public Icon getCreativeIcon() {
      return ItemIcon.getItemIcon(Items.f_42116_);
   }

   @Override
   public void getConfig(ConfigGroup group) {
      group.addString("command", this.command, v -> this.command = v, "/time set day", Pattern.compile("^/.*"));
      group.addBool("elevatePerms", this.elevatePerms, v -> this.elevatePerms = v, false);
      group.addBool("silent", this.silent, v -> this.silent = v, false);
      group.addInt("maxBuyCount", this.maxBuyCount, v -> this.maxBuyCount = v, 1, 1, Integer.MAX_VALUE);
   }

   @Override
   public EntryTypeProperty getProperty() {
      return EntryTypeProperty.ONLY_BUY;
   }

   @Override
   public String getId() {
      return "commandType";
   }

   @Override
   public boolean isSearch(String search) {
      return this.command.contains(search);
   }

   public CompoundTag serialize() {
      CompoundTag nbt = new CompoundTag();
      nbt.m_128359_("command", this.command);
      nbt.m_128379_("elevatePerms", true);
      nbt.m_128379_("silent", true);
      nbt.m_128405_("maxBuyCount", this.maxBuyCount);
      return nbt;
   }

   public void deserialize(CompoundTag nbt) {
      this.command = nbt.m_128461_("command");
      if (nbt.m_128441_("elevatePerms")) {
         this.elevatePerms = nbt.m_128471_("elevatePerms");
      }

      if (nbt.m_128441_("silent")) {
         this.silent = nbt.m_128471_("silent");
      }

      if (nbt.m_128441_("maxBuyCount")) {
         this.maxBuyCount = nbt.m_128451_("maxBuyCount");
      }
   }
}
