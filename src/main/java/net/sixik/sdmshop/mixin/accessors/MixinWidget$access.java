package net.sixik.sdmshop.mixin.accessors;

import dev.ftb.mods.ftblibrary.ui.Widget;
import dev.ftb.mods.ftblibrary.ui.Widget.DrawLayer;
import net.sixik.sdmshop.utils.mixin.WidgetPath;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = Widget.class, remap = false)
public abstract class MixinWidget$access implements WidgetPath {
   @Shadow
   abstract boolean shouldRenderInLayer(DrawLayer var1, int var2, int var3, int var4, int var5);

   @Override
   public boolean sdm$shouldRenderInLayer(DrawLayer layer, int x, int y, int w, int h) {
      return this.shouldRenderInLayer(layer, x, y, w, h);
   }
}
