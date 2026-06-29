package net.sixik.sdmshop.old_api.shop;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.Widget;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.sixik.sdmshop.old_api.ModObjectIdentifier;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.utils.DataSerializerCompoundTag;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractEntrySellerType<T> implements DataSerializerCompoundTag, ModObjectIdentifier, ShopObject {
   public static final String ID_KEY = "register_id";
   protected T objectType;
   protected boolean shopTooltip = false;

   protected AbstractEntrySellerType(T objectType) {
      this.objectType = objectType;
   }

   public abstract boolean onBuy(Player var1, ShopEntry var2, long var3);

   public abstract boolean onSell(Player var1, ShopEntry var2, long var3);

   public abstract double getMoney(Player var1, ShopEntry var2);

   public boolean haveMoney(Player player, ShopEntry shopEntry, long countSell) {
      return shopEntry.getPrice() * countSell <= this.getMoney(player, shopEntry);
   }

   public boolean isFractionalNumber() {
      return true;
   }

   public abstract AbstractEntrySellerType<T> copy();

   public final void getConfig(ConfigGroup configGroup) {
      configGroup.addBool("shopTooltip", this.shopTooltip, v -> this.shopTooltip = v, false);
      this._getConfig(configGroup);
   }

   public void _getConfig(ConfigGroup configGroup) {
   }

   @Override
   public abstract String getId();

   public final CompoundTag serialize() {
      CompoundTag nbt = new CompoundTag();
      nbt.m_128359_("register_id", this.getId());
      nbt.m_128365_("data", this._serialize());
      nbt.m_128379_("shopTooltip", this.shopTooltip);
      return nbt;
   }

   public final void deserialize(CompoundTag tag) {
      if (tag.m_128441_("shopTooltip")) {
         this.shopTooltip = tag.m_128471_("shopTooltip");
      }

      this._deserialize(tag.m_128469_("data"));
   }

   public abstract CompoundTag _serialize();

   public abstract void _deserialize(CompoundTag var1);

   public abstract String getEnumName();

   public void addEntryTooltip(TooltipList list, ShopEntry entry) {
   }

   public abstract String moneyToString(ShopEntry var1);

   @OnlyIn(Dist.CLIENT)
   public abstract int getRenderWight(GuiGraphics var1, Theme var2, int var3, int var4, int var5, int var6, double var7, @Nullable Widget var9, int var10);

   @OnlyIn(Dist.CLIENT)
   public abstract void draw(GuiGraphics var1, Theme var2, int var3, int var4, int var5, int var6, double var7, @Nullable Widget var9, int var10);

   @OnlyIn(Dist.CLIENT)
   public abstract int draw(GuiGraphics var1, Theme var2, int var3, int var4, int var5, int var6, double var7);

   @OnlyIn(Dist.CLIENT)
   public abstract int getRenderSize(GuiGraphics var1, Theme var2, int var3, int var4, int var5, int var6, double var7);

   @OnlyIn(Dist.CLIENT)
   public abstract void drawCentered(GuiGraphics var1, Theme var2, int var3, int var4, int var5, int var6, double var7);

   @Override
   public final ShopObjectTypes getShopType() {
      return ShopObjectTypes.ENTRY_SELLER_TYPE;
   }
}
