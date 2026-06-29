package net.sixik.sdmshop.shop.seller_types;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftblibrary.ui.GuiHelper;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.Widget;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.sixik.sdmshop.old_api.shop.AbstractEntrySellerType;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.utils.ShopItemHelper;
import net.sixik.sdmshop.utils.ShopNBTUtils;
import org.jetbrains.annotations.Nullable;

public class ItemSellerType extends AbstractEntrySellerType<ItemStack> {
   public static final String KEY = "money_item";

   public ItemSellerType() {
      this(Items.f_42415_.m_7968_());
   }

   public ItemSellerType(ItemStack objectType) {
      super(objectType);
   }

   @Override
   public boolean onBuy(Player player, ShopEntry shopEntry, long countSell) {
      int countItems = ShopItemHelper.countItem(player.m_150109_(), this.objectType, !this.objectType.m_41782_());
      int sellSize = (int)(shopEntry.getPrice() * countSell);
      return countItems >= sellSize ? ShopItemHelper.shrinkItem(player.m_150109_(), this.objectType.m_41777_(), sellSize, !this.objectType.m_41782_()) : false;
   }

   @Override
   public boolean onSell(Player player, ShopEntry shopEntry, long countSell) {
      return ShopItemHelper.giveItems(player, this.objectType.m_255036_(1), (long)(shopEntry.getPrice() * countSell));
   }

   @Override
   public double getMoney(Player player, ShopEntry shopEntry) {
      return ShopItemHelper.countItem(player.m_150109_(), this.objectType, !this.objectType.m_41782_());
   }

   @Override
   public AbstractEntrySellerType<ItemStack> copy() {
      return new ItemSellerType(this.objectType);
   }

   @Override
   public String getId() {
      return "item_seller";
   }

   @Override
   public CompoundTag _serialize() {
      CompoundTag nbt = new CompoundTag();
      ShopNBTUtils.putItemStack(nbt, "money_item", this.objectType);
      return nbt;
   }

   @Override
   public void _deserialize(CompoundTag tag) {
      this.objectType = ShopNBTUtils.getItemStack(tag, "money_item");
   }

   @Override
   public String getEnumName() {
      return "ITEM";
   }

   @Override
   public String moneyToString(ShopEntry entry) {
      return entry.getPrice() + " ";
   }

   @Override
   public boolean isFractionalNumber() {
      return false;
   }

   @Override
   public void _getConfig(ConfigGroup configGroup) {
      configGroup.addItemStack("item", this.objectType, v -> this.objectType = v, Items.f_42415_.m_7968_(), true, false);
   }

   @Override
   public void addEntryTooltip(TooltipList list, ShopEntry entry) {
      if (this.shopTooltip) {
         List<Component> list1 = new ArrayList<>();
         if (entry.getType().isSell()) {
            list1.add(Component.m_237115_("sdm.shop.entry.sell.buy").m_130940_(ChatFormatting.GOLD));
         } else {
            list1.add(Component.m_237115_("sdm.shop.entry.sell.sell").m_130940_(ChatFormatting.GOLD));
         }

         list1.add(Component.m_237113_("‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾"));
         GuiHelper.addStackTooltip(this.objectType, list1);
         list1.add(Component.m_237113_("‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾"));
         list1.forEach(list::add);
      }
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void draw(GuiGraphics graphics, Theme theme, int x, int y, int width, int height, double count, @Nullable Widget widget, int additionSize) {
      int size = height - height / 3 + additionSize;
      ItemIcon.getItemIcon(this.objectType).draw(graphics, x, y - 1, size, size);
      graphics.m_280488_(Minecraft.m_91087_().f_91062_, String.valueOf((int)count), x + height, y + 1, 16777215);
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public int draw(GuiGraphics graphics, Theme theme, int x, int y, int width, int height, double count) {
      int iconSize = theme.getFontHeight();
      int roundCount = (int)Math.round(count);
      String countTxt = String.valueOf(roundCount);
      ItemIcon.getItemIcon(this.objectType).draw(graphics, x, y - 1, iconSize, iconSize);
      graphics.m_280488_(Minecraft.m_91087_().f_91062_, countTxt, x + iconSize + 2, y + 1, 16777215);
      return iconSize + theme.getStringWidth(countTxt) + 2;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public int getRenderSize(GuiGraphics graphics, Theme theme, int x, int y, int width, int height, double count) {
      int iconSize = theme.getFontHeight();
      int roundCount = (int)Math.round(count);
      String countTxt = String.valueOf(roundCount);
      return iconSize + theme.getStringWidth(countTxt) + 2;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void drawCentered(GuiGraphics graphics, Theme theme, int x, int y, int width, int height, double count) {
      int spacing = 2;
      int iconSize = 9;
      int roundCount = (int)Math.round(count);
      String countTxt = String.valueOf(roundCount);
      int txtL = theme.getStringWidth(countTxt);
      int fullSize = txtL + iconSize + 2;
      int startPos = Math.max(0, (width - fullSize) / 2);
      ItemIcon.getItemIcon(this.objectType).draw(graphics, x + startPos, y - 1, iconSize, iconSize);
      graphics.m_280488_(Minecraft.m_91087_().f_91062_, countTxt, x + startPos + iconSize + 2, y, 16777215);
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public int getRenderWight(GuiGraphics graphics, Theme theme, int x, int y, int width, int height, double count, @Nullable Widget widget, int additionSize) {
      int s = height / 3;
      int size = height - s + additionSize;
      int textW = theme.getStringWidth(String.valueOf((int)count));
      return size + textW;
   }
}
