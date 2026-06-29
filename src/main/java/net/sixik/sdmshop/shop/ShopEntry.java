package net.sixik.sdmshop.shop;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.sixik.sdmeconomy.SDMEconomy;
import net.sixik.sdmshop.api.data.EntryAdditionalProperty;
import net.sixik.sdmshop.old_api.ConditionSupport;
import net.sixik.sdmshop.old_api.ConfigSupport;
import net.sixik.sdmshop.old_api.LimiterSupport;
import net.sixik.sdmshop.old_api.LimiterType;
import net.sixik.sdmshop.old_api.RenderSupport;
import net.sixik.sdmshop.old_api.ShopEntryType;
import net.sixik.sdmshop.old_api.TooltipSupport;
import net.sixik.sdmshop.old_api.shop.AbstractEntrySellerType;
import net.sixik.sdmshop.old_api.shop.AbstractEntryType;
import net.sixik.sdmshop.old_api.shop.AbstractShopCondition;
import net.sixik.sdmshop.old_api.shop.ShopObject;
import net.sixik.sdmshop.old_api.shop.ShopObjectTypes;
import net.sixik.sdmshop.registers.ShopContentRegister;
import net.sixik.sdmshop.shop.entry_types.ItemEntryType;
import net.sixik.sdmshop.shop.limiter.ShopLimiter;
import net.sixik.sdmshop.shop.seller_types.MoneySellerType;
import net.sixik.sdmshop.utils.DataSerializer;
import net.sixik.sdmshop.utils.RenderComponent;
import net.sixik.sdmshop.utils.ShopDebugUtils;
import net.sixik.sdmshop.utils.ShopEntryTypeCreator;
import org.jetbrains.annotations.Nullable;
import org.joml.Math;

public class ShopEntry implements DataSerializer<CompoundTag>, ConditionSupport, ConfigSupport, RenderSupport, LimiterSupport, TooltipSupport, ShopObject {
   public static final UUID NULL_TAB = UUID.fromString("8de430d5-b1f0-45c7-b0ac-02772623c95e");
   protected final BaseShop ownerShop;
   protected UUID uuid;
   protected UUID ownerTab;
   protected double price;
   protected long count = 1L;
   protected int limitValue = 0;
   protected LimiterType limiterType = LimiterType.LocalPlayer;
   protected EntryAdditionalProperty scriptData = new EntryAdditionalProperty();
   protected Component title = Component.m_237119_();
   protected List<String> descriptions = new ArrayList<>();
   protected AbstractEntryType entryType;
   protected AbstractEntrySellerType<?> entrySellerType;
   protected List<AbstractShopCondition> conditions = new ArrayList<>();
   protected RenderComponent renderComponent = new RenderComponent();
   public ShopEntryType type = ShopEntryType.Buy;

   public ShopEntry(BaseShop ownerShop) {
      this(ownerShop, UUID.randomUUID(), NULL_TAB, new MoneySellerType(0.0));
   }

   public ShopEntry(BaseShop ownerShop, UUID ownerTab) {
      this(ownerShop, UUID.randomUUID(), ownerTab, new MoneySellerType(0.0));
   }

   public ShopEntry(BaseShop ownerShop, UUID uuid, UUID ownerTab, AbstractEntrySellerType<?> entrySellerType) {
      this.ownerShop = ownerShop;
      this.uuid = uuid;
      this.ownerTab = ownerTab;
      this.entrySellerType = entrySellerType;
      this.entryType = new ItemEntryType(this);
   }

   public ShopEntry setEntryType(AbstractEntryType entryType) {
      this.entryType = entryType;
      if (!this.entryType.getProperty().sellType.isBoth()) {
         this.type = this.entryType.getProperty().sellType.isSell() ? ShopEntryType.Sell : ShopEntryType.Buy;
      }

      return this;
   }

   public ShopEntry setPrice(double price) {
      this.price = price;
      return this;
   }

   public ShopEntry setCount(long count) {
      this.count = count;
      return this;
   }

   public ShopEntry updateIcon(ItemStack icon) {
      this.renderComponent.updateIcon(icon);
      return this;
   }

   public ShopEntry changeType(ShopEntryType type) {
      this.type = type;
      return this;
   }

   public ShopEntry copy() {
      return new ShopEntry(this.ownerShop, UUID.randomUUID(), this.ownerTab, this.entrySellerType).setEntryType(this.entryType.copy()).changeType(this.type);
   }

   public boolean onBuy(Player player, int count) {
      try {
         boolean value = this.getEntrySellerType().onBuy(player, this, count);
         ShopDebugUtils.log("On Buy value1: {}", value);
         if (value) {
            value = this.getEntryType().onBuy(player, this, count);
            ShopDebugUtils.log("On Buy value2: {}", value);
            return value;
         }
      } catch (Exception e) {
         SDMEconomy.printStackTrace("Error when try buy entry: ", e);
      }

      return false;
   }

   public boolean onSell(Player player, int count) {
      try {
         boolean value = this.getEntrySellerType().onSell(player, this, count);
         if (value) {
            return this.getEntryType().onSell(player, this, count);
         }
      } catch (Exception e) {
         SDMEconomy.printStackTrace("Error when try sell entry: ", e);
      }

      return false;
   }

   public CompoundTag serialize() {
      CompoundTag nbt = new CompoundTag();
      nbt.m_128362_("uuid", this.uuid);
      nbt.m_128362_("ownerTab", this.ownerTab);
      nbt.m_128347_("price", this.price);
      nbt.m_128356_("count", this.count);
      if (!this.title.getString().isEmpty()) {
         nbt.m_128359_("title", this.title.getString());
      }

      if (this.entryType != null) {
         CompoundTag entryNbt = this.entryType.serialize();
         entryNbt.m_128359_("type_id", this.entryType.getId());
         nbt.m_128365_("entry_type", entryNbt);
      }

      nbt.m_128365_("seller_type", this.entrySellerType.serialize());
      nbt.m_128405_("type", this.type.ordinal());
      nbt.m_128365_("render_component", this.renderComponent.serialize());
      if (this.limitValue > 0) {
         nbt.m_128405_("limiter", this.limitValue);
      }

      this.serializeConditions(nbt);
      this.serializeLimiter(nbt);
      this.serializeTooltips(nbt);
      return nbt;
   }

   public void deserialize(CompoundTag tag) {
      this.uuid = tag.m_128342_("uuid");
      this.ownerTab = tag.m_128342_("ownerTab");
      if (tag.m_128441_("price")) {
         this.price = tag.m_128459_("price");
      }

      if (tag.m_128441_("count")) {
         this.count = tag.m_128454_("count");
      }

      if (tag.m_128441_("title")) {
         this.title = Component.m_237115_(tag.m_128461_("title"));
      }

      if (tag.m_128441_("entry_type")) {
         this.entryType = ShopEntryTypeCreator.createEntryType(this, tag.m_128469_("entry_type")).orElse(null);
      }

      if (tag.m_128441_("seller_type")) {
         CompoundTag sellerData = tag.m_128469_("seller_type");
         String id = sellerData.m_128461_("register_id");
         Optional<Supplier<AbstractEntrySellerType<?>>> opt = ShopContentRegister.getSellerType(id);
         if (opt.isEmpty()) {
            this.entrySellerType = new MoneySellerType();
         } else {
            this.entrySellerType = opt.get().get();
            this.entrySellerType.deserialize(tag.m_128469_("seller_type"));
         }
      }

      if (tag.m_128441_("limiter")) {
         this.limitValue = tag.m_128451_("limiter");
      }

      if (tag.m_128441_("type")) {
         this.type = ShopEntryType.values()[tag.m_128451_("type")];
      }

      this.renderComponent.deserialize(tag.m_128469_("render_component"));
      this.deserializeConditions(tag, this.ownerShop);
      this.deserializeLimiter(tag);
      this.deserializeTooltips(tag);
   }

   @Override
   public List<AbstractShopCondition> getConditions() {
      return this.conditions;
   }

   public long getCount() {
      return this.count + this.scriptData.getCount();
   }

   public double getPrice() {
      return this.price + this.scriptData.getPrice();
   }

   public UUID getId() {
      return this.uuid;
   }

   public UUID getTab() {
      return this.ownerTab;
   }

   public void setTab(UUID ownerTab) {
      this.ownerTab = ownerTab;
   }

   public AbstractEntryType getEntryType() {
      return this.entryType;
   }

   public BaseShop getOwnerShop() {
      return this.ownerShop;
   }

   public ShopEntryType getType() {
      return this.type;
   }

   public AbstractEntrySellerType<?> getEntrySellerType() {
      return this.entrySellerType;
   }

   public Component getTitle() {
      return this.title;
   }

   @Override
   public void getConfig(ConfigGroup group) {
      group.addLong("count", this.count, v -> this.count = v, 1L, 1L, Long.MAX_VALUE);
      if (this.getEntryType().getProperty().sellType.isBoth()) {
         group.addBool(
            "sell", this.getType().isSell(), v -> this.type = v ? ShopEntryType.Sell : ShopEntryType.Buy, this.getEntryType().getProperty().sellType.isSell()
         );
      }

      this.entryType.getConfig(group);
      ConfigGroup sellerGroup = group.getOrCreateSubgroup("seller_type");
      if (this.getEntrySellerType().isFractionalNumber()) {
         sellerGroup.addDouble("price", this.price, v -> this.price = v, 0.0, 0.0, 2.147483647E9);
      } else {
         sellerGroup.addInt("price", this.price % 1.0 != 0.0 ? 0 : (int)this.price, v -> this.price = v.intValue(), 0, 0, Integer.MAX_VALUE);
      }

      sellerGroup.addEnum("type_id", this.getEntrySellerType().getEnumName(), v -> {
         if (!Objects.equals(v, this.getEntrySellerType().getEnumName())) {
            ShopContentRegister.getSellerTypeByEnumName(v).ifPresent(findType -> this.entrySellerType = findType.get());
         }
      }, ShopContentRegister.getSellerTypesForConfig());
      this.getEntrySellerType().getConfig(sellerGroup);
      this.renderComponent.getConfig(group);
      this.getTooltipConfig(group);
      this.getLimiterConfig(group);
      this.getConditionConfig(group);
   }

   @Override
   public RenderComponent getRenderComponent() {
      return this.renderComponent;
   }

   @Override
   public int getObjectLimitLeft(@Nullable Player player) {
      if (!this.isLimiterActive()) {
         return Integer.MAX_VALUE;
      }

      Optional<ShopLimiter> optLimiter = this.getShopLimiter();
      if (optLimiter.isEmpty()) {
         ShopDebugUtils.error("Entry Limiter is null!");
         return 0;
      }

      ShopLimiter limiter = optLimiter.get();
      int used = 0;
      if (this.getLimiterType().isGlobal()) {
         used = limiter.getEntryData(this.uuid).orElse(0);
      } else if (player != null && this.getLimiterType().isPlayer()) {
         used = limiter.getEntryData(this.uuid, player).orElse(0);
      }

      return Math.max(0, this.getObjectLimit() - used);
   }

   @Override
   public int getObjectLimit() {
      return this.limitValue + this.scriptData.getLimit();
   }

   @Override
   public void changeObjectLimit(int value) {
      int old = this.limitValue;
      if (old != value) {
         if (value > 0) {
            this.resetObjectLimit();
         } else {
            this.updateLimiterData(shopLimiter -> shopLimiter.deleteEntryData(this.uuid));
         }
      }

      this.limitValue = value;
   }

   @Override
   public void resetObjectLimit() {
      Optional<ShopLimiter> optLimiter = this.getShopLimiter();
      if (!optLimiter.isEmpty()) {
         optLimiter.get().resetEntryDataAll(this.uuid);
      }
   }

   @Override
   public LimiterType getLimiterType() {
      return this.limiterType;
   }

   @Override
   public void changeLimiterType(LimiterType type) {
      this.limiterType = type;
   }

   @Override
   public boolean updateLimit(@Nullable Player player, int count) {
      if (!this.isLimiterActive()) {
         return true;
      }

      if (this.isLimitReached(player)) {
         return false;
      }

      Optional<ShopLimiter> limiterOpt = this.getShopLimiter();
      if (limiterOpt.isEmpty()) {
         return false;
      }

      ShopLimiter limiter = limiterOpt.get();
      int left = this.getObjectLimitLeft(player);
      int v = left == Integer.MAX_VALUE ? count : Math.min(count, left);
      if (player == null) {
         limiter.addEntryData(this.uuid, v);
      } else {
         limiter.addEntryData(this.uuid, player.m_36316_().getId(), v);
      }

      return true;
   }

   @Override
   public List<String> getTooltips() {
      return this.descriptions;
   }

   @Override
   public final ShopObjectTypes getShopType() {
      return ShopObjectTypes.SHOP_ENTRY;
   }

   public EntryAdditionalProperty getScriptData() {
      return this.scriptData;
   }
}
