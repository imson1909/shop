package net.sixik.sdmshop.client.screen.base.widgets;

import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.TextBox;
import net.sixik.sdmshop.client.screen.base.AbstractShopScreen;

public class AbstractShopEntrySearch extends TextBox {
   public AbstractShopEntrySearch(Panel panel) {
      super(panel);
   }

   public void onTextChanged() {
      this.getShopScreen().searchField = this.getText();
   }

   public void onEnterPressed() {
      this.getShopScreen().searchField = this.getText();
      this.getShopScreen().onRefresh();
   }

   public AbstractShopScreen getShopScreen() {
      return (AbstractShopScreen)this.getGui();
   }
}
