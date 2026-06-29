package net.sixik.sdmshop.client.screen_new.components.creator.entry;

import com.mojang.blaze3d.platform.Window;
import dev.ftb.mods.ftblibrary.ui.BaseScreen;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.minecraft.client.gui.GuiGraphics;
import net.sixik.sdmshop.client.screen_new.api.GUIShopMenu;
import net.sixik.sdmshop.utils.rendering.ShopRenderingWrapper;

public class ShopCreatorSelectCategoryScreen extends BaseScreen {
   protected ShopCreatorSelectCategoryListBoxWip selectCategoryListBox;
   public final Runnable onSelected;

   public ShopCreatorSelectCategoryScreen(Runnable onSelected) {
      this.onSelected = onSelected;
   }

   public boolean onInit() {
      Window panel = this.getScreen();
      int sw = panel.m_85441_();
      int sh = panel.m_85442_();
      int w = this.getWidth();
      int h = this.getHeight();
      this.setPos((sw - w) / 2, (sh - h) / 2);
      return super.onInit();
   }

   public void addWidgets() {
      this.add(this.selectCategoryListBox = new ShopCreatorSelectCategoryListBoxWip(this, 2, 2));
   }

   public void alignWidgets() {
      this.selectCategoryListBox.posY = 8;
      this.selectCategoryListBox.posX = 4;
      this.selectCategoryListBox.setWidth(this.width - 8);
      this.selectCategoryListBox.height = this.height - 16;
      this.selectCategoryListBox.clearWidgets();
      this.selectCategoryListBox.addWidgets();
      this.selectCategoryListBox.alignWidgets();
   }

   public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
      ShopRenderingWrapper.beginBatch(w, h, 4.0F, 1.0F);
      ShopRenderingWrapper.addBatchRect(graphics, x, y, w, h, GUIShopMenu.BACKGROUND_INT, GUIShopMenu.BORDER_INT);
      ShopRenderingWrapper.endBatch();
   }
}
