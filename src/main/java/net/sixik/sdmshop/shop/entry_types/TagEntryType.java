package net.sixik.sdmshop.shop.entry_types;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.NameMap;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.sixik.sdmshop.old_api.CustomIcon;
import net.sixik.sdmshop.old_api.shop.AbstractEntryType;
import net.sixik.sdmshop.old_api.shop.EntryTypeProperty;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.utils.ShopItemHelper;
import net.sixik.sdmshop.utils.ShopUtilsClient;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Nullable;

public class TagEntryType extends AbstractEntryType implements CustomIcon {
   protected static ResourceLocation DEFAULT = new ResourceLocation("minecraft", "anvil");
   protected ResourceLocation tagKey;
   protected boolean useIconFromTag = true;
   protected boolean showRandomIconFromTag = true;
   protected int currentRenderIndex = 0;

   public TagEntryType(ShopEntry shopEntry) {
      this(shopEntry, DEFAULT);
   }

   public TagEntryType(ShopEntry shopEntry, ResourceLocation tagKey) {
      super(shopEntry);
      this.tagKey = tagKey;
   }

   @Override
   public void getConfig(ConfigGroup group) {
      group.addEnum("tags", this.tagKey.toString(), v -> {
         this.tagKey = new ResourceLocation(v);
         this.currentRenderIndex = 0;
      }, getTags());
      group.addBool("useIconFromTag", this.useIconFromTag, v -> this.useIconFromTag = v, true);
      group.addBool("showRandomIconFromTag", this.showRandomIconFromTag, v -> this.showRandomIconFromTag = v, true);
   }

   @Override
   public Icon getCreativeIcon() {
      return ItemIcon.getItemIcon(Items.f_42146_);
   }

   public Optional<Named<Item>> getTag() {
      return BuiltInRegistries.f_257033_.m_203431_(TagKey.m_203882_(Registries.f_256913_, this.tagKey));
   }

   public static NameMap<String> getTags() {
      List<String> str = new ArrayList<>();
      BuiltInRegistries.f_257033_.m_203612_().forEach(s -> str.add(((TagKey)s.getFirst()).f_203868_().toString()));
      return NameMap.of(DEFAULT.toString(), str).create();
   }

   @Override
   public EntryTypeProperty getProperty() {
      return EntryTypeProperty.ONLY_SELL_COUNTABLE;
   }

   @Override
   public AbstractEntryType copy() {
      return new TagEntryType(this.shopEntry, this.tagKey);
   }

   @Override
   public Component getTranslatableForCreativeMenu() {
      return Component.m_237115_("sdm.shop.entry.creator.type.itemtag");
   }

   @Override
   public List<Component> getDescriptionForContextMenu() {
      List<Component> list = new ArrayList<>();
      list.add(Component.m_237115_("sdm.shop.entry.creator.type.itemTag.description"));
      return list;
   }

   public CompoundTag serialize() {
      CompoundTag nbt = new CompoundTag();
      nbt.m_128359_("tagKey", this.tagKey.toString());
      if (this.useIconFromTag) {
         nbt.m_128379_("useIconFromTag", true);
      }

      if (!this.showRandomIconFromTag) {
         nbt.m_128379_("showRandomIconFromTag", false);
      }

      return nbt;
   }

   public void deserialize(CompoundTag nbt) {
      this.tagKey = new ResourceLocation(nbt.m_128461_("tagKey"));
      if (nbt.m_128441_("useIconFromTag")) {
         this.useIconFromTag = nbt.m_128471_("useIconFromTag");
      }

      if (nbt.m_128441_("showRandomIconFromTag")) {
         this.showRandomIconFromTag = false;
      }
   }

   @Override
   public String getId() {
      return "itemTag";
   }

   @Override
   public boolean onBuy(Player player, ShopEntry entry, int countBuy) {
      throw new NotImplementedException();
   }

   @Override
   public boolean onSell(Player player, ShopEntry entry, int countBuy) {
      Optional<Named<Item>> tag = BuiltInRegistries.f_257033_.m_203431_(TagKey.m_203882_(Registries.f_256913_, this.tagKey));
      if (tag.isEmpty()) {
         return false;
      }

      Named<Item> tagData = tag.get();
      return ShopItemHelper.shrinkItemByTag(player.m_150109_(), tagData.m_205839_(), (int)(entry.getCount() * countBuy));
   }

   @Override
   public boolean canExecute(Player player, ShopEntry entry, int countBuy) {
      if (entry.getType().isBuy()) {
         return false;
      }

      Optional<Named<Item>> tag = BuiltInRegistries.f_257033_.m_203431_(TagKey.m_203882_(Registries.f_256913_, this.tagKey));
      if (tag.isEmpty()) {
         return false;
      }

      Named<Item> t = tag.get();
      return ShopItemHelper.countItem(player.m_150109_(), t.m_205839_()) >= entry.getCount() * countBuy;
   }

   @Override
   public int howMany(Player player, ShopEntry entry) {
      Optional<Named<Item>> tag = BuiltInRegistries.f_257033_.m_203431_(TagKey.m_203882_(Registries.f_256913_, this.tagKey));
      if (tag.isEmpty()) {
         return 0;
      }

      Named<Item> t = tag.get();
      return (int)(ShopItemHelper.countItem(player.m_150109_(), t.m_205839_()) / entry.getCount());
   }

   @Override
   public void addEntryTooltip(TooltipList list, ShopEntry entry) {
      if (Screen.m_96638_()) {
         Optional<Named<Item>> tagOptional = this.getTag();
         if (tagOptional.isEmpty()) {
            return;
         }

         Named<Item> tagData = tagOptional.get();
         if (tagData.m_203632_() <= 0) {
            return;
         }

         list.add(Component.m_237115_("sdm.shop.entry.info.items").m_130940_(ChatFormatting.GOLD));

         for (int i = 0; i < tagData.m_203632_(); i++) {
            if (i % 2 == 0) {
               list.add(((Item)tagData.m_203662_(i).m_203334_()).m_7968_().m_41786_().m_6881_().m_130940_(ChatFormatting.WHITE));
            } else {
               list.add(((Item)tagData.m_203662_(i).m_203334_()).m_7968_().m_41786_().m_6881_().m_130940_(ChatFormatting.AQUA));
            }
         }
      } else {
         list.add(Component.m_237110_("sdm.shop.entry.info.tag.type", new Object[]{this.tagKey.toString()}));
         list.add(Component.m_237115_("sdm.shop.entry.info.pressshift").m_130940_(ChatFormatting.GRAY).m_130940_(ChatFormatting.ITALIC));
      }
   }

   @Nullable
   @Override
   public Icon getCustomIcon(ShopEntry entry, int tick) {
      if (entry != null && this.useIconFromTag && this.tagKey != null) {
         Optional<Named<Item>> oTag = BuiltInRegistries.f_257033_.m_203431_(TagKey.m_203882_(Registries.f_256913_, this.tagKey));
         if (oTag.isEmpty()) {
            return null;
         }

         Named<Item> tag = oTag.get();
         int size = tag.m_203632_();
         if (size <= 0) {
            return null;
         }

         if (this.showRandomIconFromTag) {
            if (tick % ShopUtilsClient.getShop().getParams().getChangeIconSpeed() == 0) {
               this.currentRenderIndex = (this.currentRenderIndex + 1) % size;
            }

            return ItemIcon.getItemIcon((Item)tag.m_203662_(this.currentRenderIndex).m_203334_());
         } else {
            return ItemIcon.getItemIcon((Item)tag.m_203662_(0).m_203334_());
         }
      } else {
         return null;
      }
   }

   @Override
   public boolean isSearch(String search) {
      Optional<Named<Item>> oTag = BuiltInRegistries.f_257033_.m_203431_(TagKey.m_203882_(Registries.f_256913_, this.tagKey));
      return oTag.filter(holders -> ShopItemHelper.isSearch(search, (Named<Item>)holders)).isPresent();
   }
}
