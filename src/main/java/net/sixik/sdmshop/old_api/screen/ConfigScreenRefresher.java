package net.sixik.sdmshop.old_api.screen;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.ui.ScreenWrapper;
import net.minecraft.client.Minecraft;

@Deprecated
public interface ConfigScreenRefresher {
   void refreshAndSafe(ConfigGroup var1);

   static void refreshIfOpened(ConfigGroup group) {
      if (Minecraft.m_91087_().f_91080_ instanceof ScreenWrapper w && w instanceof ConfigScreenRefresher refreshSupport) {
         refreshSupport.refreshAndSafe(group);
      }
   }
}
