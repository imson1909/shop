package net.sixik.sdmshop.client.screen_new.components.creator.entry;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import net.minecraft.network.chat.Component;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.client.screen_new.components.creator.ShopCreatorComponentModalPanel;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.utils.config.SDMConfigGroup;
import net.sixik.sdmshop.utils.config.SDMEditConfigScreen;

public class ShopCreatorEntryPanel extends Panel {
   protected final ShopCreatorComponentModalPanel modalPanel;
   private SDMEditConfigScreen configScreen;
   protected ShopCreatorEntryTypesPanel entryTypesPanel;
   protected SimpleTextButton editButton;
   protected ShopCreatorEntrySelectedCategory entrySelectedCategoryPanel;
   public static ShopEntry shopEntry;

   public ShopCreatorEntryPanel(ShopCreatorComponentModalPanel panel) {
      super(panel);
      this.modalPanel = panel;
      shopEntry = new ShopEntry(SDMShopClient.CurrentShop);
   }

   public void addWidgets() {
      this.add(this.entryTypesPanel = new ShopCreatorEntryTypesPanel(this));
      this.add(this.entrySelectedCategoryPanel = new ShopCreatorEntrySelectedCategory(this));
      this.add(this.editButton = new SimpleTextButton(this, Component.m_237113_("Edit Entry"), Icons.SETTINGS) {
         public void onClicked(MouseButton button) {
            if (ShopCreatorEntryPanel.shopEntry.getEntryType().getClass() != ShopCreatorComponentModalPanel.Data.Entry.selectedType.getClass()) {
               ShopCreatorEntryPanel.shopEntry.setEntryType(ShopCreatorComponentModalPanel.Data.Entry.selectedType.copy());
            }

            ShopEntry entry = new ShopEntry(ShopCreatorEntryPanel.shopEntry.getOwnerShop());
            entry.deserialize(ShopCreatorEntryPanel.shopEntry.serialize());
            ConfigGroup group = new SDMConfigGroup("sdm", accept -> {
               if (accept) {
                  ShopCreatorEntryPanel.shopEntry.deserialize(entry.serialize());
               }

               ShopCreatorEntryPanel.this.configScreen.closeGui();
            }).setNameKey("sidebar_button.sdm.shop");
            ConfigGroup g = group.getOrCreateSubgroup("shop").getOrCreateSubgroup("entry");
            entry.getConfig(g);
            ShopCreatorEntryPanel.this.configScreen = new SDMEditConfigScreen(group);
            ShopCreatorEntryPanel.this.configScreen.openGui();
         }

         public boolean shouldDraw() {
            return ShopCreatorEntrySelectedCategory.isTabSelected();
         }
      });
   }

   public void alignWidgets() {
      this.entryTypesPanel.setWidth(this.width);
      this.entryTypesPanel.clearWidgets();
      this.entryTypesPanel.addWidgets();
      this.entryTypesPanel.alignWidgets();
      this.alignWidgetsWithoutEntryTypes();
   }

   public void alignWidgetsWithoutEntryTypes() {
      this.entrySelectedCategoryPanel.posX = 4;
      this.entrySelectedCategoryPanel.width = this.width - 8;
      this.entrySelectedCategoryPanel.posY = this.entryTypesPanel.posY + this.entryTypesPanel.height + 2;
      this.entrySelectedCategoryPanel.clearWidgets();
      this.entrySelectedCategoryPanel.addWidgets();
      this.entrySelectedCategoryPanel.alignWidgets();
      this.editButton.posX = 4;
      this.editButton.width = this.entrySelectedCategoryPanel.width;
      this.editButton.height = 20;
      this.editButton.posY = this.entrySelectedCategoryPanel.posY + this.entrySelectedCategoryPanel.height + 2;
   }
}
