package net.sixik.sdmshop.client.screen_new.api;

import dev.ftb.mods.ftblibrary.ui.ModalPanel;
import dev.ftb.mods.ftblibrary.ui.Widget;
import org.jetbrains.annotations.Nullable;

public interface ShopScreenEvents {
   @FunctionalInterface
   interface OnClickElement {
      void handle(@Nullable Widget var1);
   }

   @FunctionalInterface
   interface OnFilterChanged {
      void handle();
   }

   @FunctionalInterface
   interface OnModalClose {
      void handle(ModalPanel var1);
   }

   @FunctionalInterface
   interface OnModalOpen {
      void handle(ModalPanel var1);
   }
}
