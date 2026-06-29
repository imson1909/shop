package net.sixik.sdmshop.shop;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.sixik.sdmshop.old_api.ConditionSupport;
import net.sixik.sdmshop.old_api.ConfigSupport;
import net.sixik.sdmshop.old_api.LimiterSupport;
import net.sixik.sdmshop.old_api.LimiterType;
import net.sixik.sdmshop.old_api.RenderSupport;
import net.sixik.sdmshop.old_api.TooltipSupport;
import net.sixik.sdmshop.old_api.shop.AbstractShopCondition;
import net.sixik.sdmshop.old_api.shop.ShopObject;
import net.sixik.sdmshop.old_api.shop.ShopObjectTypes;
import net.sixik.sdmshop.shop.limiter.ShopLimiter;
import net.sixik.sdmshop.utils.DataSerializerCompoundTag;
import net.sixik.sdmshop.utils.RenderComponent;
import net.sixik.sdmshop.utils.ShopDebugUtils;
import net.sixik.sdmshop.utils.ShopUtils;
import org.jetbrains.annotations.Nullable;
import org.joml.Math;

public class ShopTab implements DataSerializerCompoundTag, ConditionSupport, ConfigSupport, RenderSupport, LimiterSupport, TooltipSupport, ShopObject {
   public Component title = Component.m_237119_();
   protected final BaseShop ownerShop;
   protected UUID uuid;
   protected RenderComponent renderComponent = new RenderComponent();
   protected List<AbstractShopCondition> conditions = new ArrayList<>();
   protected int limitValue = 0;
   protected LimiterType limiterType = LimiterType.LocalPlayer;
   protected List<String> descriptions = new ArrayList<>();

   public ShopTab(BaseShop ownerShop) {
      this(ownerShop, UUID.randomUUID());
   }

   public ShopTab(BaseShop ownerShop, UUID uuid) {
      this.uuid = uuid;
      this.ownerShop = ownerShop;
   }

   public UUID getId() {
      return this.uuid;
   }

   public BaseShop getOwnerShop() {
      return this.ownerShop;
   }

   public CompoundTag serialize() {
      CompoundTag nbt = new CompoundTag();
      nbt.m_128362_("uuid", this.uuid);
      nbt.m_128359_("title", this.title.getString());
      nbt.m_128365_("render_component", this.renderComponent.serialize());
      this.serializeConditions(nbt);
      this.serializeLimiter(nbt);
      this.serializeTooltips(nbt);
      return nbt;
   }

   public void deserialize(CompoundTag tag) {
      this.uuid = tag.m_128342_("uuid");
      if (tag.m_128441_("title")) {
         this.title = Component.m_237113_(tag.m_128461_("title"));
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

   @Override
   public void getConfig(ConfigGroup group) {
      ShopUtils.addConfig(group, s -> s.addString("title", this.title.getString(), v -> this.title = Component.m_237115_(v), ""), new TooltipList());
      group.addString("title", this.title.getString(), v -> this.title = Component.m_237115_(v), "");
      this.renderComponent.getConfig(group);
      this.getLimiterConfig(group);
      this.getTooltipConfig(group);
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
         ShopDebugUtils.error("Tab Limiter is null!");
         return 0;
      }

      ShopLimiter limiter = optLimiter.get();
      int used = 0;
      if (this.getLimiterType().isGlobal()) {
         used = limiter.getTabData(this.uuid).orElse(0);
      } else if (player != null && this.getLimiterType().isPlayer()) {
         used = limiter.getTabData(this.uuid, player).orElse(0);
      }

      return Math.max(0, this.getObjectLimit() - used);
   }

   @Override
   public int getObjectLimit() {
      return this.limitValue;
   }

   @Override
   public void changeObjectLimit(int value) {
      int old = this.limitValue;
      if (old != value) {
         if (value > 0) {
            this.resetObjectLimit();
         } else {
            this.updateLimiterData(shopLimiter -> shopLimiter.deleteTabData(this.uuid));
         }
      }

      this.limitValue = value;
   }

   @Override
   public void resetObjectLimit() {
      Optional<ShopLimiter> optLimiter = this.getShopLimiter();
      if (!optLimiter.isEmpty()) {
         optLimiter.get().resetTabDataAll(this.uuid);
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
         limiter.addTabData(this.uuid, v);
      } else {
         limiter.addTabData(this.uuid, player.m_36316_().getId(), v);
      }

      return true;
   }

   @Override
   public List<String> getTooltips() {
      return this.descriptions;
   }

   @Override
   public final ShopObjectTypes getShopType() {
      return ShopObjectTypes.SHOP_TAB;
   }
}
