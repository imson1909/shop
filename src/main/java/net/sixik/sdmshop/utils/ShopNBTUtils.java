package net.sixik.sdmshop.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ShopNBTUtils {
   public static <T> void putList(CompoundTag nbt, String id, Collection<T> collection, Function<T, Tag> func) {
      if (!collection.isEmpty()) {
         ListTag tags = new ListTag();

         for (T t : collection) {
            tags.add(func.apply(t));
         }

         nbt.m_128365_(id, tags);
      }
   }

   public static <T> List<T> getList(CompoundTag nbt, String id, Function<Tag, T> func) {
      if (!nbt.m_128441_(id)) {
         return new ArrayList<>();
      }

      List<T> list = new ArrayList<>();

      for (Tag t : (ListTag)nbt.m_128423_(id)) {
         list.add(func.apply(t));
      }

      return list;
   }

   public static <T> void getList(CompoundTag nbt, String id, Function<Tag, T> func, Collection<T> toAdd) {
      toAdd.addAll(getList(nbt, id, func));
   }

   public static <T> void getListWithClear(CompoundTag nbt, String id, Function<Tag, T> func, Collection<T> toAdd) {
      toAdd.clear();
      toAdd.addAll(getList(nbt, id, func));
   }

   public static void putItemStack(CompoundTag nbt, String key, ItemStack itemStack) {
      nbt.m_128365_(key, itemStack.m_41739_(new CompoundTag()));
   }

   public static ItemStack getItemStack(CompoundTag nbt, String key) {
      if (nbt.m_128423_(key) instanceof StringTag stringTag) {
         Item d1 = (Item)BuiltInRegistries.f_257033_.m_7745_(new ResourceLocation(stringTag.m_7916_()));
         return d1 == null ? ItemStack.f_41583_ : d1.m_7968_();
      } else {
         return ItemStack.m_41712_(nbt.m_128469_(key));
      }
   }

   public static <T> Optional<T> get(CompoundTag nbt, String id, Function<Tag, T> func) {
      return !nbt.m_128441_(id) ? Optional.empty() : Optional.ofNullable(func.apply(nbt.m_128423_(id)));
   }
}
