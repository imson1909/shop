package net.sixik.sdmshop.utils;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.network.sync.server.SendAddEntryC2S;
import net.sixik.sdmshop.network.sync.server.SendAddTabC2S;
import net.sixik.sdmshop.network.sync.server.SendChangeEntryC2S;
import net.sixik.sdmshop.network.sync.server.SendChangeShopParamsC2S;
import net.sixik.sdmshop.network.sync.server.SendChangeTabC2S;
import net.sixik.sdmshop.network.sync.server.SendMoveEntryC2S;
import net.sixik.sdmshop.network.sync.server.SendMoveTabC2S;
import net.sixik.sdmshop.network.sync.server.SendRemoveEntryC2S;
import net.sixik.sdmshop.network.sync.server.SendRemoveTabC2S;
import net.sixik.sdmshop.old_api.MoveType;
import net.sixik.sdmshop.shop.BaseShop;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.shop.ShopTab;

@OnlyIn(Dist.CLIENT)
public class ShopUtilsClient {
   private static final UUID EMPTY = UUID.randomUUID();
   public static final Icon FAVORITE_ICON = Icons.STAR;

   public static BaseShop getShop() {
      return SDMShopClient.CurrentShop;
   }

   public static int getTick() {
      return Minecraft.m_91087_().f_91065_.m_93079_();
   }

   public static void changeParams(BaseShop shop) {
      new SendChangeShopParamsC2S(shop).sendToServer();
   }

   public static boolean moveShopEntry(BaseShop shop, UUID entry, MoveType type) {
      if (shop.moveEntry(entry, type)) {
         shop.onChange();
         new SendMoveEntryC2S(shop.getId(), entry, EMPTY, type).sendToServer();
         return true;
      } else {
         return false;
      }
   }

   public static boolean moveShopTab(BaseShop shop, UUID entry, MoveType type) {
      if (shop.moveTab(entry, type)) {
         shop.onChange();
         new SendMoveTabC2S(shop.getId(), entry, EMPTY, type).sendToServer();
         return true;
      } else {
         return false;
      }
   }

   public static boolean swapShopEntries(BaseShop shop, UUID from, UUID to, MoveType type) {
      if (shop.swapEntries(from, to, type)) {
         shop.onChange();
         new SendMoveEntryC2S(shop.getId(), from, to, type).sendToServer();
         return true;
      } else {
         return false;
      }
   }

   public static void changeEntry(BaseShop shop, ShopEntry entry, Consumer<ShopEntry> entryConsumer) {
      entryConsumer.accept(entry);
      shop.onChange();
      syncEntry(shop, entry);
   }

   public static void addEntry(BaseShop shop, ShopEntry entry) {
      if (shop.addEntry(entry)) {
         shop.onChange();
         new SendAddEntryC2S(shop, entry).sendToServer();
      }
   }

   public static void removeEntry(BaseShop shop, ShopEntry entry) {
      if (shop.removeEntry(entry).success()) {
         shop.onChange();
         new SendRemoveEntryC2S(shop, entry).sendToServer();
      }
   }

   public static void syncEntry(BaseShop shop, ShopEntry entry) {
      new SendChangeEntryC2S(shop.getId(), entry.getId(), entry.serialize()).sendToServer();
   }

   public static boolean swapShopTabs(BaseShop shop, UUID from, UUID to, MoveType type) {
      if (shop.swapTabs(from, to, type)) {
         shop.onChange();
         new SendMoveTabC2S(shop.getId(), from, to, type).sendToServer();
         return true;
      } else {
         return false;
      }
   }

   public static void changeTab(BaseShop shop, ShopTab tab, Consumer<ShopTab> tabConsumer) {
      tabConsumer.accept(tab);
      shop.onChange();
      syncTab(shop, tab);
   }

   public static void addTab(BaseShop shop, ShopTab tab) {
      if (shop.addTab(tab)) {
         shop.onChange();
         new SendAddTabC2S(shop, tab).sendToServer();
      }
   }

   public static void removeTab(BaseShop shop, ShopTab tab) {
      if (shop.removeTab(tab).success()) {
         shop.onChange();
         new SendRemoveTabC2S(shop, tab).sendToServer();
      }
   }

   public static void syncTab(BaseShop shop, ShopTab tab) {
      new SendChangeTabC2S(shop.getId(), tab.getId(), tab.serialize()).sendToServer();
   }

   public static void addFavorite(ShopEntry entry) {
      SDMShopClient.userData.getEntries().add(entry.getId());
      SDMShopClient.userData.save();
   }

   public static void removeFavorite(ShopEntry entry) {
      SDMShopClient.userData.getEntries().remove(entry.getId());
      SDMShopClient.userData.save();
   }

   public static boolean isFavorite(ShopEntry entry) {
      return entry != null && SDMShopClient.userData.getEntries().contains(entry.getId());
   }
}
