package net.sixik.sdmshop.client.screen.base.buyer;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.input.MouseButton;
import net.minecraft.network.chat.Component;

public abstract class AbstractBuyerCancelButton extends SimpleTextButton {
   public AbstractBuyerCancelButton(Panel panel) {
      super(panel, Component.m_237115_("sdm.shop.ui.button.cancel"), Icon.empty());
   }

   public void onClicked(MouseButton mouseButton) {
      if (mouseButton.isLeft()) {
         AbstractBuyerScreen screen = this.getBuyerScreen();
         screen.shopScreen.onRefresh();
         screen.closeGui();
      }
   }

   public AbstractBuyerScreen getBuyerScreen() {
      return (AbstractBuyerScreen)this.getGui();
   }
}
