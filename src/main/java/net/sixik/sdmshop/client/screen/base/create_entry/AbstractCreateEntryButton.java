package net.sixik.sdmshop.client.screen.base.create_entry;

import dev.architectury.platform.Platform;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.ContextMenuItem;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.client.screen.base.AbstractShopScreen;
import net.sixik.sdmshop.old_api.shop.AbstractEntryType;
import net.sixik.sdmshop.shop.BaseShop;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.utils.ShopUtilsClient;

public class AbstractCreateEntryButton extends SimpleTextButton {
   public final AbstractEntryType shopEntryType;

   public AbstractCreateEntryButton(Panel panel, AbstractEntryType entryType) {
      super(panel, Component.m_237119_(), entryType.getCreativeIcon());
      this.shopEntryType = entryType;
   }

   public boolean isActive() {
      return Platform.isModLoaded(this.shopEntryType.getModId());
   }

   public void onClicked(MouseButton mouseButton) {
      if (this.isActive()) {
         AbstractCreateEntryScreen screen = (AbstractCreateEntryScreen)this.getGui();
         AbstractShopScreen shopScreen = screen.shopScreen;
         BaseShop shop = shopScreen.currentShop;
         if (mouseButton.isLeft()) {
            ShopEntry entry = new ShopEntry(shop, shopScreen.selectedTab);
            entry.setEntryType(this.shopEntryType.copy());
            ShopUtilsClient.addEntry(shop, entry);
            screen.closeGui();
         }

         if (mouseButton.isRight()) {
            List<ContextMenuItem> contextMenu = new ArrayList<>();
            if (!SDMShopClient.userData.getCreator().contains(this.shopEntryType.getId())) {
               contextMenu.add(new ContextMenuItem(Component.m_237115_("sdm.shop.context.entry.favorite"), Icons.ADD, b -> {
                  SDMShopClient.userData.getCreator().add(this.shopEntryType.getId());
                  SDMShopClient.userData.save();
               }));
            } else {
               contextMenu.add(new ContextMenuItem(Component.m_237115_("sdm.shop.context.user.unfavorite"), Icons.REMOVE, b -> {
                  SDMShopClient.userData.getCreator().remove(this.shopEntryType.getId());
                  SDMShopClient.userData.save();
               }));
            }

            screen.openContextMenu(contextMenu);
         }
      }
   }
}
