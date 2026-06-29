package net.sixik.sdmshop.shop.entry_types;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftblibrary.ui.GuiHelper;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.sixik.sdmshop.client.render.BuyerRenderVariable;
import net.sixik.sdmshop.client.screen.base.buyer.AbstractBuyerScreen;
import net.sixik.sdmshop.old_api.CustomIcon;
import net.sixik.sdmshop.old_api.shop.AbstractEntryType;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.utils.ShopItemHelper;
import net.sixik.sdmshop.utils.ShopNBTUtils;
import net.sixik.sdmuilib.client.utils.TextHelper;
import net.sixik.sdmuilib.client.utils.math.Vector2;
import org.jetbrains.annotations.Nullable;

public class ItemEntryType extends AbstractEntryType implements CustomIcon {
   protected ItemStack itemStack;
   protected boolean strictNbt = true;
   protected boolean ignoreDamage = true;

   public ItemEntryType(ShopEntry shopEntry) {
      this(shopEntry, Items.f_41852_.m_7968_());
   }

   public ItemEntryType(ShopEntry shopEntry, ItemStack itemStack) {
      super(shopEntry);
      this.itemStack = itemStack;
      this.updateIcon(itemStack);
   }

   @Override
   public AbstractEntryType copy() {
      return new ItemEntryType(this.shopEntry, this.itemStack);
   }

   @Override
   public boolean onBuy(Player player, ShopEntry entry, int countBuy) {
      if (countBuy <= 0) {
         return false;
      }

      long totalCount = entry.getCount() * countBuy;
      return totalCount > 2147483647L ? false : ShopItemHelper.giveItems(player, this.itemStack, totalCount);
   }

   public ItemStack getItemStack() {
      return this.itemStack;
   }

   @Override
   public boolean onSell(Player player, ShopEntry entry, int countBuy) {
      if (countBuy <= 0) {
         return false;
      }

      long totalNeeded = entry.getCount() * countBuy;
      if (totalNeeded > 2147483647L) {
         return false;
      }

      int available = ShopItemHelper.countItem(player.m_150109_(), this.itemStack, this.strictNbt, this.ignoreDamage);
      if (available < totalNeeded) {
         return false;
      }

      boolean success = ShopItemHelper.shrinkItem(player.m_150109_(), this.itemStack, (int)totalNeeded, this.strictNbt, this.ignoreDamage);
      if (success) {
         player.f_36096_.m_38946_();
      }

      return success;
   }

   @Override
   public boolean canExecute(Player player, ShopEntry entry, int countBuy) {
      if (entry.getType().isSell()) {
         long totalNeeded = entry.getCount() * countBuy;
         if (totalNeeded > 2147483647L) {
            return false;
         }

         int countItems = ShopItemHelper.countItem(player.m_150109_(), this.itemStack, this.strictNbt, this.ignoreDamage);
         return countItems >= totalNeeded;
      } else {
         double playerMoney = entry.getEntrySellerType().getMoney(player, entry);
         double needMoney = entry.getPrice() * countBuy;
         return playerMoney >= needMoney;
      }
   }

   @Override
   public int howMany(Player player, ShopEntry entry) {
      if (entry.getType().isSell()) {
         int countItems = ShopItemHelper.countItem(player.m_150109_(), this.itemStack, this.strictNbt, this.ignoreDamage);
         return entry.getCount() == 0L ? 0 : Math.toIntExact(countItems / entry.getCount());
      } else {
         double playerMoney = entry.getEntrySellerType().getMoney(player, entry);
         return entry.getPrice() == 0.0 ? 508 : (int)(playerMoney / entry.getPrice());
      }
   }

   @Override
   public Component getTranslatableForCreativeMenu() {
      return Component.m_237115_("sdm.shop.entry.creator.type.item");
   }

   @Override
   public List<Component> getDescriptionForContextMenu() {
      List<Component> list = new ArrayList<>();
      list.add(Component.m_237115_("sdm.shop.entry.creator.type.item.description"));
      return list;
   }

   @Override
   public void sendNotifiedMessage(Player player, ShopEntry entry, int count) {
      Component text;
      if (this.shopEntry.getType().isSell()) {
         text = Component.m_237110_(
               "sdm.shop.entry.sell.info.item",
               new Object[]{this.itemStack.m_41611_().getString(), entry.getCount() * count, entry.getEntrySellerType().getMoney(player, entry)}
            )
            .m_130940_(ChatFormatting.ITALIC)
            .m_130940_(ChatFormatting.GRAY);
      } else {
         text = Component.m_237110_(
               "sdm.shop.entry.buy.info.item",
               new Object[]{this.itemStack.m_41611_().getString(), this.shopEntry.getCount(), entry.getEntrySellerType().getMoney(player, entry)}
            )
            .m_130940_(ChatFormatting.ITALIC)
            .m_130940_(ChatFormatting.GRAY);
      }

      player.m_5661_(text, false);
   }

   @Override
   public String getId() {
      return "shopItemEntryType";
   }

   @Override
   public boolean isSearch(String search) {
      return ShopItemHelper.isSearch(search, this.itemStack);
   }

   public CompoundTag serialize() {
      CompoundTag nbt = new CompoundTag();
      ShopNBTUtils.putItemStack(nbt, "itemStack", this.itemStack);
      nbt.m_128379_("strictNbt", this.strictNbt);
      nbt.m_128379_("ignoreDamage", this.ignoreDamage);
      return nbt;
   }

   public void deserialize(CompoundTag tag) {
      this.itemStack = ShopNBTUtils.getItemStack(tag, "itemStack");
      if (tag.m_128441_("ignoreDamage")) {
         this.ignoreDamage = tag.m_128471_("ignoreDamage");
      }

      if (tag.m_128441_("strictNbt")) {
         this.strictNbt = tag.m_128471_("strictNbt");
      }
   }

   @Override
   public void getConfig(ConfigGroup group) {
      group.addItemStack("item", this.itemStack, v -> {
         this.itemStack = v;
         this.updateIcon(this.itemStack);
      }, ItemStack.f_41583_, true, false);
      group.addBool("ignoreDamage", this.ignoreDamage, v -> this.ignoreDamage = v, true);
      group.addBool("strictNbt", this.strictNbt, v -> this.strictNbt = v, true);
   }

   @Nullable
   @Override
   public Icon getCustomIcon(ShopEntry entry, int tick) {
      return ItemIcon.getItemIcon(this.itemStack);
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void drawIcon(ShopEntry entry, GuiGraphics graphics, Theme theme, int x, int y, int w, int h, SimpleTextButton widget, int tick) {
      if (this.getCustomIcon(entry, tick) instanceof ItemIcon itemIcon) {
         ItemIcon.getItemIcon(itemIcon.getStack().m_255036_((int)entry.getCount())).draw(graphics, x, y, w, h);
      }
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void drawTitle(
      ShopEntry entry, GuiGraphics graphics, Theme theme, int x, int y, int w, int h, BuyerRenderVariable variable, AbstractBuyerScreen screen
   ) {
      Vector2 pos = variable.pos;
      w = TextHelper.getTextWidth(this.itemStack.m_41611_().getString());
      int w1 = screen.width - 10 - 2 - variable.iconSize * 2 - w;
      int w2 = w1 / 2;
      String d = this.itemStack.m_41611_().getString();
      d = d.replace("[", "").replace("]", "");
      theme.drawString(graphics, d, pos.x + w2, pos.y + 1, Color4I.WHITE, 2);
   }

   @Override
   public void drawTitleCentered(ShopEntry entry, GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
      this.drawComponentCentered(graphics, this.itemStack.m_41786_(), theme, x, y, w, h);
   }

   @Override
   public void addEntryTooltip(TooltipList list, ShopEntry entry) {
      List<Component> list1 = new ArrayList<>();
      GuiHelper.addStackTooltip(this.itemStack, list1);
      list1.forEach(list::add);
   }
}
