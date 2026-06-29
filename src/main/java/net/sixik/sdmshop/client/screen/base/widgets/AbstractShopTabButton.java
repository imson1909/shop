package net.sixik.sdmshop.client.screen.base.widgets;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.ContextMenuItem;
import dev.ftb.mods.ftblibrary.ui.GuiHelper;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sixik.sdmshop.client.screen.base.AbstractShopScreen;
import net.sixik.sdmshop.network.sync.server.SendResetLimiterC2S;
import net.sixik.sdmshop.old_api.MoveType;
import net.sixik.sdmshop.old_api.shop.ShopObjectTypes;
import net.sixik.sdmshop.shop.BaseShop;
import net.sixik.sdmshop.shop.ShopTab;
import net.sixik.sdmshop.utils.ShopUtils;
import net.sixik.sdmshop.utils.ShopUtilsClient;
import net.sixik.sdmshop.utils.config.SDMConfigGroup;
import net.sixik.sdmshop.utils.config.SDMEditConfigScreen;

public class AbstractShopTabButton extends SimpleTextButton {
   protected ShopTab shopTab;
   protected boolean edit;

   public AbstractShopTabButton(Panel panel, ShopTab shopTab) {
      this(panel, shopTab, false);
   }

   public AbstractShopTabButton(Panel panel, ShopTab shopTab, boolean edit) {
      super(
         panel,
         (Component)(shopTab != null ? shopTab.title : Component.m_237119_()),
         (Icon)(shopTab != null ? shopTab.getRenderComponent().getIcon() : Icon.empty())
      );
      this.edit = edit;
      this.shopTab = shopTab;
      if (edit) {
         this.icon = Icons.ADD;
         this.title = Component.m_237113_("Create");
      }
   }

   public void addMouseOverText(TooltipList list) {
      if (this.shopTab != null) {
         if (this.shopTab.title != null && !this.shopTab.title.getString().isEmpty()) {
            list.add(this.shopTab.title);
         }

         this.shopTab.addTooltipToList(list);
      }
   }

   public void onClicked(MouseButton mouseButton) {
      AbstractShopScreen screen = this.getShopScreen();
      BaseShop shop = screen.currentShop;
      boolean isClientEdit = ShopUtils.isEditModeClient();
      if (mouseButton.isLeft()) {
         if (this.isEdit()) {
            ShopUtilsClient.addTab(shop, new ShopTab(shop));
         } else {
            screen.selectTab(this.shopTab.getId());
         }
      } else {
         if (mouseButton.isRight() && isClientEdit && !this.isEdit()) {
            List<ContextMenuItem> contextMenu = new ArrayList<>();
            contextMenu.add(new ContextMenuItem(Component.m_237115_("sdm.shop.context.edit"), Icons.SETTINGS, button -> {
               ConfigGroup group = new SDMConfigGroup("sdm", b -> {
                  if (b) {
                     ShopUtilsClient.syncTab(shop, this.shopTab);
                  }

                  screen.openGui();
               }).setNameKey("sidebar_button.sdm.shop");
               ConfigGroup g = group.getOrCreateSubgroup("shop").getOrCreateSubgroup("tab");
               this.shopTab.getConfig(g);
               new SDMEditConfigScreen(group).openGui();
            }));
            contextMenu.add(new ContextMenuItem(Component.m_237115_("sdm.shop.context.edit.delete"), Icons.REMOVE, b -> {
               if (screen.selectedTab != null && Objects.equals(screen.selectedTab, this.shopTab.getId())) {
                  screen.selectedTab = null;
               }

               ShopUtilsClient.removeTab(shop, this.shopTab);
            }));
            contextMenu.add(
               new ContextMenuItem(
                  Component.m_237115_("sdm.shop.context.edit.reset_limiter"),
                  Icons.BOOK_RED,
                  b -> new SendResetLimiterC2S(this.shopTab.getId(), ShopObjectTypes.SHOP_TAB).sendToServer()
               )
            );
            contextMenu.add(
               new ContextMenuItem(
                  Component.m_237115_("sdm.shop.context.edit.move.up"), Icons.UP, b -> ShopUtilsClient.moveShopTab(shop, this.shopTab.getId(), MoveType.Up)
               )
            );
            contextMenu.add(
               new ContextMenuItem(
                  Component.m_237115_("sdm.shop.context.edit.move.down"),
                  Icons.DOWN,
                  b -> ShopUtilsClient.moveShopTab(shop, this.shopTab.getId(), MoveType.Down)
               )
            );
            screen.openContextMenu(contextMenu);
         }
      }
   }

   public boolean isEdit() {
      return this.edit;
   }

   public ShopTab getShopTab() {
      return this.shopTab;
   }

   public AbstractShopScreen getShopScreen() {
      return (AbstractShopScreen)this.getGui();
   }

   public void drawSelected(GuiGraphics graphics, int x, int y, int w, int h) {
      GuiHelper.drawHollowRect(graphics, x, y, w, h, Color4I.WHITE, false);
   }
}
