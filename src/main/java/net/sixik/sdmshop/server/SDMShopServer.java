package net.sixik.sdmshop.server;

import dev.architectury.platform.Platform;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import net.sixik.sdmshop.SDMShop;
import net.sixik.sdmshop.SDMShopPaths;
import net.sixik.sdmshop.old_api.DataSaver;
import net.sixik.sdmshop.shop.BaseShop;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.shop.ShopTab;
import net.sixik.sdmshop.shop.limiter.ShopLimiter;
import net.sixik.sdmshop.utils.ShopSerializerUtils;
import org.jetbrains.annotations.Nullable;

public class SDMShopServer implements DataSaver {
   public static final Path SHOP_FOLDER_DATA = Path.of("shops");
   private static final String LIMITER_FILE_NAME = "limiter.data";
   private static SDMShopServer _instance;
   protected final Map<UUID, BaseShop> shopsByUUID = new Object2ObjectOpenHashMap();
   protected final Map<ResourceLocation, BaseShop> shopsByRes = new Object2ObjectOpenHashMap();
   protected MinecraftServer server;
   protected ShopLimiter shopLimiter;
   @Nullable
   protected BaseShop defaultShop;

   public static SDMShopServer Instance() {
      return _instance;
   }

   public static Optional<SDMShopServer> InstanceOptional() {
      return Optional.ofNullable(_instance);
   }

   public SDMShopServer(MinecraftServer server) {
      this.server = server;
      this.shopLimiter = new ShopLimiter();
      _instance = this;
      this.load(server);
   }

   public boolean exists(String id) {
      return this.shopsByRes.containsKey(parseLocation(id));
   }

   public Optional<BaseShop> getShop(ResourceLocation shopId) {
      return Optional.ofNullable(this.shopsByRes.get(shopId));
   }

   public Optional<BaseShop> getShop(UUID uuid) {
      return Optional.ofNullable(this.shopsByUUID.get(uuid));
   }

   public BaseShop createShop(String id) {
      return this.createShop(new ResourceLocation("sdmshop", id));
   }

   public BaseShop createShop(ResourceLocation shopId) {
      if (this.shopsByRes.containsKey(shopId)) {
         return this.shopsByRes.get(shopId);
      }

      BaseShop shop = new BaseShop(shopId, UUID.randomUUID());
      this.registerInternal(shop);
      this.saveShopToFile(shop);
      return shop;
   }

   public boolean removeShop(String shopId) {
      return this.removeShop(parseLocation(shopId));
   }

   public boolean removeShop(ResourceLocation shopId) {
      BaseShop shop = this.shopsByRes.get(shopId);
      if (shop == null) {
         return false;
      }

      this.shopsByUUID.remove(shop.getId());
      this.shopsByRes.remove(shop.getRegistryId());
      this.deleteShopFile(shop);
      return true;
   }

   public Optional<BaseShop> getFirstShop() {
      Iterator<BaseShop> it = this.shopsByUUID.values().iterator();
      return it.hasNext() ? Optional.of(it.next()) : Optional.empty();
   }

   public List<String> getAllShopIDs() {
      return this.shopsByRes.keySet().stream().<String>map(ResourceLocation::toString).toList();
   }

   public List<UUID> getAllShopUUIDs() {
      return new ArrayList<>(this.shopsByUUID.keySet());
   }

   public ShopLimiter getShopLimiter() {
      return this.shopLimiter;
   }

   private void registerInternal(BaseShop shop) {
      this.shopsByUUID.put(shop.getId(), shop);
      this.shopsByRes.put(shop.getRegistryId(), shop);
   }

   public static ResourceLocation parseLocation(String id) {
      if (id.isEmpty()) {
         id = "default";
      }

      ResourceLocation res = ResourceLocation.m_135820_(id);
      if (res == null) {
         return new ResourceLocation("sdmshop", id);
      } else {
         return "minecraft".equals(res.m_135827_()) ? new ResourceLocation("sdmshop", res.m_135815_()) : res;
      }
   }

   private void createDefault() {
      this.defaultShop = this.createShop("default");
      if (Platform.isDevelopmentEnvironment()) {
         UUID ownerTab = UUID.randomUUID();
         this.defaultShop.addTab(new ShopTab(this.defaultShop, ownerTab));

         for (int i = 0; i < 100; i++) {
            this.defaultShop.addEntry(new ShopEntry(this.defaultShop, ownerTab));
         }

         this.saveShopToFile(this.defaultShop);
      }
   }

   @Override
   public void save(MinecraftServer server) {
      this.saveAllShops();
      this.saveLimiter(server);
   }

   @Override
   public void load(MinecraftServer server) {
      this.loadAllShops();
      this.loadLimiter(server);
   }

   public void saveAllShops() {
      Path root = this.getShopsDir();
      this.ensureDir(root);

      for (BaseShop shop : this.shopsByUUID.values()) {
         this.saveShopToFile(shop, root);
      }
   }

   public void saveShop(MinecraftServer server, UUID shopId) {
      BaseShop shop = this.shopsByUUID.get(shopId);
      if (shop != null) {
         this.saveShopToFile(shop);
      }
   }

   public void saveShopToFile(BaseShop shop) {
      this.saveShopToFile(shop, this.getShopsDir());
   }

   public void saveShopToFile(BaseShop shop, Path dir) {
      try {
         File file = new File(dir.toFile(), shop.getId().toString() + ".data");
         NbtIo.m_128955_(shop.serialize(), file);
      } catch (IOException e) {
         SDMShop.LOGGER.error("Error writing shop " + shop.getId(), e);
      }
   }

   public void loadAllShops() {
      this.shopsByUUID.clear();
      this.shopsByRes.clear();
      Path dir = this.getShopsDir();
      if (!dir.toFile().exists()) {
         this.createDefault();
      } else {
         File[] files = dir.toFile().listFiles((d, name) -> name.endsWith(".data"));
         if (files != null) {
            for (File file : files) {
               try {
                  CompoundTag nbt = NbtIo.m_128953_(file);
                  if (nbt != null) {
                     ResourceLocation registryId = new ResourceLocation(nbt.m_128461_("id"));
                     UUID uuid = nbt.m_128342_("uuid");
                     BaseShop shop = new BaseShop(registryId, uuid);
                     shop.deserialize(nbt);
                     ShopSerializerUtils.deleteEntriesWithNonexistentTabs(shop);
                     this.registerInternal(shop);
                  }
               } catch (Exception e) {
                  SDMShop.LOGGER.error("Error reading shop file " + file.getName(), e);
               }
            }

            if (this.defaultShop == null && !this.shopsByUUID.isEmpty()) {
               ResourceLocation defId = new ResourceLocation("sdmshop", "default");
               this.defaultShop = this.shopsByRes.getOrDefault(defId, this.getFirstShop().orElse(null));
            }
         }
      }
   }

   public void deleteShopFile(BaseShop shop) {
      Path path = this.getShopsDir().resolve(shop.getId().toString() + ".data");
      File file = path.toFile();
      if (file.exists() && !file.delete()) {
         SDMShop.LOGGER.error("Failed to delete shop file: {}", path);
      }
   }

   public Path getShopsDir() {
      Path p = SDMShopPaths.getModFolder().resolve(SHOP_FOLDER_DATA);
      this.ensureDir(p);
      return p;
   }

   public void saveLimiter(MinecraftServer server) {
      Path dir = this.getLimiterDir(server);

      try {
         File file = new File(dir.toFile(), "limiter.data");
         NbtIo.m_128955_(this.shopLimiter.serialize(), file);
      } catch (Exception e) {
         SDMShop.LOGGER.error("Error writing limiter file", e);
      }
   }

   public void loadLimiter(MinecraftServer server) {
      Path dir = this.getLimiterDir(server);
      File file = new File(dir.toFile(), "limiter.data");
      if (file.exists()) {
         try {
            CompoundTag nbt = NbtIo.m_128953_(file);
            if (nbt != null) {
               this.shopLimiter.deserialize(nbt);
            }
         } catch (Exception e) {
            SDMShop.LOGGER.error("Error reading limiter file", e);
         }
      }
   }

   public Path getLimiterDir(MinecraftServer server) {
      Path p = server.m_129843_(LevelResource.f_78182_).resolve("SDMShop").resolve(ShopLimiter.LIMITER_FOLDER);
      this.ensureDir(p);
      return p;
   }

   public void ensureDir(Path path) {
      File f = path.toFile();
      if (!f.exists() && !f.mkdirs()) {
         SDMShop.LOGGER.error("Error creating directory [{}]", path);
      }
   }
}
