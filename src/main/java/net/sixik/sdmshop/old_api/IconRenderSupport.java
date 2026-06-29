package net.sixik.sdmshop.old_api;

import dev.ftb.mods.ftblibrary.icon.Icon;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IconRenderSupport {
   @OnlyIn(Dist.CLIENT)
   Icon getIcon();
}
