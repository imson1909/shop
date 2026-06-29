package net.sixik.sdmshop.client.screen_new.components.creator.category;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.TextBox;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import net.minecraft.network.chat.Component;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.client.screen_new.components.creator.ShopCreatorComponentModalPanel;
import net.sixik.sdmshop.shop.ShopTab;
import net.sixik.sdmshop.utils.config.SDMConfigGroup;
import net.sixik.sdmshop.utils.config.SDMEditConfigScreen;

public class ShopCreatorCategoryPanel extends Panel {
   protected final ShopCreatorComponentModalPanel modalPanel;
   protected TextBox categoryNameBox;
   protected SimpleTextButton editCategoryButton;
   private SDMEditConfigScreen configScreen;
   public static ShopTab shopTab;

   public ShopCreatorCategoryPanel(ShopCreatorComponentModalPanel panel) {
      super(panel);
      this.modalPanel = panel;
      shopTab = new ShopTab(SDMShopClient.CurrentShop);
   }

   public void addWidgets() {
      this.add(this.categoryNameBox = new TextBox(this) {
         public void onTextChanged() {
            ShopCreatorComponentModalPanel.Data.Category.name = this.getText();
            ShopCreatorCategoryPanel.shopTab.title = Component.m_237115_(ShopCreatorComponentModalPanel.Data.Category.name);
         }
      });
      this.categoryNameBox.ghostText = "Enter Name...";
      this.add(this.editCategoryButton = new SimpleTextButton(this, Component.m_237113_("Edit Category"), Icons.SETTINGS) {
         public void onClicked(MouseButton button) {
            ShopCreatorCategoryPanel.shopTab.title = Component.m_237113_(ShopCreatorComponentModalPanel.Data.Category.name);
            ConfigGroup group = new SDMConfigGroup("sdm", accept -> {
               if (accept) {
                  ShopCreatorComponentModalPanel.Data.Category.name = ShopCreatorCategoryPanel.shopTab.title.getString();
                  ShopCreatorCategoryPanel.this.categoryNameBox.setText(ShopCreatorComponentModalPanel.Data.Category.name);
               }

               ShopCreatorCategoryPanel.this.configScreen.closeGui();
            }).setNameKey("sidebar_button.sdm.shop");
            ConfigGroup g = group.getOrCreateSubgroup("shop").getOrCreateSubgroup("tab");
            ShopCreatorCategoryPanel.shopTab.getConfig(g);
            ShopCreatorCategoryPanel.this.configScreen = new SDMEditConfigScreen(group);
            ShopCreatorCategoryPanel.this.configScreen.openGui();
         }
      });
   }

   public void alignWidgets() {
      this.categoryNameBox.setText(ShopCreatorComponentModalPanel.Data.Category.name);
      this.categoryNameBox.setHeight(20);
      this.categoryNameBox.setWidth(this.width - 16);
      this.categoryNameBox.setX(8);
      this.categoryNameBox.setY(2);
      this.editCategoryButton.setX(8);
      this.editCategoryButton.setWidth(this.categoryNameBox.width);
      this.editCategoryButton.posY = this.categoryNameBox.posY + this.categoryNameBox.height + 2;
      this.editCategoryButton.setHeight(20);
   }
}
