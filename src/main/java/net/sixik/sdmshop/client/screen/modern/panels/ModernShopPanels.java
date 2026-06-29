package net.sixik.sdmshop.client.screen.modern.panels;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.TextField;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.client.screen.base.AbstractShopPanel;
import net.sixik.sdmshop.client.screen.base.AbstractShopScreen;
import net.sixik.sdmshop.client.screen.base.panels.AbstractShopMoneyPanel;
import net.sixik.sdmshop.client.screen.base.widgets.AbstractShopEntrySearch;
import net.sixik.sdmshop.client.screen.modern.wallet.OpenWalletButton;
import net.sixik.sdmshop.client.screen.modern.wallet.PlayerWallet;
import net.sixik.sdmshop.currencies.SDMCoin;
import net.sixik.sdmshop.utils.ShopUtils;
import net.sixik.sdmshop.utils.ShopUtilsClient;
import net.sixik.sdmshop.utils.config.SDMConfigGroup;
import net.sixik.sdmshop.utils.config.SDMEditConfigScreen;
import net.sixik.sdmuilib.client.utils.RenderHelper;
import net.sixik.sdmuilib.client.utils.TextHelper;
import net.sixik.sdmuilib.client.utils.math.Vector2f;
import net.sixik.sdmuilib.client.utils.misc.RGBA;

public class ModernShopPanels {
   public static class BottomPanel extends AbstractShopMoneyPanel {
      public BottomPanel(Panel panel) {
         super(panel);
      }

      public void addWidgets() {
         this.add(this.moneyTitleField = new TextField(this) {
            public void addMouseOverText(TooltipList list) {
               list.add(Component.m_237113_("Coming soon money menu..."));
            }
         });
         this.add(this.moneyCountField = new TextField(this) {
            public void addMouseOverText(TooltipList list) {
               list.add(Component.m_237113_("Coming soon money menu..."));
            }
         });
         this.add(
            this.openWalletButton = new OpenWalletButton(this, Component.m_237119_(), Icon.empty(), (simpleButton, mouseButton) -> new PlayerWallet().openGui())
         );
         this.setProperty();
      }

      public void alignWidgets() {
         this.setProperty();
      }

      public void setProperty() {
         Component textTitle = Component.m_237115_("sdm.shop.modern.ui.money");
         int w = TextHelper.getTextWidth(textTitle.getString());
         int w1 = this.width - w;
         int w2 = w1 / 2;
         this.moneyTitleField.addFlags(32);
         this.moneyTitleField.setSize(this.width - 1, this.height);
         this.moneyTitleField.setMaxWidth(this.width - 2);
         this.moneyTitleField.setText(textTitle);
         this.moneyTitleField.setX(w2);
         this.moneyTitleField.setY(2);
         this.moneyTitleField.setScale(1.2F);
         String textMoney = ShopUtils.moneyToString(Minecraft.m_91087_().f_91074_, SDMCoin.getId());
         w = TextHelper.getTextWidth(textMoney);
         w1 = this.width - w;
         w2 = w1 / 2;
         this.openWalletButton.setSize(this.height - this.moneyTitleField.height - 4, this.height - this.moneyTitleField.height - 4);
         this.openWalletButton.setX(this.width / 2 - this.openWalletButton.width / 2);
         this.openWalletButton.setY(this.moneyTitleField.posY + Theme.DEFAULT.getFontHeight() + 1);
      }

      public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
         RenderHelper.drawRoundedRectDown(graphics, x, y, w, h, 10, RGBA.create(0, 0, 0, 127));
      }
   }

   public static class TopEntriesPanel extends AbstractShopPanel {
      public AbstractShopEntrySearch textBox;
      public SimpleTextButton infoButton;
      public SimpleTextButton settingButton;

      public TopEntriesPanel(Panel panel) {
         super(panel);
      }

      public void addWidgets() {
         final AbstractShopScreen shopScreen = this.getShopScreen();
         this.add(this.textBox = new AbstractShopEntrySearch(this) {
            public void drawTextBox(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
               RGBA.create(0, 0, 0, 127).drawRoundFill(graphics, x, y, w, h, 8);
            }
         });
         if (ShopUtils.isEditModeClient()) {
            this.add(this.infoButton = new SimpleTextButton(this, Component.m_237119_(), Icons.INFO) {
               public void onClicked(MouseButton mouseButton) {
                  TopEntriesPanel.this.getShopScreen().openInfoScreen();
               }

               public void addMouseOverText(TooltipList list) {
                  list.add(Component.m_237115_("sdm.shop.modern.ui.keybinding.info"));
                  list.add(Component.m_237119_());
                  list.add(Component.m_237115_("sdm.shop.modern.ui.keybinding.info.entry"));
                  list.add(Component.m_237115_("sdm.shop.modern.ui.keybinding.info.entry.change_buy_sell"));
                  list.add(Component.m_237115_("sdm.shop.modern.ui.keybinding.info.entry.move"));
               }

               public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
               }
            });
         }

         this.add(this.settingButton = new SimpleTextButton(this, Component.m_237119_(), Icons.SETTINGS) {
            public void onClicked(MouseButton mouseButton) {
               if (mouseButton.isLeft()) {
                  ConfigGroup group = new SDMConfigGroup("sdm", accept -> {
                     if (accept) {
                        SDMShopClient.userData.save();
                     }

                     shopScreen.openGui();
                  }).setNameKey("sidebar_button.sdm.shop");
                  ConfigGroup g = group.getOrCreateSubgroup("shop");
                  SDMShopClient.userData.getConfig(g);
                  new SDMEditConfigScreen(group).openGui();
               } else if (mouseButton.isRight() && ShopUtils.isEditModeClient()) {
                  ConfigGroup group = new SDMConfigGroup("sdm", accept -> {
                     if (accept) {
                        ShopUtilsClient.changeParams(shopScreen.currentShop);
                     }

                     shopScreen.openGui();
                  }).setNameKey("sidebar_button.sdm.shop");
                  ConfigGroup g = group.getOrCreateSubgroup("shop");
                  shopScreen.currentShop.getParams().getConfig(g);
                  new SDMEditConfigScreen(group).openGui();
               }
            }

            public void addMouseOverText(TooltipList list) {
               list.add(Component.m_237115_("sdm.shop.context.setting"));
               if (ShopUtils.isEditModeClient()) {
                  list.add(Component.m_237119_());
                  list.add(Component.m_237115_("sdm.shop.context.edit.settings.tooltip"));
               }
            }

            public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            }
         });
         this.textBox.setText(this.getShopScreen().searchField);
      }

      public void alignWidgets() {
         this.setProperty();
      }

      public void setProperty() {
         int h = this.height / 4;
         this.textBox.ghostText = Component.m_237115_("sdm.shop.modern.ui.search.ghost_text").getString();
         this.textBox.setPos(h / 2 + 6, h / 2);
         this.textBox.setSize(this.width / 2 - h, this.height - h);
         if (ShopUtils.isEditModeClient()) {
            this.infoButton.setSize(this.height - h, this.height - h);
            this.infoButton.setPos(this.width - this.infoButton.width - 6, h / 2);
            this.settingButton.setSize(this.infoButton.width, this.infoButton.height);
            this.settingButton.setPos(this.infoButton.posX - this.settingButton.width - 2, this.infoButton.posY);
         } else {
            this.settingButton.setSize(this.height - h, this.height - h);
            this.settingButton.setPos(this.width - this.settingButton.width - 6, h / 2);
         }
      }

      public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
         RenderHelper.drawRoundedRectUp(graphics, x, y, w, h, 10, RGBA.create(0, 0, 0, 127));
      }
   }

   public static class TopPanel extends AbstractShopPanel {
      public TextField categoryField;

      public TopPanel(Panel panel) {
         super(panel);
      }

      public void addWidgets() {
         this.add(this.categoryField = new TextField(this));
         this.setProperty();
      }

      public void alignWidgets() {
         this.setProperty();
      }

      public void setProperty() {
         Vector2f size = TextHelper.getTextRenderSize(Component.m_237115_("sdm.shop.modern.ui.tab_categories").getString(), this.width, 1.2F, 50);
         int d2 = 9;
         int w1 = (int)(this.width - size.x);
         int w2 = w1 / 2;
         int h1 = this.height / 2;
         h1 -= d2 / 2;
         this.categoryField.setPos(w2, h1);
         this.categoryField.setSize(this.width, this.height);
         this.categoryField.setMaxWidth(this.width);
         this.categoryField.setText(Component.m_237115_("sdm.shop.modern.ui.tab_categories"));
         this.categoryField.setScale(size.y);
      }

      public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
         RenderHelper.drawRoundedRectUp(graphics, x, y, w, h, 10, RGBA.create(0, 0, 0, 127));
      }
   }
}
