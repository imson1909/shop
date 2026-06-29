package net.sixik.sdmshop.client;

import com.mojang.blaze3d.platform.InputConstants.Type;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.snbt.SNBT;
import dev.ftb.mods.ftblibrary.snbt.SNBTCompoundTag;
import dev.ftb.mods.ftblibrary.ui.CustomClickEvent;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.sixik.sdmshop.SDMShopConstants;
import net.sixik.sdmshop.SDMShopPaths;
import net.sixik.sdmshop.api.ShopBase;
import net.sixik.sdmshop.api.ShopEvents;
import net.sixik.sdmshop.cache.ShopClientCache;
import net.sixik.sdmshop.client.screen.modern.ModernShopScreen;
import net.sixik.sdmshop.client.screen_new.MainShopScreen;
import net.sixik.sdmshop.config.ShopConfig;
import net.sixik.sdmshop.network.async.AsyncBridge;
import net.sixik.sdmshop.network.async.AsyncClientTasks;
import net.sixik.sdmshop.network.async.BlobTransfer;
import net.sixik.sdmshop.old_api.ConfigSupport;
import net.sixik.sdmshop.old_api.shop.AbstractEntryType;
import net.sixik.sdmshop.server.SDMShopServer;
import net.sixik.sdmshop.shop.BaseShop;
import net.sixik.sdmshop.shop.ShopParams;
import net.sixik.sdmshop.shop.limiter.ShopLimiter;
import net.sixik.sdmshop.shop.sorts.AbstractEntryTypeFilter;
import net.sixik.sdmshop.utils.DataSerializerCompoundTag;
import net.sixik.sdmshop.utils.ShopNBTUtils;
import org.jetbrains.annotations.Nullable;

public class SDMShopClient {
   @Nullable
   public static BaseShop CurrentShop;
   public static ShopLimiter shopLimiter = new ShopLimiter();
   public static Map<Class<? extends AbstractEntryType>, List<AbstractEntryTypeFilter<? extends AbstractEntryType>>> shopFilters;
   public static final Color4I someColor = Color4I.rgb(214, 154, 255);
   public static SDMShopClient.ClientShopData userData;
   public static final ResourceLocation OPEN_GUI = new ResourceLocation("sdmshop", "open_gui");
   public static final String SHOP_CATEGORY = "key.category.sdmshopr";
   public static final String KEY_NAME = "key.sdmshop.shopr";
   public static KeyMapping KEY_SHOP = new KeyMapping("key.sdmshop.shopr", Type.KEYSYM, 79, "key.category.sdmshopr");

   public static void init(Runnable onClient) {
      AsyncBridge.initClient();
      BlobTransfer.initClient();
      AsyncClientTasks.init();
      ClientLifecycleEvent.CLIENT_SETUP.register(SDMShopClient::onClientSetup);
      ClientTickEvent.CLIENT_PRE.register(SDMShopClient::keyInput);
      CustomClickEvent.EVENT.register(SDMShopClient::customClick);
      ClientPlayerEvent.CLIENT_PLAYER_JOIN.register(SDMShopClient::onClientPlayerConnect);
      ShopEvents.SHOP_CHANGE_EVENT.register((ShopBase.ShopChangeListener)shop -> {
         if (shop instanceof BaseShop baseShop) {
            ShopClientCache.saveCache(baseShop);
         }
      });
      if (!(Boolean)ShopConfig.DISABLE_KEYBIND.get()) {
         KeyMappingRegistry.register(KEY_SHOP);
      }

      onClient.run();
   }

   public static void onClientPlayerConnect(Player player) {
      ShopClientCache.loadCache();
   }

   public static EventResult customClick(CustomClickEvent event) {
      if (event.id().equals(OPEN_GUI) && !(Boolean)ShopConfig.DISABLE_KEYBIND.get()) {
         openGui(SDMShopConstants.AUTO_SHOP_OPEN);
         return EventResult.interruptTrue();
      } else {
         return EventResult.pass();
      }
   }

   public static void keyInput(Minecraft mc) {
      if (KEY_SHOP.m_90859_() && !(Boolean)ShopConfig.DISABLE_KEYBIND.get()) {
         openGui(SDMShopConstants.AUTO_SHOP_OPEN);
      }
   }

   public static void openGui(String shopId) {
      AsyncClientTasks.openShop(SDMShopServer.parseLocation(shopId));
   }

   public static void openGui(ResourceLocation shopId) {
      AsyncClientTasks.openShop(shopId);
   }

   public static void openGui() {
      switch ((ShopConfig.UIStyle)ShopConfig.GUI_STYLE.get()) {
         case Modern:
            new ModernShopScreen().openGui();
            break;
         case BlockyModern:
            new MainShopScreen().openGui();
      }
   }

   public static void onClientSetup(Minecraft minecraft) {
      userData = new SDMShopClient.ClientShopData(SDMShopPaths.getFileClient());
      SNBTCompoundTag d1 = SNBT.read(SDMShopPaths.getFileClient());
      if (d1 != null) {
         userData.deserialize(d1);
      }
   }

   public static class ClientShopData implements DataSerializerCompoundTag, ConfigSupport {
      public Path path;
      protected List<String> favoriteCreator = new ArrayList<>();
      protected List<UUID> favoriteEntries = new ArrayList<>();
      protected List<UUID> lastOpenedTabs = new ArrayList<>();
      protected Map<UUID, ShopParams> paramsMap = new HashMap<>();

      public ClientShopData(Path path) {
         this.path = path;
      }

      public boolean showEntryWitchCantBuy(UUID uuid) {
         return this.paramsMap.getOrDefault(uuid, new ShopParams()).showEntryWitchCantBuy();
      }

      public CompoundTag serialize() {
         CompoundTag nbt = new CompoundTag();
         ShopNBTUtils.putList(nbt, "favoriteCreator", this.favoriteCreator, StringTag::m_129297_);
         ShopNBTUtils.putList(nbt, "favoriteEntries", this.favoriteEntries, NbtUtils::m_129226_);
         ShopNBTUtils.putList(nbt, "lastOpenedTabs", this.lastOpenedTabs, NbtUtils::m_129226_);
         ListTag listTag = new ListTag();

         for (Entry<UUID, ShopParams> entry : this.paramsMap.entrySet()) {
            CompoundTag d1 = new CompoundTag();
            d1.m_128362_("shopId", entry.getKey());
            d1.m_128365_("data", entry.getValue().serialize());
            listTag.add(d1);
         }

         nbt.m_128365_("shop_client_data", listTag);
         return nbt;
      }

      public void deserialize(CompoundTag nbt) {
         this.favoriteCreator = ShopNBTUtils.getList(nbt, "favoriteCreator", Tag::m_7916_);
         this.favoriteEntries = ShopNBTUtils.getList(nbt, "favoriteEntries", NbtUtils::m_129233_);
         this.lastOpenedTabs = ShopNBTUtils.getList(nbt, "lastOpenedTabs", NbtUtils::m_129233_);
         if (nbt.m_128441_("shop_client_data")) {
            this.paramsMap.clear();

            for (Tag tag : (ListTag)nbt.m_128423_("shop_client_data")) {
               CompoundTag d1 = (CompoundTag)tag;
               UUID shopId = d1.m_128342_("shopId");
               ShopParams params = new ShopParams(d1.m_128469_("data"));
               this.paramsMap.put(shopId, params);
            }
         }
      }

      public void save() {
         SNBT.write(SDMShopPaths.getFileClient(), this.serialize());
      }

      public List<String> getCreator() {
         return this.favoriteCreator;
      }

      public List<UUID> getEntries() {
         return this.favoriteEntries;
      }

      @Override
      public void getConfig(ConfigGroup group) {
         if (SDMShopClient.CurrentShop != null) {
            ShopParams value = this.paramsMap.computeIfAbsent(SDMShopClient.CurrentShop.getId(), s -> new ShopParams());
            value.getClientConfig(group);
         }
      }
   }
}
