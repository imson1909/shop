package net.sixik.sdmshop.utils;

import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ShopItemHelper {
   public static int countItem(Container container, ItemStack itemStack, boolean strictNbt, boolean ignoreDamage) {
      int count = 0;

      for (int i = 0; i < container.m_6643_(); i++) {
         ItemStack slotItem = container.m_8020_(i);
         if (matches(itemStack, slotItem, strictNbt, ignoreDamage)) {
            count += slotItem.m_41613_();
         }
      }

      return count;
   }

   public static int countItem(Container container, ItemStack itemStack, boolean ignoreNbt) {
      return countItemByPredicate(container, v -> equals(v, itemStack, ignoreNbt));
   }

   public static int countItem(Container container, TagKey<Item> tagKey) {
      return countItemByPredicate(container, v -> v.m_204117_(tagKey));
   }

   public static int countItemByPredicate(Container container, Predicate<ItemStack> predicate) {
      int count = 0;

      for (int i = 0; i < container.m_6643_(); i++) {
         ItemStack findItem = container.m_8020_(i);
         if (!findItem.m_41619_() && predicate.test(findItem)) {
            count += findItem.m_41613_();
         }
      }

      return count;
   }

   public static boolean shrinkItem(Container container, ItemStack itemStack, int amount, boolean ignoreNbt) {
      return shrinkItemByPredicate(container, v -> equals(itemStack, v, ignoreNbt), amount);
   }

   public static boolean shrinkItemByTag(Container container, TagKey<Item> item, int amount) {
      return shrinkItemByPredicate(container, v -> v.m_204117_(item), amount);
   }

   public static boolean shrinkItemByPredicate(Container container, Predicate<ItemStack> itemStackPredicate, int amount) {
      int localAmount = amount <= 0 ? 1 : amount;
      int remainingToRemove = localAmount;
      if (countItemByPredicate(container, itemStackPredicate) < localAmount) {
         return false;
      }

      for (int i = 0; i < container.m_6643_() && remainingToRemove > 0; i++) {
         ItemStack findItem = container.m_8020_(i);
         if (!findItem.m_41619_() && itemStackPredicate.test(findItem)) {
            int itemCount = findItem.m_41613_();
            if (itemCount <= remainingToRemove) {
               remainingToRemove -= itemCount;
               container.m_6836_(i, ItemStack.f_41583_);
            } else {
               findItem.m_41764_(itemCount - remainingToRemove);
               container.m_6836_(i, findItem);
               remainingToRemove = 0;
            }
         }
      }

      return true;
   }

   public static boolean equals(ItemStack item1, ItemStack item2, boolean ignoreNbt) {
      if (item1 != null && item2 != null) {
         boolean equalsItem = Objects.equals(item1.m_41720_(), item2.m_41720_());
         boolean equalsNbt = ignoreNbt || equalsNbt(item1, item2);
         return equalsItem && equalsNbt;
      } else {
         return false;
      }
   }

   public static boolean equalsNbt(ItemStack item1, ItemStack item2) {
      return item1.m_41782_() && item2.m_41782_() ? NbtUtils.m_129235_(item1.m_41783_(), item2.m_41783_(), true) : false;
   }

   private static long distributeItems(Container container, ItemStack itemStack, long left) {
      int subtract = container instanceof Inventory ? 4 : 0;
      int maxStack = itemStack.m_41741_();
      boolean ignoreNbt = !itemStack.m_41782_();

      for (int i = 0; i < container.m_6643_() - subtract && left > 0L; i++) {
         ItemStack slotItem = container.m_8020_(i);
         if (equals(slotItem, itemStack, ignoreNbt) && slotItem.m_41613_() < maxStack) {
            int toAdd = (int)Math.min(maxStack - slotItem.m_41613_(), left);
            slotItem.m_41769_(toAdd);
            container.m_6836_(i, slotItem);
            left -= toAdd;
         }
      }

      for (int i = 0; i < container.m_6643_() - subtract && left > 0L; i++) {
         ItemStack slotItem = container.m_8020_(i);
         if (slotItem.m_41619_()) {
            int toAdd = (int)Math.min(maxStack, left);
            container.m_6836_(i, itemStack.m_255036_(toAdd));
            left -= toAdd;
         }
      }

      return left;
   }

   public static boolean giveItems(Player player, ItemStack itemStack, long amount) {
      if (itemStack != null && !itemStack.m_41619_() && amount > 0L) {
         long left = distributeItems(player.m_150109_(), itemStack, amount);
         if (left > 0L) {
            player.m_36176_(itemStack.m_255036_((int)left), true);
         }

         return true;
      } else {
         return false;
      }
   }

   public static boolean giveItems(Container container, ItemStack itemStack, long amount) {
      if (itemStack != null && !itemStack.m_41619_() && amount > 0L) {
         distributeItems(container, itemStack, amount);
         return true;
      } else {
         return false;
      }
   }

   public static boolean isSearch(String search, ItemStack itemStack) {
      String _search = search.toLowerCase();
      String display = itemStack.m_41611_().getString().toLowerCase();
      if (display.contains(_search)) {
         return true;
      }

      String registry = BuiltInRegistries.f_257033_.m_7981_(itemStack.m_41720_()).toString().toLowerCase();
      String _search_filtered = _search.replace(" ", "_");
      return registry.contains(_search_filtered);
   }

   public static boolean isSearch(String search, Named<Item> tag) {
      if (tag.m_203632_() == 0) {
         return false;
      }

      for (int i = 0; i < tag.m_203632_(); i++) {
         if (isSearch(search, ((Item)tag.m_203662_(i).m_203334_()).m_7968_())) {
            return true;
         }
      }

      return false;
   }

   private static boolean checkNbtRunning(ItemStack s1, ItemStack s2) {
      return ItemStack.m_150942_(s1, s2);
   }

   public static boolean matches(ItemStack shopItem, ItemStack playerItem, boolean strictNbt, boolean ignoreDamage) {
      if (playerItem.m_41619_() || shopItem.m_41619_()) {
         return false;
      } else if (!playerItem.m_150930_(shopItem.m_41720_())) {
         return false;
      } else if (!strictNbt) {
         return true;
      } else if (!shopItem.m_41782_()) {
         return !playerItem.m_41782_();
      } else {
         return ignoreDamage && playerItem.m_41763_()
            ? ItemStack.m_150942_(shopItem, playerItem) || playerItem.m_41773_() != shopItem.m_41773_() && checkNbtRunning(shopItem, playerItem)
            : ItemStack.m_150942_(shopItem, playerItem);
      }
   }

   public static boolean shrinkItem(Container container, ItemStack itemStack, int amount, boolean strictNbt, boolean ignoreDamage) {
      if (amount <= 0) {
         return false;
      }

      if (countItem(container, itemStack, strictNbt, ignoreDamage) < amount) {
         return false;
      }

      int remainingToRemove = amount;

      for (int i = 0; i < container.m_6643_() && remainingToRemove > 0; i++) {
         ItemStack slotItem = container.m_8020_(i);
         if (matches(itemStack, slotItem, strictNbt, ignoreDamage)) {
            int count = slotItem.m_41613_();
            if (count <= remainingToRemove) {
               remainingToRemove -= count;
               container.m_6836_(i, ItemStack.f_41583_);
            } else {
               slotItem.m_41774_(remainingToRemove);
               remainingToRemove = 0;
               container.m_6596_();
            }
         }
      }

      return remainingToRemove == 0;
   }
}
