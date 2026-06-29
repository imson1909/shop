package net.sixik.sdmshop.shop;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import net.minecraft.nbt.CompoundTag;
import net.sixik.sdmshop.old_api.ConfigSupport;
import net.sixik.sdmshop.utils.DataSerializerCompoundTag;

public class ShopParams implements DataSerializerCompoundTag, ConfigSupport {
   protected static final String SHOW_TITLE_KEY = "show_title";
   protected static final String CHANGE_ICON_SPEED_KEY = "change_icon_speed";
   protected static final String SHOW_CANT_BUY_KEY = "show_cant_buy";
   protected CompoundTag data;

   public ShopParams() {
      this(new CompoundTag());
   }

   public ShopParams(CompoundTag nbt) {
      this.data = nbt;
   }

   public CompoundTag serialize() {
      return this.data;
   }

   public void deserialize(CompoundTag tag) {
      this.data = tag;
   }

   public CompoundTag getData() {
      return this.data;
   }

   @Override
   public void getConfig(ConfigGroup group) {
      ConfigGroup shopParamGroup = group.getOrCreateSubgroup("shop_param");
      shopParamGroup.addBool("show_title", this.isShowTitle(), v -> this.data.m_128379_("show_title", v), false);
      shopParamGroup.addInt("change_icon_speed", this.getChangeIconSpeed(), v -> this.data.m_128405_("change_icon_speed", v), 10, 1, Integer.MAX_VALUE);
      shopParamGroup.addBool("show_cant_buy", this.showEntryWitchCantBuy(), v -> this.data.m_128379_("show_cant_buy", v), false);
   }

   public void getClientConfig(ConfigGroup group) {
      ConfigGroup shopParamGroup = group.getOrCreateSubgroup("shop_param");
      shopParamGroup.addBool("show_cant_buy", this.showEntryWitchCantBuy(), v -> this.data.m_128379_("show_cant_buy", v), false);
   }

   public boolean isShowTitle() {
      return this.data.m_128441_("show_title") && this.data.m_128471_("show_title");
   }

   public int getChangeIconSpeed() {
      return this.data.m_128441_("change_icon_speed") ? this.data.m_128451_("change_icon_speed") : 10;
   }

   public boolean showEntryWitchCantBuy() {
      return this.data.m_128441_("show_cant_buy") && this.data.m_128471_("show_cant_buy");
   }
}
