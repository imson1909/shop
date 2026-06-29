package net.sixik.sdmshop.network.async;

import io.netty.buffer.Unpooled;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.sixik.sdmshop.SDMShop;
import net.sixik.sdmshop.SDMShopConstants;
import net.sixik.sdmshop.config.ShopConfig;
import net.sixik.sdmshop.server.SDMShopServer;
import net.sixik.sdmshop.shop.BaseShop;

public class AsyncServerTasks {
   public static final String SYNC_SHOP_NEW = "sync_shop_new";
   public static final String SYNC_SHOP_CACHE_GET = "sync_shop_cache_get";
   public static final String SYNC_SHOP_CACHE_GET_OPEN = "sync_shop_cache_get_open";
   public static final String SYNC_SHOP_CACHE_SET = "sync_shop_cache_set";
   public static final String SYNC_SHOP_CACHE_SET_OPEN = "sync_shop_cache_set_open";
   public static final String OPEN_SHOP_NEW = "open_shop_new";
   public static final String GET_OPEN_SHOP = "get_open_shop";

   public static void init() {
      AsyncBridge.registerHandler(
         "get_open_shop",
         buf -> {
            ResourceLocation readId = buf.m_130281_();
            FriendlyByteBuf hugeData = new FriendlyByteBuf(Unpooled.buffer());
            if ((Boolean)ShopConfig.DISABLE_KEYBIND.get()) {
               hugeData.writeBoolean(false);
               return hugeData;
            } else {
               hugeData.writeBoolean(true);
               ResourceLocation shopId = readId.equals(SDMShopConstants.AUTO_SHOP_OPEN)
                  ? SDMShopServer.parseLocation((String)ShopConfig.DEFAULT_SHOP_ID.get())
                  : readId;
               Optional<BaseShop> optionalShop = SDMShopServer.Instance().getShop(shopId);
               if (optionalShop.isEmpty()) {
                  hugeData.writeBoolean(false);
                  hugeData.m_130085_(shopId);
                  SDMShop.LOGGER.error("Can't find shop with id [{}]", shopId);
                  return hugeData;
               } else {
                  BaseShop shop = optionalShop.get();
                  CompoundTag fullShopData = shop.serialize();
                  hugeData.writeBoolean(true);
                  hugeData.m_130085_(shopId);
                  hugeData.m_130077_(shop.getId());
                  hugeData.m_130079_(fullShopData);
                  return hugeData;
               }
            }
         }
      );
   }

   public static void openShopOrCache(ServerPlayer player, ResourceLocation shopId) {
      Optional<BaseShop> shopOpt = SDMShopServer.Instance().getShop(shopId);
      if (!shopOpt.isEmpty()) {
         openShopOrCache(player, shopOpt.get());
      }
   }

   public static void openShopOrCache(ServerPlayer player, BaseShop shop) {
      if ((Boolean)ShopConfig.USE_CACHED_SHOP_DATA.get()) {
         openShopCacheNew(player, shop);
      } else {
         openShopNew(player, shop);
      }
   }

   public static void openShopNew(ServerPlayer player, ResourceLocation shopId) {
      Optional<BaseShop> shopOpt = SDMShopServer.Instance().getShop(shopId);
      if (!shopOpt.isEmpty()) {
         openShopNew(player, shopOpt.get());
      }
   }

   public static void openShopNew(ServerPlayer player, BaseShop shop) {
      AsyncBridge.askPlayer(player, "open_shop_new", buf -> {
         buf.m_130077_(shop.getId());
         buf.m_130085_(shop.getRegistryId());
         buf.m_130079_(shop.serializeOrCache());
         return buf;
      });
   }

   public static void openShopCacheNew(ServerPlayer player, ResourceLocation shopId) {
      Optional<BaseShop> shopOpt = SDMShopServer.Instance().getShop(shopId);
      if (!shopOpt.isEmpty()) {
         openShopCacheNew(player, shopOpt.get());
      }
   }

   public static void openShopCacheNew(ServerPlayer player, BaseShop shop) {
      AsyncBridge.askPlayer(player, "sync_shop_cache_get_open", buf -> {
         buf.m_130077_(shop.getId());
         buf.m_130085_(shop.getRegistryId());
         buf.m_130070_(shop.getVersion());
         return buf;
      }).thenAcceptAsync(response -> {
         boolean haveShop = response.readBoolean();
         if (!haveShop) {
            AsyncBridge.askPlayer(player, "sync_shop_cache_set_open", buf -> {
               buf.m_130077_(shop.getId());
               buf.m_130085_(shop.getRegistryId());
               buf.m_130079_(shop.serializeOrCache());
               return buf;
            });
         }
      }, player.m_20194_());
   }

   public static void syncShop(ServerPlayer player, ResourceLocation shopId) {
      Optional<BaseShop> shopOpt = SDMShopServer.Instance().getShop(shopId);
      if (!shopOpt.isEmpty()) {
         syncShop(player, shopOpt.get());
      }
   }

   public static void syncShop(ServerPlayer player, BaseShop shop) {
      AsyncBridge.askPlayer(player, "sync_shop_new", buf -> {
         buf.m_130077_(shop.getId());
         buf.m_130085_(shop.getRegistryId());
         buf.m_130079_(shop.serializeOrCache());
         return buf;
      });
   }

   public static void syncShopCache(ServerPlayer player, ResourceLocation shopId) {
      Optional<BaseShop> shopOpt = SDMShopServer.Instance().getShop(shopId);
      if (!shopOpt.isEmpty()) {
         syncShopCache(player, shopOpt.get());
      }
   }

   public static void syncShopCache(ServerPlayer player, BaseShop shop) {
      AsyncBridge.askPlayer(player, "sync_shop_cache_get", buf -> {
         buf.m_130077_(shop.getId());
         buf.m_130085_(shop.getRegistryId());
         buf.m_130070_(shop.getVersion());
         return buf;
      }).thenAcceptAsync(response -> {
         boolean haveShop = response.readBoolean();
         if (!haveShop) {
            AsyncBridge.askPlayer(player, "sync_shop_cache_set", buf -> {
               buf.m_130077_(shop.getId());
               buf.m_130085_(shop.getRegistryId());
               buf.m_130079_(shop.serializeOrCache());
               return buf;
            });
         }
      }, player.m_20194_());
   }
}
