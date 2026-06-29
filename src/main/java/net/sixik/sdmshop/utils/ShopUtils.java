package net.sixik.sdmshop.utils;

import com.google.common.collect.Maps;
import dev.architectury.platform.Platform;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.ConfigValue;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.sixik.sdmeconomy.SDMEconomy;
import net.sixik.sdmeconomy.CustomPlayerData.Data;
import net.sixik.sdmeconomy.api.EconomyAPI;
import net.sixik.sdmeconomy.currencies.BaseCurrency;
import net.sixik.sdmeconomy.currencies.CurrencySymbol.Type;
import net.sixik.sdmeconomy.currencies.data.CurrencyData;
import net.sixik.sdmeconomy.currencies.data.CurrencyPlayerData.PlayerCurrency;
import net.sixik.sdmeconomy.utils.CurrencyHelper;
import net.sixik.sdmeconomy.utils.ErrorCodes;
import net.sixik.sdmshop.currencies.SDMCoin;
import net.sixik.sdmshop.network.async.AsyncClientTasks;
import net.sixik.sdmshop.network.async.AsyncServerTasks;
import net.sixik.sdmshop.network.economy.ShopChangeMoneyC2S;
import net.sixik.sdmshop.network.server.ChangeEditModeC2S;
import net.sixik.sdmshop.server.SDMShopServer;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.shop.ShopTab;
import net.sixik.sdmshop.shop.limiter.ShopLimiterAttachType;
import net.sixik.sdmshop.shop.limiter.ShopLimiterData;
import net.sixik.sdmshop.utils.config.ConfigBuilder;
import org.jetbrains.annotations.Nullable;

public class ShopUtils {
   public static final Predicate<String> ONLY_DIGITS = s -> s != null && (s.isEmpty() || s.chars().allMatch(Character::isDigit));
   public static final Predicate<String> DIGITS_0_100 = s -> {
      if (s == null) {
         return false;
      }

      if (s.isEmpty()) {
         return true;
      }

      if (s.length() > 3) {
         return false;
      }

      for (int i = 0; i < s.length(); i++) {
         char c = s.charAt(i);
         if (c < '0' || c > '9') {
            return false;
         }
      }

      try {
         int v = Integer.parseInt(s);
         return v >= 0 && v <= 100;
      } catch (NumberFormatException e) {
         return false;
      }
   };
   public static final Predicate<String> ONLY_DIGITS_MAX2 = s -> s != null && (s.isEmpty() || s.length() <= 2 && s.chars().allMatch(Character::isDigit));
   public static final boolean isMarketLoaded = Platform.isModLoaded("sdm_market");

   public static ResourceLocation location(String path) {
      return new ResourceLocation("sdmshop", path);
   }

   @OnlyIn(Dist.CLIENT)
   public static boolean isEditModeClient() {
      return isEditMode(Minecraft.m_91087_().f_91074_);
   }

   public static boolean isEditMode(Player player) {
      Data data;
      if (player.m_7578_()) {
         data = EconomyAPI.getCustomClientData().data;
      } else {
         data = EconomyAPI.getCustomServerData().getPlayerCustomData(player);
      }

      return data.nbt.m_128441_("edit_mode") && data.nbt.m_128471_("edit_mode");
   }

   @OnlyIn(Dist.CLIENT)
   public static void changeEditModeClient(boolean value) {
      changeEditMode(Minecraft.m_91087_().f_91074_, value);
   }

   public static void changeEditMode(Player player, boolean value) {
      if (player.m_7578_()) {
         new ChangeEditModeC2S(value).sendToServer();
      } else {
         try {
            Data data = EconomyAPI.getCustomServerData().getPlayerCustomData(player);
            data.nbt.m_128379_("edit_mode", value);
            CurrencyHelper.syncCustomData((ServerPlayer)player);
         } catch (Exception e) {
            SDMEconomy.printStackTrace("", e);
         }
      }
   }

   public static double getMoney(Player player) {
      return getMoney(player, SDMCoin.getId());
   }

   public static double getMoney(Player player, String moneyName) {
      return player.m_7578_()
         ? EconomyAPI.getPlayerCurrencyClientData().getBalance(moneyName)
         : (Double)EconomyAPI.getPlayerCurrencyServerData().getBalance(player, moneyName).value;
   }

   public static boolean addMoney(Player player, double value) {
      return addMoney(player, SDMCoin.getId(), value);
   }

   public static boolean addMoney(Player player, String moneyName, double value) {
      if (player.m_7578_()) {
         new ShopChangeMoneyC2S(moneyName, getMoney(player) + value).sendToServer();
         return CurrencyHelper.isAdmin(player);
      } else {
         ErrorCodes result = EconomyAPI.getPlayerCurrencyServerData().addCurrencyValue(player, moneyName, value);
         if (result.isSuccess()) {
            EconomyAPI.syncPlayer((ServerPlayer)player);
            return true;
         } else {
            return false;
         }
      }
   }

   public static boolean setMoney(Player player, double value) {
      return setMoney(player, SDMCoin.getId(), value);
   }

   public static boolean setMoney(Player player, String moneyName, double value) {
      if (player.m_7578_()) {
         new ShopChangeMoneyC2S(moneyName, value).sendToServer();
         return CurrencyHelper.isAdmin(player);
      } else {
         ErrorCodes result = EconomyAPI.getPlayerCurrencyServerData().setCurrencyValue(player, moneyName, value);
         if (result.isSuccess()) {
            EconomyAPI.syncPlayer((ServerPlayer)player);
            return true;
         } else {
            return false;
         }
      }
   }

   public static String moneyToString(Player player) {
      return moneyToString(player, SDMCoin.getId());
   }

   public static String moneyToString(Player player, String moneyName) {
      StringBuilder builder = new StringBuilder();
      if (player.m_7578_()) {
         Optional<PlayerCurrency> opt = EconomyAPI.getPlayerCurrencyClientData().getCurrency(moneyName);
         if (opt.isPresent()) {
            PlayerCurrency currency = opt.get();
            if (currency.currency.symbol.type == Type.CHAR) {
               builder.append(currency.currency.symbol.value).append(" ");
            }

            builder.append(currency.balance);
         }

         return builder.toString();
      } else {
         Optional<PlayerCurrency> opt = EconomyAPI.getPlayerCurrencyServerData().getPlayerCurrency(player, moneyName);
         if (opt.isPresent()) {
            PlayerCurrency currency = opt.get();
            if (currency.currency.symbol.type == Type.CHAR) {
               builder.append(currency.currency.symbol.value).append(" ");
            }

            builder.append(currency.balance);
         }

         return builder.toString();
      }
   }

   public static String moneyToString(double l, String moneyName) {
      for (BaseCurrency currency : ((CurrencyData)EconomyAPI.getAllCurrency().value).currencies) {
         if (Objects.equals(currency.getName(), moneyName)) {
            return (currency.symbol.type == Type.CHAR ? currency.symbol.value : "") + " " + String.format("%.2f", l);
         }
      }

      return " " + l;
   }

   public static void sendOpenShop(MinecraftServer server, String shopId) {
      for (ServerPlayer player : server.m_6846_().m_11314_()) {
         AsyncServerTasks.openShopNew(player, SDMShopServer.parseLocation(shopId));
      }
   }

   public static void sendOpenShop(ServerPlayer player, String shopId) {
      AsyncServerTasks.openShopOrCache(player, SDMShopServer.parseLocation(shopId));
   }

   public static void openShopClient(String shopId) {
      AsyncClientTasks.openShop(SDMShopServer.parseLocation(shopId));
   }

   public static <T> ConfigValue<T> addConfig(ConfigGroup group, Function<ConfigGroup, ConfigValue<T>> fun, Component[] components) {
      TooltipList list = new TooltipList();

      for (Component component : components) {
         list.add(component);
      }

      return addConfig(group, fun, list);
   }

   public static <T> ConfigValue<T> addConfig(ConfigGroup group, Function<ConfigGroup, ConfigValue<T>> fun, TooltipList list) {
      ConfigValue<T> value = fun.apply(group);
      value.addInfo(list);
      return value;
   }

   public static <T> ConfigValue<T> addConfig(ConfigGroup group, Function<ConfigGroup, ConfigValue<T>> fun, Consumer<ConfigBuilder<T>> builderConsumer) {
      ConfigBuilder<T> builder = addConfigBuilder(group, fun);
      builderConsumer.accept(builder);
      return builder.getValue();
   }

   public static <T> ConfigBuilder<T> addConfigBuilder(ConfigGroup group, Function<ConfigGroup, ConfigValue<T>> fun) {
      return new ConfigBuilder<>(group, fun);
   }

   public static ShopLimiterData getShopLimit(ShopTab shopTab, ShopEntry shopEntry, Player player) {
      int tabLimit = Integer.MAX_VALUE;
      int entryLimit = Integer.MAX_VALUE;
      if (shopTab.isLimiterActive()) {
         tabLimit = shopTab.getObjectLimitLeft(player);
      }

      if (shopEntry.isLimiterActive()) {
         entryLimit = shopEntry.getObjectLimitLeft(player);
      }

      if (tabLimit == Integer.MAX_VALUE && entryLimit == Integer.MAX_VALUE) {
         return new ShopLimiterData(ShopLimiterAttachType.None, Integer.MAX_VALUE);
      } else if (tabLimit == Integer.MAX_VALUE) {
         return new ShopLimiterData(ShopLimiterAttachType.Entry, entryLimit);
      } else if (entryLimit == Integer.MAX_VALUE) {
         return new ShopLimiterData(ShopLimiterAttachType.Tab, tabLimit);
      } else {
         return tabLimit <= entryLimit
            ? new ShopLimiterData(ShopLimiterAttachType.Tab, tabLimit)
            : new ShopLimiterData(ShopLimiterAttachType.Entry, entryLimit);
      }
   }

   public static int getMaxEntryOfferSize(ShopTab shopTab, ShopEntry shopEntry, Player player) {
      return getMaxEntryOfferSize(shopEntry, player, getShopLimit(shopTab, shopEntry, player).value());
   }

   public static int getMaxEntryOfferSize(ShopEntry shopEntry, Player player, int size) {
      int howMany = shopEntry.getEntryType().howMany(player, shopEntry);
      return size <= -1 ? howMany : Math.min(howMany, size);
   }

   public static int getPlayerXP(Player player) {
      return (int)(getExperienceForLevel(player.f_36078_) + player.f_36080_ * player.m_36323_());
   }

   public static int getLevelForExperience(int targetXp) {
      int level = 0;

      while (true) {
         int xpToNextLevel = xpBarCap(level);
         if (targetXp < xpToNextLevel) {
            return level;
         }

         level++;
         targetXp -= xpToNextLevel;
      }
   }

   public static int getExperienceForLevel(int level) {
      if (level == 0) {
         return 0;
      } else if (level <= 15) {
         return sum(level, 7, 2);
      } else {
         return level <= 30 ? 315 + sum(level - 15, 37, 5) : 1395 + sum(level - 30, 112, 9);
      }
   }

   public static int xpBarCap(int level) {
      if (level >= 30) {
         return 112 + (level - 30) * 9;
      } else {
         return level >= 15 ? 37 + (level - 15) * 5 : 7 + level * 2;
      }
   }

   private static int sum(int n, int a0, int d) {
      return n * (2 * a0 + (n - 1) * d) / 2;
   }

   public static String normalize(String s) {
      if (s == null) {
         return "";
      }

      s = s.trim().toLowerCase(Locale.ROOT);
      return s.replaceAll("\\s+", " ");
   }

   public static boolean matchesQuery(String name, String query) {
      String[] parts = query.split(" ");

      for (String p : parts) {
         if (!p.isEmpty()) {
            if (p.charAt(0) == '-') {
               String neg = p.substring(1);
               if (!neg.isEmpty() && name.contains(neg)) {
                  return false;
               }
            } else if (!name.contains(p)) {
               return false;
            }
         }
      }

      return true;
   }

   public static boolean isDigitsInRange(String s, int min, int max) {
      if (s != null && !s.isEmpty()) {
         for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c < '0' || c > '9') {
               return false;
            }
         }

         long v;
         try {
            v = Long.parseLong(s);
         } catch (NumberFormatException e) {
            return false;
         }

         return v >= min && v <= max;
      } else {
         return false;
      }
   }

   @Nullable
   public static ResourceLocation getEnchantmentId(CompoundTag arg) {
      return ResourceLocation.m_135820_(arg.m_128461_("id"));
   }

   public static Map<Enchantment, Integer> deserializeEnchantments(ListTag arg) {
      Map<Enchantment, Integer> map = Maps.newLinkedHashMap();

      for (int i = 0; i < arg.size(); i++) {
         CompoundTag compoundtag = arg.m_128728_(i);
         BuiltInRegistries.f_256876_.m_6612_(getEnchantmentId(compoundtag)).ifPresent(arg2 -> map.put(arg2, getEnchantmentLevel(compoundtag)));
      }

      return map;
   }

   public static int getEnchantmentLevel(CompoundTag arg) {
      return Mth.m_14045_(arg.m_128451_("lvl"), 0, 255);
   }
}
