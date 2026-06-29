package net.sixik.sdmshop.client.screen_new;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.ui.ContextMenuItem;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.Widget.DrawLayer;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sixik.sdmshop.client.screen_new.api.GUIShopMenu;
import net.sixik.sdmshop.client.screen_new.components.buyer.ShopBuyProductComponentModalPanel;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.utils.ShopContextMenuUtils;
import net.sixik.sdmshop.utils.ShopRenderUtils;
import net.sixik.sdmshop.utils.config.SDMEditConfigScreen;
import net.sixik.sdmshop.utils.rendering.ShopRenderingWrapper;

public class MainShopEntryButton extends SimpleTextButton {
   protected static final int fontH = Theme.DEFAULT.getFontHeight();
   protected SDMEditConfigScreen editConfigScreen;
   protected final MainShopEntryPanel entryPanel;
   protected final ShopEntry shopEntry;
   public String moneyText;
   public int textL;
   public Icon icon;
   public Component component;
   private int componentL;
   private String countString;
   private String limitString;
   private int limitStingL;
   private boolean hasLimit;
   private int limitColor;
   private boolean hasCount;
   private int countL;
   public static final Component FREE_COMPONENT = Component.m_237115_("sdm.shop.gui.panel.entry.price.empty");
   public static final int FREE_COMPONENT_L = Theme.DEFAULT.getStringWidth(FREE_COMPONENT);

   public MainShopEntryButton(MainShopEntryPanel panel, ShopEntry shopEntry) {
      super(panel, shopEntry.getTitle(), Icon.empty());
      this.entryPanel = panel;
      this.shopEntry = shopEntry;
      this.onInit();
   }

   public void onInit() {
      this.moneyText = this.shopEntry.getEntrySellerType().moneyToString(this.shopEntry);
      this.textL = Theme.DEFAULT.getStringWidth(this.moneyText);
      this.icon = ShopRenderUtils.getIconFromEntry(this.shopEntry);
      this.component = this.shopEntry.getType().isSell() ? Component.m_237115_("sdm.shop.entry.sell") : Component.m_237115_("sdm.shop.entry.buy");
      this.hasCount = this.shopEntry.getCount() > 1L;
      this.countString = String.valueOf(this.shopEntry.getCount());
      this.countL = Theme.DEFAULT.getStringWidth(this.countString);
      int remaining = this.shopEntry.getObjectLimitLeft(Minecraft.m_91087_().f_91074_);
      int totalLimit = this.shopEntry.getObjectLimit();
      if (remaining != Integer.MAX_VALUE && totalLimit > 0) {
         this.hasLimit = true;
         this.limitString = remaining + "/" + totalLimit;
         this.limitStingL = Theme.DEFAULT.getStringWidth(this.limitString);
         if (remaining <= 0) {
            this.limitColor = -43691;
            this.component = Component.m_237115_("sdm.shop.entry.sold");
         } else if (remaining == 1) {
            this.limitColor = -22016;
         } else {
            this.limitColor = -5592406;
         }
      } else {
         this.hasLimit = false;
      }

      this.componentL = Theme.DEFAULT.getStringWidth(this.component);
   }

   public void addMouseOverText(TooltipList list) {
      if (this.shopEntry != null) {
         this.shopEntry.addTooltipToList(list);
         this.shopEntry.getEntryType().addEntryTooltip(list, this.shopEntry);
         this.shopEntry.getEntrySellerType().addEntryTooltip(list, this.shopEntry);
      }
   }

   public boolean isEdit() {
      return false;
   }

   public void onClicked(MouseButton mouseButton) {
      MainShopScreen screen = (MainShopScreen)this.entryPanel.screen;
      if (mouseButton.isLeft()) {
         ShopBuyProductComponentModalPanel.openCentered(screen, this.shopEntry);
      } else {
         if (mouseButton.isRight()) {
            List<ContextMenuItem> contextMenu = ShopContextMenuUtils.getContextMenu(
               this.shopEntry, entry -> this.entryPanel.refreshWidgets(), s -> this.editConfigScreen = s, () -> {
                  if (this.editConfigScreen != null) {
                     this.getParent().refreshWidgets();
                     this.editConfigScreen.closeGui();
                  }
               }, s -> this.getGui().refreshWidgets(), 14
            );
            if (!contextMenu.isEmpty()) {
               this.getGui().openContextMenu(contextMenu);
            }
         }
      }
   }

   public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
      if (MainShopScreen.Instance.shouldRenderWidgets) {
         this.drawAfterBatch(graphics, theme, x, y, w, h);
      }
   }

   public boolean checkMouseOver(int mouseX, int mouseY) {
      return !MainShopScreen.Instance.shouldRenderWidgets ? false : super.checkMouseOver(mouseX, mouseY);
   }

   public DrawLayer getDrawLayer() {
      return DrawLayer.FOREGROUND;
   }

   public void drawBatch(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
      if (this.isMouseOver) {
         ShopRenderingWrapper.addBatchRect(graphics, x, y, w, h, GUIShopMenu.BACKGROUND_INT, GUIShopMenu.BORDER_3_INT);
      } else {
         ShopRenderingWrapper.addBatchRect(graphics, x, y, w, h, GUIShopMenu.BACKGROUND_INT, GUIShopMenu.BORDER_INT);
      }
   }

   public void drawAfterBatch(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
      int iconSize = w / 3;
      int iconSize3 = iconSize / 3;
      if (!this.icon.isEmpty()) {
         this.icon.draw(graphics, x + (this.width - iconSize) / 2, y + iconSize3, iconSize, iconSize);
      }

      if (this.hasCount) {
         graphics.m_280168_().m_85836_();
         graphics.m_280168_().m_252880_(0.0F, 0.0F, 200.0F);
         theme.drawString(graphics, this.countString, x + (w - this.countL) / 2 + iconSize / 2, y + iconSize);
         graphics.m_280168_().m_85849_();
      }

      if (this.hasLimit) {
         this.drawLimitBadge(graphics, theme, x, y, w);
      }

      int endElementY = y + (h - (fontH + 2));
      theme.drawString(graphics, this.component, x + (w - this.componentL) / 2, endElementY - (fontH + 2));
      if (this.shopEntry.getPrice() != 0.0) {
         this.shopEntry.getEntrySellerType().drawCentered(graphics, theme, x, endElementY, w, h, this.shopEntry.getPrice());
      } else {
         theme.drawString(graphics, FREE_COMPONENT, x + (w - FREE_COMPONENT_L) / 2, endElementY);
      }
   }

   private void drawLimitBadge(GuiGraphics graphics, Theme theme, int x, int y, int w) {
      PoseStack pose = graphics.m_280168_();
      pose.m_85836_();
      float scale = 0.75F;
      pose.m_85841_(0.75F, 0.75F, 1.0F);
      int destX = (int)((x + w - 4) / 0.75F - this.limitStingL);
      int destY = (int)((y + 4) / 0.75F);
      theme.drawString(graphics, this.limitString, destX, destY, this.limitColor);
      pose.m_85849_();
   }
}
