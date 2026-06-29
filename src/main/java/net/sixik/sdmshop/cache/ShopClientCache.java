package net.sixik.sdmshop.cache;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.sixik.sdmshop.SDMShop;
import net.sixik.sdmshop.SDMShopPaths;
import net.sixik.sdmshop.api.ShopBase;
import net.sixik.sdmshop.shop.BaseShop;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class ShopClientCache {
   public static final String CACHE_FOLDER = "cache";
   public static final String EXTENSION = ".cache";
   private static final Map<UUID, ShopBase> SHOP_CACHE = new HashMap<>();
   private static String serverIp = "";

   @Nullable
   public static ShopBase getCache(UUID shopId) {
      return SHOP_CACHE.get(shopId);
   }

   @Nullable
   public static ShopBase getCache(ResourceLocation shopId) {
      for (ShopBase value : SHOP_CACHE.values()) {
         if (Objects.equals(value.getRegistryId(), shopId)) {
            return value;
         }
      }

      return null;
   }

   public static String getCacheVersion(UUID shopId) {
      ShopBase shop = SHOP_CACHE.get(shopId);
      return shop == null ? "null_hash" : shop.getVersion();
   }

   public static String getCacheVersion(ResourceLocation shopId) {
      for (ShopBase value : SHOP_CACHE.values()) {
         if (Objects.equals(value.getRegistryId(), shopId)) {
            return value.getVersion();
         }
      }

      return "null_hash";
   }

   public static boolean loadCache() {
      ServerData serverData = Minecraft.m_91087_().m_91089_();
      if (serverData == null) {
         return false;
      } else {
         return Objects.equals(serverData.f_105363_, serverIp) ? false : loadCacheInternal(serverData);
      }
   }

   protected static boolean loadCacheInternal(ServerData serverData) {
      SHOP_CACHE.clear();
      if (serverData == null) {
         return false;
      }

      Path shopFolder = SDMShopPaths.getModFolder();
      Path cacheFolder = shopFolder.resolve("cache");
      Path serverCacheFolder = getCacheDirForCurrentServer(cacheFolder, serverData);

      try {
         Files.createDirectories(serverCacheFolder);
         File[] files = serverCacheFolder.toFile().listFiles();
         if (files != null && files.length != 0) {
            int loaded = 0;

            for (File file : files) {
               if (file.isFile()) {
                  String name = file.getName();
                  if (name.endsWith(".cache")) {
                     String uuidStr = name.substring(0, name.length() - ".cache".length());

                     UUID shopId;
                     try {
                        shopId = UUID.fromString(uuidStr);
                     } catch (IllegalArgumentException badName) {
                        SDMShop.LOGGER.warn("Skip cache file with invalid UUID name: {}", name);
                        continue;
                     }

                     try {
                        CompoundTag tag = readNbtFile(file.toPath());
                        if (tag != null) {
                           ShopBase shop = decodeShop(tag);
                           if (shop == null) {
                              SDMShop.LOGGER.warn("Failed to decode shop cache for {} (file {})", shopId, name);
                           } else {
                              SHOP_CACHE.put(shopId, shop);
                              loaded++;
                           }
                        }
                     } catch (Exception e) {
                        SDMShop.LOGGER.error("Failed to load cache file {}", file.getAbsolutePath(), e);
                     }
                  }
               }
            }

            SDMShop.LOGGER.info("Loaded {} shop caches from {}", loaded, serverCacheFolder);
            return true;
         } else {
            return true;
         }
      } catch (Exception e) {
         SDMShop.LOGGER.error("Failed to load cache folder {}", serverCacheFolder, e);
         return false;
      }
   }

   public static void saveCache(BaseShop shop) {
      if (shop != null) {
         SHOP_CACHE.put(shop.getId(), shop);
         ServerData serverData = Minecraft.m_91087_().m_91089_();
         if (serverData != null) {
            Path shopFolder = SDMShopPaths.getModFolder();
            Path cacheFolder = shopFolder.resolve("cache");
            Path serverCacheFolder = getCacheDirForCurrentServer(cacheFolder, serverData);

            try {
               Files.createDirectories(serverCacheFolder);
               UUID shopId = shop.getId();
               Path file = serverCacheFolder.resolve(shopId.toString() + ".cache");
               CompoundTag tag = shop.serialize();
               Path tmp = serverCacheFolder.resolve(shopId.toString() + ".cache.tmp");
               NbtIo.m_128955_(tag, tmp.toFile());

               try {
                  Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
               } catch (AtomicMoveNotSupportedException e) {
                  Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
               }
            } catch (Exception e) {
               SDMShop.LOGGER.error("Failed to save shop cache (shop={})", shop.getId(), e);
            }
         }
      }
   }

   private static String normalizeServerAddress(String raw) {
      if (raw != null && !raw.isBlank()) {
         String s = raw.trim().toLowerCase(Locale.ROOT);
         int colon = s.lastIndexOf(58);
         if (colon > 0 && colon < s.length() - 1) {
            String host = s.substring(0, colon);
            String portStr = s.substring(colon + 1);

            try {
               int port = Integer.parseInt(portStr);
               return host + ":" + port;
            } catch (NumberFormatException var6) {
            }
         }

         return s + ":25565";
      } else {
         return "unknown";
      }
   }

   private static String sha256Hex(String input) {
      try {
         MessageDigest md = MessageDigest.getInstance("SHA-256");
         byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
         StringBuilder sb = new StringBuilder(digest.length * 2);

         for (byte b : digest) {
            sb.append(String.format("%02x", b));
         }

         return sb.toString();
      } catch (Exception e) {
         return Integer.toHexString(input.hashCode());
      }
   }

   private static Path getCacheDirForCurrentServer(Path baseCacheDir, ServerData serverData) {
      String key = serverData == null ? "singleplayer" : normalizeServerAddress(serverData.f_105363_);
      String hash = sha256Hex("sdmshop|" + key);
      String folderName = hash.substring(0, 24);
      return baseCacheDir.resolve(folderName);
   }

   private static CompoundTag readNbtFile(Path path) throws Exception {
      return NbtIo.m_128953_(path.toFile());
   }

   private static ShopBase decodeShop(CompoundTag tag) {
      return (ShopBase)BaseShop.CODEC.parse(NbtOps.f_128958_, tag).resultOrPartial(SDMShop.LOGGER::error).orElse(null);
   }
}
