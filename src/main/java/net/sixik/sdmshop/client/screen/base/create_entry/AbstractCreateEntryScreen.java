package net.sixik.sdmshop.client.screen.base.create_entry;

import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.BaseScreen;
import dev.ftb.mods.ftblibrary.ui.ContextMenu;
import dev.ftb.mods.ftblibrary.ui.ContextMenuItem;
import dev.ftb.mods.ftblibrary.ui.GuiHelper;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.PanelScrollBar;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import dev.ftb.mods.ftblibrary.ui.misc.NordColors;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.sixik.sdmshop.client.screen.base.AbstractShopScreen;
import net.sixik.sdmshop.old_api.screen.RefreshSupport;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractCreateEntryScreen extends BaseScreen implements RefreshSupport {
   public static final int DEFAULT_BUTTON_SIZE = 50;
   protected boolean showNotLoadedContent = false;
   protected AbstractCreateEntryPanel entriesPanel;
   protected PanelScrollBar entriesScrollPanel;
   protected AbstractCreateEntryScreen.AbstractShowOnlyLoadedButton shopOnlyLoadedButton;
   protected AbstractCreateEntryScreen.AbstractBackButton backToShopButton;
   public final AbstractShopScreen shopScreen;

   public AbstractCreateEntryScreen(AbstractShopScreen shopScreen) {
      this.shopScreen = shopScreen;
   }

   public ContextMenu openContextMenu(@NotNull List<ContextMenuItem> menu) {
      ContextMenu contextMenu = new ContextMenu(this, menu) {
         public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
            NordColors.POLAR_NIGHT_3.draw(graphics, x + 1, y + 1, w - 2, h - 2);
            GuiHelper.drawHollowRect(graphics, x, y, w, h, Color4I.BLACK, false);
         }
      };
      this.openContextMenu(contextMenu);
      return contextMenu;
   }

   public static class AbstractBackButton extends SimpleTextButton {
      public AbstractBackButton(Panel panel) {
         super(panel, Component.m_237115_("sdm.shop.entry.creator.back"), Icons.BACK);
      }

      public void onClicked(MouseButton mouseButton) {
         if (mouseButton.isLeft()) {
            this.getGui().closeGui();
         }
      }
   }

   public static class AbstractShowOnlyLoadedButton extends SimpleTextButton {
      public AbstractShowOnlyLoadedButton(Panel panel) {
         super(panel, Component.m_237115_("sdm.shop.entry.creator.info"), Icons.BOOK);
      }

      public void drawIcon(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
         AbstractCreateEntryScreen screen = (AbstractCreateEntryScreen)this.getGui();
         if (screen.showNotLoadedContent) {
            this.icon = Icons.CHECK;
         } else {
            this.icon = Icons.CLOSE;
         }

         super.drawIcon(graphics, theme, x, y, w, h);
      }

      public void onClicked(MouseButton mouseButton) {
         if (mouseButton.isLeft()) {
            AbstractCreateEntryScreen screen = (AbstractCreateEntryScreen)this.getGui();
            screen.showNotLoadedContent = !screen.showNotLoadedContent;
            screen.refreshWidgets();
            screen.onRefresh();
         }
      }
   }
}
