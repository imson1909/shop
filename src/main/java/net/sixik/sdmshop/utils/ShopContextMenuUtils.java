package net.sixik.sdmshop.utils;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.ContextMenuItem;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.sixik.sdmshop.network.sync.server.SendResetLimiterC2S;
import net.sixik.sdmshop.old_api.MoveType;
import net.sixik.sdmshop.old_api.shop.ShopObjectTypes;
import net.sixik.sdmshop.shop.BaseShop;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.shop.ShopTab;
import net.sixik.sdmshop.utils.config.SDMConfigGroup;
import net.sixik.sdmshop.utils.config.SDMEditConfigScreen;

public class ShopContextMenuUtils {
   public static final int ShowEdit = 2;
   public static final int ShowDelete = 4;
   public static final int ShowLimiter = 8;
   public static final int ShowMove = 16;
   public static final int ShowBasic = 14;
   public static final int ShowAll = 30;

   public static List<ContextMenuItem> getContextMenu(
      ShopTab shopTab, Consumer<ShopTab> onDelete, Consumer<SDMEditConfigScreen> onEditOpen, Runnable onEditClose
   ) {
      return getContextMenu(shopTab, onDelete, onEditOpen, onEditClose, 30);
   }

   public static List<ContextMenuItem> getContextMenu(
      ShopTab shopTab, Consumer<ShopTab> onDelete, Consumer<SDMEditConfigScreen> onEditOpen, Runnable onEditClose, int showBits
   ) {
      List<ContextMenuItem> contextMenu = new ObjectArrayList();
      BaseShop shop = shopTab.getOwnerShop();
      if (ShopUtils.isEditModeClient()) {
         if ((showBits & 2) != 0) {
            contextMenu.add(new ContextMenuItem(Component.m_237115_("sdm.shop.context.edit"), Icons.SETTINGS, button -> {
               ConfigGroup group = new SDMConfigGroup("sdm", b -> {
                  if (b) {
                     ShopUtilsClient.syncTab(shop, shopTab);
                  }

                  onEditClose.run();
               }).setNameKey("sidebar_button.sdm.shop");
               ConfigGroup g = group.getOrCreateSubgroup("shop").getOrCreateSubgroup("tab");
               shopTab.getConfig(g);
               SDMEditConfigScreen edit = new SDMEditConfigScreen(group);
               onEditOpen.accept(edit);
               edit.openGui();
            }));
         }

         if ((showBits & 4) != 0) {
            contextMenu.add(new ContextMenuItem(Component.m_237115_("sdm.shop.context.edit.delete"), Icons.REMOVE, b -> {
               ShopUtilsClient.removeTab(shop, shopTab);
               onDelete.accept(shopTab);
            }));
         }

         if ((showBits & 8) != 0) {
            contextMenu.add(
               new ContextMenuItem(
                  Component.m_237115_("sdm.shop.context.edit.reset_limiter"),
                  Icons.BOOK_RED,
                  b -> new SendResetLimiterC2S(shopTab.getId(), ShopObjectTypes.SHOP_TAB).sendToServer()
               )
            );
         }

         if ((showBits & 16) != 0) {
            contextMenu.add(
               new ContextMenuItem(
                  Component.m_237115_("sdm.shop.context.edit.move.up"), Icons.UP, b -> ShopUtilsClient.moveShopTab(shop, shopTab.getId(), MoveType.Up)
               )
            );
            contextMenu.add(
               new ContextMenuItem(
                  Component.m_237115_("sdm.shop.context.edit.move.down"), Icons.DOWN, b -> ShopUtilsClient.moveShopTab(shop, shopTab.getId(), MoveType.Down)
               )
            );
         }
      }

      return contextMenu;
   }

   public static List<ContextMenuItem> getContextMenu(
      ShopEntry shopEntry, Consumer<ShopEntry> onDelete, Consumer<SDMEditConfigScreen> onEditOpen, Runnable onEditClose, Consumer<ShopEntry> onCopy
   ) {
      return getContextMenu(shopEntry, onDelete, onEditOpen, onEditClose, onCopy, 30);
   }

   public static List<ContextMenuItem> getContextMenu(
      ShopEntry shopEntry,
      Consumer<ShopEntry> onDelete,
      Consumer<SDMEditConfigScreen> onEditOpen,
      Runnable onEditClose,
      Consumer<ShopEntry> onCopy,
      int showBits
   ) {
      List<ContextMenuItem> contextMenu = new ObjectArrayList();
      BaseShop shop = shopEntry.getOwnerShop();
      if (ShopUtils.isEditModeClient()) {
         if ((showBits & 2) != 0) {
            contextMenu.add(new ContextMenuItem(Component.m_237115_("sdm.shop.context.edit"), Icons.SETTINGS, button -> {
               ConfigGroup group = new SDMConfigGroup("sdm", accept -> {
                  if (accept) {
                     ShopUtilsClient.syncEntry(shop, shopEntry);
                  }

                  onEditClose.run();
               }).setNameKey("sidebar_button.sdm.shop");
               ConfigGroup g = group.getOrCreateSubgroup("shop").getOrCreateSubgroup("entry");
               shopEntry.getConfig(g);
               SDMEditConfigScreen edit = new SDMEditConfigScreen(group);
               onEditOpen.accept(edit);
               edit.openGui();
            }));
         }

         contextMenu.add(new ContextMenuItem(Component.m_237115_("sdm.shop.context.edit.duplicate"), Icons.ADD, b -> {
            ShopEntry entry = shopEntry.copy();
            ShopUtilsClient.addEntry(shop, entry);
            onCopy.accept(entry);
         }));
         TooltipList d1List = new TooltipList();
         d1List.add(Component.m_237113_("Copy " + shopEntry.getId()));
         ContextMenuItem cont = new ContextMenuItem(Component.m_237115_("sdm.shop.context.edit.copy_id"), Icons.INFO, b -> {
            Minecraft.m_91087_().f_91068_.m_90911_(shopEntry.getId().toString());
            Minecraft.m_91087_().f_91074_.m_213846_(Component.m_237113_("Copy Shop Entry " + shopEntry.getId()));
         });
         cont.addMouseOverText(d1List);
         contextMenu.add(cont);
         if ((showBits & 4) != 0) {
            contextMenu.add(new ContextMenuItem(Component.m_237115_("sdm.shop.context.edit.delete"), Icons.REMOVE, b -> {
               ShopUtilsClient.removeEntry(shop, shopEntry);
               onDelete.accept(shopEntry);
            }));
         }

         if ((showBits & 8) != 0) {
            contextMenu.add(
               new ContextMenuItem(
                  Component.m_237115_("sdm.shop.context.edit.reset_limiter"),
                  Icons.BOOK_RED,
                  b -> new SendResetLimiterC2S(shopEntry.getId(), ShopObjectTypes.SHOP_ENTRY).sendToServer()
               )
            );
         }

         if ((showBits & 16) != 0) {
            contextMenu.add(
               new ContextMenuItem(
                  Component.m_237115_("sdm.shop.context.edit.move.up"), Icons.UP, b -> ShopUtilsClient.moveShopEntry(shop, shopEntry.getId(), MoveType.Up)
               )
            );
            contextMenu.add(
               new ContextMenuItem(
                  Component.m_237115_("sdm.shop.context.edit.move.down"),
                  Icons.DOWN,
                  b -> ShopUtilsClient.moveShopEntry(shop, shopEntry.getId(), MoveType.Down)
               )
            );
         }
      }

      return contextMenu;
   }
}
