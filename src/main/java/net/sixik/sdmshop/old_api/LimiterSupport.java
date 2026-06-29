package net.sixik.sdmshop.old_api;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.server.SDMShopServer;
import net.sixik.sdmshop.shop.limiter.ShopLimiter;
import org.jetbrains.annotations.Nullable;

public interface LimiterSupport {
   int getObjectLimitLeft(@Nullable Player var1);

   int getObjectLimit();

   void changeObjectLimit(int var1);

   void resetObjectLimit();

   boolean updateLimit(@Nullable Player var1, int var2);

   default boolean isLimiterActive() {
      return this.getObjectLimit() > 0;
   }

   LimiterType getLimiterType();

   void changeLimiterType(LimiterType var1);

   default boolean isLimitReached(@Nullable Player player) {
      return this.isLimiterActive() && this.getObjectLimitLeft(player) == 0;
   }

   default Optional<ShopLimiter> getShopLimiter() {
      ShopLimiter limiter = null;
      if (SDMShopServer.Instance() != null) {
         limiter = SDMShopServer.Instance().getShopLimiter();
      } else if (SDMShopClient.shopLimiter != null) {
         limiter = SDMShopClient.shopLimiter;
      }

      return Optional.ofNullable(limiter);
   }

   static Optional<ShopLimiter> getShopLimiterStatic() {
      ShopLimiter limiter = null;
      if (SDMShopServer.Instance() != null) {
         limiter = SDMShopServer.Instance().getShopLimiter();
      } else if (SDMShopClient.shopLimiter != null) {
         limiter = SDMShopClient.shopLimiter;
      }

      return Optional.ofNullable(limiter);
   }

   default void getLimiterConfig(ConfigGroup group) {
      ConfigGroup limiterGroup = group.getOrCreateSubgroup("limiter");
      limiterGroup.setNameKey("sdm.shop.limiter");
      limiterGroup.addInt("value", this.getObjectLimit(), this::changeObjectLimit, 0, 0, Integer.MAX_VALUE).setNameKey("sdm.shop.limiter.value");
      limiterGroup.addEnum("type", this.getLimiterType().name(), v -> {
         if (!Objects.equals(v, this.getLimiterType().name())) {
            this.changeLimiterType(LimiterType.valueOf(v));
         }
      }, LimiterType.getTypeList()).setNameKey("sdm.shop.limiter.type");
   }

   default void serializeLimiter(CompoundTag nbt) {
      nbt.m_128405_("limiter_value", this.getObjectLimit());
      nbt.m_128359_("limiter_type", this.getLimiterType().name());
   }

   default void deserializeLimiter(CompoundTag nbt) {
      if (nbt.m_128441_("limiter_value")) {
         this.changeObjectLimit(nbt.m_128451_("limiter_value"));
      }

      if (nbt.m_128441_("limiter_type")) {
         this.changeLimiterType(LimiterType.valueOf(nbt.m_128461_("limiter_type")));
      }
   }

   default void updateLimiterData(Consumer<ShopLimiter> limiterConsumer) {
      SDMShopServer.InstanceOptional().ifPresent(sdmShopServer -> limiterConsumer.accept(sdmShopServer.getShopLimiter()));
   }
}
