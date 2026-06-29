package net.sixik.sdmshop.shop.entry_types;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.NameMap;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftblibrary.util.KnownServerRegistries;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import dev.ftb.mods.ftblibrary.util.KnownServerRegistries.AdvancementInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.sixik.sdmshop.old_api.CustomIcon;
import net.sixik.sdmshop.old_api.shop.AbstractEntryType;
import net.sixik.sdmshop.old_api.shop.EntryTypeProperty;
import net.sixik.sdmshop.shop.ShopEntry;
import org.jetbrains.annotations.Nullable;

public class AdvancementEntryType extends AbstractEntryType implements CustomIcon {
   protected static final ResourceLocation DEFAULT = new ResourceLocation("minecraft:story/root");
   protected ResourceLocation advancement;
   protected boolean useIconFromAdvancement;

   public AdvancementEntryType(ShopEntry shopEntry) {
      this(shopEntry, DEFAULT);
   }

   public AdvancementEntryType(ShopEntry shopEntry, ResourceLocation advancement) {
      this(shopEntry, advancement, true);
   }

   public AdvancementEntryType(ShopEntry shopEntry, ResourceLocation advancement, boolean useIconFromAdvancement) {
      super(shopEntry);
      this.advancement = advancement;
      this.useIconFromAdvancement = true;
   }

   @Override
   public AbstractEntryType copy() {
      return new AdvancementEntryType(this.shopEntry, this.advancement, this.useIconFromAdvancement);
   }

   @Override
   public boolean onBuy(Player player, ShopEntry entry, int countBuy) {
      if (!(player instanceof ServerPlayer serverPlayer)) {
         return false;
      } else {
         Optional<Advancement> opt = getAdvancement(player, this.advancement);
         if (opt.isEmpty()) {
            return false;
         }

         if (entry.getPrice() * countBuy > entry.getEntrySellerType().getMoney(player, entry)) {
            return false;
         }

         Advancement value = opt.get();

         for (String s : value.m_138325_().keySet()) {
            serverPlayer.m_8960_().m_135988_(value, s);
         }

         return true;
      }
   }

   @Override
   public boolean onSell(Player player, ShopEntry entry, int countBuy) {
      if (!(player instanceof ServerPlayer serverPlayer)) {
         return false;
      } else {
         Optional<Advancement> opt = getAdvancement(player, this.advancement);
         if (opt.isEmpty()) {
            return false;
         }

         Advancement value = opt.get();

         for (String s : value.m_138325_().keySet()) {
            serverPlayer.m_8960_().m_135998_(value, s);
         }

         return true;
      }
   }

   @Override
   public boolean canExecute(Player player, ShopEntry entry, int countBuy) {
      if (!entry.getType().isBuy()) {
         return hasAdvancement(player, this.advancement);
      }

      double money = entry.getEntrySellerType().getMoney(player, entry);
      return !hasAdvancement(player, this.advancement) && entry.getPrice() * countBuy <= money;
   }

   @Override
   public int howMany(Player player, ShopEntry entry) {
      if (entry.getType().isSell()) {
         return hasAdvancement(player, this.advancement) ? 1 : 0;
      } else if (entry.getPrice() > entry.getEntrySellerType().getMoney(player, entry)) {
         return 0;
      } else {
         return hasAdvancement(player, this.advancement) ? 0 : 1;
      }
   }

   @Override
   public Component getTranslatableForCreativeMenu() {
      return Component.m_237115_("sdm.shop.entry.creator.type.advancement");
   }

   @Override
   public List<Component> getDescriptionForContextMenu() {
      List<Component> list = new ArrayList<>();
      list.add(Component.m_237115_("sdm.shop.entry.creator.type.advancement.description"));
      return list;
   }

   @Override
   public void getConfig(ConfigGroup group) {
      group.addEnum(
            "advancement",
            this.advancement,
            v -> this.advancement = v,
            NameMap.of(
                  (ResourceLocation)KnownServerRegistries.client.advancements.keySet().iterator().next(),
                  (ResourceLocation[])KnownServerRegistries.client.advancements.keySet().toArray(new ResourceLocation[0])
               )
               .icon(resourceLocation -> ItemIcon.getItemIcon(((AdvancementInfo)KnownServerRegistries.client.advancements.get(resourceLocation)).icon))
               .name(resourceLocation -> ((AdvancementInfo)KnownServerRegistries.client.advancements.get(resourceLocation)).name)
               .create()
         )
         .setNameKey("ftbquests.reward.ftbquests.advancement");
      group.addBool("useIconFromAdvancement", this.useIconFromAdvancement, v -> this.useIconFromAdvancement = v, true);
   }

   @Override
   public String getId() {
      return "advancementType";
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public boolean isSearch(String search) {
      Optional<Advancement> opt = getAdvancement(Minecraft.m_91087_().f_91074_, this.advancement);
      return opt.<Boolean>map(value -> value.m_138330_().getString().contains(search)).orElse(false);
   }

   public CompoundTag serialize() {
      CompoundTag nbt = new CompoundTag();
      nbt.m_128359_("advancement", this.advancement.toString());
      nbt.m_128379_("useIconFromAdvancement", this.useIconFromAdvancement);
      return nbt;
   }

   public void deserialize(CompoundTag nbt) {
      this.advancement = new ResourceLocation(nbt.m_128461_("advancement"));
      this.useIconFromAdvancement = nbt.m_128471_("useIconFromAdvancement");
   }

   @Override
   public void addEntryTooltip(TooltipList list, ShopEntry entry) {
      list.add(
         Component.m_237110_(
            "sdm.shop.entry.info.advancement",
            new Object[]{((AdvancementInfo)KnownServerRegistries.client.advancements.get(this.advancement)).name.m_6881_().m_130940_(ChatFormatting.GREEN)}
         )
      );
   }

   @Nullable
   @Override
   public Icon getCustomIcon(ShopEntry entry, int tick) {
      return this.useIconFromAdvancement ? ItemIcon.getItemIcon(((AdvancementInfo)KnownServerRegistries.client.advancements.get(this.advancement)).icon) : null;
   }

   public static boolean hasAdvancement(Player player, ResourceLocation id) {
      if (player.m_7578_()) {
         return Minecraft.m_91087_().m_91403_().m_105145_().m_104396_().m_139337_(id) != null;
      } else if (player instanceof ServerPlayer serverPlayer) {
         Advancement adv = serverPlayer.f_8924_.m_129889_().m_136041_(id);
         return adv == null ? false : serverPlayer.m_8960_().m_135996_(adv).m_8193_();
      } else {
         return false;
      }
   }

   public static Optional<Advancement> getAdvancement(Player player, ResourceLocation id) {
      if (player.m_7578_()) {
         return Optional.ofNullable(Minecraft.m_91087_().m_91403_().m_105145_().m_104396_().m_139337_(id));
      } else {
         return player instanceof ServerPlayer serverPlayer ? Optional.ofNullable(serverPlayer.f_8924_.m_129889_().m_136041_(id)) : Optional.empty();
      }
   }

   @Override
   public EntryTypeProperty getProperty() {
      return EntryTypeProperty.DEFAULT;
   }

   @Override
   public Icon getCreativeIcon() {
      return ItemIcon.getItemIcon(Items.f_42405_);
   }
}
