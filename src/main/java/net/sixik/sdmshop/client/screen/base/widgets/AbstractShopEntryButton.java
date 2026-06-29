package net.sixik.sdmshop.client.screen.base.widgets;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftblibrary.ui.ContextMenuItem;
import dev.ftb.mods.ftblibrary.ui.GuiHelper;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.client.screen.base.AbstractShopScreen;
import net.sixik.sdmshop.network.sync.server.SendResetLimiterC2S;
import net.sixik.sdmshop.old_api.CustomIcon;
import net.sixik.sdmshop.old_api.MoveType;
import net.sixik.sdmshop.old_api.ShopEntryType;
import net.sixik.sdmshop.old_api.shop.ShopObjectTypes;
import net.sixik.sdmshop.shop.BaseShop;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.utils.ShopEntryTypeCreator;
import net.sixik.sdmshop.utils.ShopInputHelper;
import net.sixik.sdmshop.utils.ShopUtils;
import net.sixik.sdmshop.utils.ShopUtilsClient;
import net.sixik.sdmshop.utils.config.SDMConfigGroup;
import net.sixik.sdmshop.utils.config.SDMEditConfigScreen;

public abstract class AbstractShopEntryButton extends SimpleTextButton {
   protected ShopEntry shopEntry;
   protected boolean edit;
   protected boolean favorite = false;

   public AbstractShopEntryButton(Panel panel, ShopEntry entry) {
      this(panel, entry, false);
   }

   public AbstractShopEntryButton(Panel panel, ShopEntry entry, boolean isEdit) {
      super(panel, Component.m_237119_(), getIconFromEntry(entry));
      this.shopEntry = entry;
      this.edit = isEdit;
      if (this.edit) {
         this.icon = Icons.ADD;
         this.title = Component.m_237113_("Create");
      } else {
         this.favorite = ShopUtilsClient.isFavorite(this.shopEntry);
      }
   }

   public void addMouseOverText(TooltipList list) {
      if (this.shopEntry != null) {
         this.shopEntry.addTooltipToList(list);
         this.shopEntry.getEntryType().addEntryTooltip(list, this.shopEntry);
         this.shopEntry.getEntrySellerType().addEntryTooltip(list, this.shopEntry);
      }
   }

   public void onClicked(MouseButton mouseButton) {
      AbstractShopScreen screen = this.getShopScreen();
      BaseShop shop = screen.currentShop;
      boolean isClientEdit = ShopUtils.isEditModeClient();
      if (mouseButton.isLeft()) {
         if (this.isEdit()) {
            if (SDMShopClient.userData.getCreator().isEmpty()) {
               screen.openCreateEntryScreen();
            } else {
               screen.openContextMenu(ShopEntryTypeCreator.createContext(screen));
            }

            return;
         }

         if (ShopUtils.isEditModeClient()) {
            if (ShopInputHelper.isControl()) {
               if (screen.selectedEntryId == null) {
                  screen.selectedEntryId = this.shopEntry.getId();
                  return;
               }

               if (Objects.equals(screen.selectedEntryId, this.shopEntry.getId())) {
                  screen.selectedEntryId = null;
                  return;
               }

               MoveType type = ShopInputHelper.isMoveInsert() ? MoveType.Insert : (ShopInputHelper.isMoveSwap() ? MoveType.Swap : null);
               if (type != null) {
                  ShopUtilsClient.swapShopEntries(shop, screen.selectedEntryId, this.shopEntry.getId(), type);
                  screen.selectedEntryId = null;
               }

               return;
            }

            if (ShopInputHelper.isShift() && this.shopEntry.getEntryType().getProperty().sellType.isBoth()) {
               ShopUtilsClient.changeEntry(
                  shop, this.shopEntry, entry -> entry.changeType(this.shopEntry.getType().isSell() ? ShopEntryType.Buy : ShopEntryType.Sell)
               );
               return;
            }
         }

         screen.openBuyScreen(this);
      }

      if (mouseButton.isRight()) {
         List<ContextMenuItem> contextMenu = new ArrayList<>();
         if (isClientEdit && !this.isEdit()) {
            contextMenu.add(new ContextMenuItem(Component.m_237115_("sdm.shop.context.edit"), Icons.SETTINGS, button -> {
               ConfigGroup group = new SDMConfigGroup("sdm", accept -> {
                  if (accept) {
                     ShopUtilsClient.syncEntry(shop, this.shopEntry);
                  }

                  screen.openGui();
               }).setNameKey("sidebar_button.sdm.shop");
               ConfigGroup g = group.getOrCreateSubgroup("shop").getOrCreateSubgroup("entry");
               this.shopEntry.getConfig(g);
               new SDMEditConfigScreen(group).openGui();
            }));
            contextMenu.add(
               new ContextMenuItem(
                  Component.m_237115_("sdm.shop.context.edit.duplicate"), Icons.ADD, b -> ShopUtilsClient.addEntry(shop, this.shopEntry.copy())
               )
            );
            TooltipList d1List = new TooltipList();
            d1List.add(Component.m_237113_("Copy " + this.shopEntry.getId()));
            ContextMenuItem cont = new ContextMenuItem(Component.m_237115_("sdm.shop.context.edit.copy_id"), Icons.INFO, b -> {
               Minecraft.m_91087_().f_91068_.m_90911_(this.shopEntry.getId().toString());
               Minecraft.m_91087_().f_91074_.m_213846_(Component.m_237113_("Copy Shop Entry " + this.shopEntry.getId()));
            });
            cont.addMouseOverText(d1List);
            contextMenu.add(cont);
            contextMenu.add(
               new ContextMenuItem(Component.m_237115_("sdm.shop.context.edit.delete"), Icons.REMOVE, b -> ShopUtilsClient.removeEntry(shop, this.shopEntry))
            );
            contextMenu.add(
               new ContextMenuItem(
                  Component.m_237115_("sdm.shop.context.edit.reset_limiter"),
                  Icons.BOOK_RED,
                  b -> new SendResetLimiterC2S(this.shopEntry.getId(), ShopObjectTypes.SHOP_ENTRY).sendToServer()
               )
            );
            contextMenu.add(
               new ContextMenuItem(
                  Component.m_237115_("sdm.shop.context.edit.move.up"), Icons.UP, b -> ShopUtilsClient.moveShopEntry(shop, this.shopEntry.getId(), MoveType.Up)
               )
            );
            contextMenu.add(
               new ContextMenuItem(
                  Component.m_237115_("sdm.shop.context.edit.move.down"),
                  Icons.DOWN,
                  b -> ShopUtilsClient.moveShopEntry(shop, this.shopEntry.getId(), MoveType.Down)
               )
            );
         }

         if (!this.isEdit()) {
            if (this.isFavorite()) {
               contextMenu.add(
                  new ContextMenuItem(Component.m_237115_("sdm.shop.context.user.unfavorite"), ShopUtilsClient.FAVORITE_ICON.withColor(Color4I.GRAY), b -> {
                     ShopUtilsClient.removeFavorite(this.shopEntry);
                     this.favorite = false;
                     screen.onRefresh();
                  })
               );
            } else {
               contextMenu.add(new ContextMenuItem(Component.m_237115_("sdm.shop.context.entry.favorite"), ShopUtilsClient.FAVORITE_ICON, b -> {
                  ShopUtilsClient.addFavorite(this.shopEntry);
                  this.favorite = true;
                  screen.onRefresh();
               }));
            }
         }

         if (!contextMenu.isEmpty()) {
            screen.openContextMenu(contextMenu);
            return;
         }
      }
   }

   public boolean isEdit() {
      return this.edit;
   }

   public boolean isFavorite() {
      return this.favorite;
   }

   public ShopEntry getShopEntry() {
      return this.shopEntry;
   }

   public boolean isSelected() {
      return this.getShopScreen().selectedEntryId != null
         && this.shopEntry != null
         && Objects.equals(this.getShopScreen().selectedEntryId, this.shopEntry.getId());
   }

   public void drawSelected(GuiGraphics graphics, int x, int y, int w, int h) {
      GuiHelper.drawHollowRect(graphics, x, y, w, h, Color4I.WHITE, false);
   }

   public AbstractShopScreen getShopScreen() {
      return (AbstractShopScreen)this.getGui();
   }

   public void drawFavorite(GuiGraphics graphics, int x, int y, int w, int h) {
      if (this.isFavorite()) {
         int size = w >= 20 ? 8 : 4;
         ShopUtilsClient.FAVORITE_ICON.draw(graphics, x + w - size, y, size, size);
      }
   }

   public static Icon getIconFromEntry(ShopEntry entry) {
      return getIconFromEntry(entry, ShopUtilsClient.getTick());
   }

   public static Icon getIconFromEntry(ShopEntry entry, int tick) {
      if (entry == null) {
         return Icon.empty();
      }

      Icon i1 = null;
      if ((entry.getRenderComponent().getIcon().isEmpty() || entry.getRenderComponent().getIcon() instanceof ItemIcon itemIcon && itemIcon.isEmpty())
         && entry.getEntryType() instanceof CustomIcon customIcon) {
         i1 = customIcon.getCustomIcon(entry, tick);
      }

      if (i1 == null) {
         i1 = entry.getRenderComponent().getIcon();
      }

      return i1;
   }
}
