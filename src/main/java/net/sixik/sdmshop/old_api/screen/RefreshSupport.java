package net.sixik.sdmshop.old_api.screen;

import dev.ftb.mods.ftblibrary.ui.ScreenWrapper;
import java.util.Objects;
import net.minecraft.client.Minecraft;

public interface RefreshSupport {
   void onRefresh();

   static void refreshIfOpened() {
      if (Minecraft.m_91087_().f_91080_ instanceof ScreenWrapper w && w instanceof RefreshSupport refreshSupport) {
         refreshSupport.onRefresh();
      }
   }

   static void refreshIfOpened(Class<? extends RefreshSupport> ref) {
      if (Minecraft.m_91087_().f_91080_ instanceof ScreenWrapper w && w instanceof RefreshSupport refreshSupport && Objects.equals(w.getGui().getClass(), ref)) {
         refreshSupport.onRefresh();
      }
   }
}
