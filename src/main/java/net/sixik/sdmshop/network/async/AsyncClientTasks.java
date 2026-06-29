package net.sixik.sdmshop.network.async;

import io.netty.buffer.Unpooled;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.sixik.sdmshop.SDMShop;
import net.sixik.sdmshop.api.ShopBase;
import net.sixik.sdmshop.cache.ShopClientCache;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.shop.BaseShop;
import net.sixik.sdmshop.utils.ShopUtils;

public class AsyncClientTasks {
   public static void init() {
      AsyncBridge.registerHandler("sync_shop_new", buf -> {
         UUID shopUId = buf.m_130259_();
         ResourceLocation shopId = buf.m_130281_();
         CompoundTag shopData = buf.m_130261_();
         boolean success = false;
         if (shopData != null) {
            SDMShopClient.CurrentShop = new BaseShop(shopData);
            success = true;
         } else {
            SDMShop.LOGGER.error("[Requests {}] Can't sync shop because 'shopData' is null", "sync_shop_new");
         }

         FriendlyByteBuf response = new FriendlyByteBuf(Unpooled.buffer());
         response.writeBoolean(success);
         return response;
      });
      AsyncBridge.registerHandler("sync_shop_cache_get", buf -> {
         UUID shopUId = buf.m_130259_();
         ResourceLocation shopID = buf.m_130281_();
         String shopVersion = buf.m_130277_();
         ShopClientCache.loadCache();
         boolean haveShop = false;
         ShopBase shopCache = ShopClientCache.getCache(shopID);
         if (shopCache != null) {
            String cachedVersion = shopCache.getVersion();
            if (!cachedVersion.equals("null_hash") && cachedVersion.equals(shopVersion)) {
               haveShop = true;
            }
         }

         FriendlyByteBuf response = new FriendlyByteBuf(Unpooled.buffer());
         response.writeBoolean(haveShop);
         return response;
      });
      AsyncBridge.registerHandler("sync_shop_cache_get_open", buf -> {
         UUID shopUId = buf.m_130259_();
         ResourceLocation shopID = buf.m_130281_();
         String shopVersion = buf.m_130277_();
         boolean haveShop = false;
         ShopClientCache.loadCache();
         ShopBase shopCache = ShopClientCache.getCache(shopID);
         if (shopCache != null) {
            String cachedVersion = shopCache.getVersion();
            if (!cachedVersion.equals("null_hash") && cachedVersion.equals(shopVersion)) {
               SDMShopClient.CurrentShop = (BaseShop)shopCache;
               SDMShopClient.openGui();
               haveShop = true;
            }
         }

         FriendlyByteBuf response = new FriendlyByteBuf(Unpooled.buffer());
         response.writeBoolean(haveShop);
         return response;
      });
      AsyncBridge.registerHandler("sync_shop_cache_set", buf -> {
         UUID shopUId = buf.m_130259_();
         ResourceLocation shopID = buf.m_130281_();
         CompoundTag shopData = buf.m_130261_();
         boolean success = false;
         if (shopData != null) {
            ShopClientCache.saveCache(new BaseShop(shopData));
            success = true;
         } else {
            SDMShop.LOGGER.error("[Requests {}] Can't sync shop because 'shopData' is null", "sync_shop_cache_set");
         }

         FriendlyByteBuf response = new FriendlyByteBuf(Unpooled.buffer());
         response.writeBoolean(success);
         return response;
      });
      AsyncBridge.registerHandler("sync_shop_cache_set_open", buf -> {
         UUID shopUId = buf.m_130259_();
         ResourceLocation shopID = buf.m_130281_();
         CompoundTag shopData = buf.m_130261_();
         boolean success = false;
         if (shopData != null) {
            BaseShop shop = new BaseShop(shopData);
            SDMShopClient.CurrentShop = shop;
            ShopClientCache.saveCache(shop);
            SDMShopClient.openGui();
            success = true;
         } else {
            SDMShop.LOGGER.error("[Requests {}] Can't sync and open shop because 'shopData' is null", "sync_shop_cache_set");
         }

         FriendlyByteBuf response = new FriendlyByteBuf(Unpooled.buffer());
         response.writeBoolean(success);
         return response;
      });
      AsyncBridge.registerHandler("open_shop_new", buf -> {
         UUID shopUId = buf.m_130259_();
         ResourceLocation shopID = buf.m_130281_();
         CompoundTag shopData = buf.m_130261_();
         boolean success = false;
         if (shopData != null) {
            if (SDMShopClient.CurrentShop == null || !SDMShopClient.CurrentShop.getId().equals(shopUId)) {
               SDMShopClient.CurrentShop = new BaseShop(shopID, shopUId);
            }

            SDMShopClient.CurrentShop.deserialize(shopData);
            SDMShopClient.openGui();
            success = true;
         } else {
            SDMShop.LOGGER.error("[Requests {}] Can't open shop because 'shopData' is null", "open_shop_new");
         }

         FriendlyByteBuf response = new FriendlyByteBuf(Unpooled.buffer());
         response.writeBoolean(success);
         return response;
      });
   }

   public static void openShop(ResourceLocation shopId) {
      AsyncBridge.askServer("get_open_shop", buf -> {
            buf.m_130085_(shopId);
            return buf;
         })
         .thenAcceptAsync(
            response -> {
               boolean allowed = response.readBoolean();
               if (allowed) {
                  boolean found = response.readBoolean();
                  ResourceLocation shopId_from_server = response.m_130281_();
                  if (!found) {
                     MutableComponent outMessage = Component.m_237113_("Shop with id '")
                        .m_130940_(ChatFormatting.RED)
                        .m_7220_(Component.m_237113_(shopId_from_server.toString()).m_130940_(ChatFormatting.GOLD))
                        .m_7220_(Component.m_237113_("' not found!").m_130940_(ChatFormatting.RED));
                     if (ShopUtils.isEditModeClient()) {
                        String shopIdString = shopId_from_server.m_135827_().equals("sdmshop") ? shopId_from_server.m_135815_() : shopId_from_server.toString();
                        outMessage.m_7220_(
                           Component.m_237113_(
                                 "\n§fPossible solutions:\n §7- Use command: §b/sdmshop create_shop "
                                    + shopIdString
                                    + "\n §7- Change shop ID in the button config\n §7- Disable this button in the config if not needed"
                              )
                              .m_130940_(ChatFormatting.GRAY)
                        );
                     }

                     Minecraft.m_91087_().f_91074_.m_5661_(outMessage, false);
                  } else {
                     UUID shopUID = response.m_130259_();
                     CompoundTag shopData = response.m_130261_();
                     if (SDMShopClient.CurrentShop == null || !SDMShopClient.CurrentShop.getId().equals(shopUID)) {
                        SDMShopClient.CurrentShop = new BaseShop(shopId_from_server, shopUID);
                     }

                     SDMShopClient.CurrentShop.deserialize(shopData);
                     SDMShopClient.openGui();
                  }
               }
            },
            Minecraft.m_91087_()
         )
         .exceptionally(ex -> {
            SDMShop.LOGGER.error("Failed to load shop", ex);
            Minecraft.m_91087_().f_91074_.m_5661_(Component.m_237113_("Error loading shop!"), false);
            return null;
         });
   }
}
