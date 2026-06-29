package net.sixik.sdmshop.utils.rendering;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class ShopRenderingComponents {
   public static final ResourceLocation BLOCKY_BORDER_SHADER_ID = new ResourceLocation("sdmshop", "blocky_border");
   public static final ResourceLocation BLOCKY_BORDER_BATCHED_SHADER_ID = new ResourceLocation("sdmshop", "blocky_border_batched");
   public static final ResourceLocation ROUNDED_BORDER_SHADER_ID = new ResourceLocation("sdmshop", "rounded_border");
   @Nullable
   public static ShaderInstance BLOCKY_BORDER_SHADER;
   @Nullable
   public static ShaderInstance BLOCKY_BORDER_BATCHED_SHADER;
   @Nullable
   public static ShaderInstance ROUNDED_BORDER_SHADER;
   public static final VertexFormat BATCH_FORMAT = new VertexFormat(
      ImmutableMap.builder()
         .put("Position", DefaultVertexFormat.f_85804_)
         .put("Color", DefaultVertexFormat.f_85805_)
         .put("UV0", DefaultVertexFormat.f_85806_)
         .put("UV1", DefaultVertexFormat.f_85807_)
         .put("UV2", DefaultVertexFormat.f_85808_)
         .put("Padding", DefaultVertexFormat.f_85810_)
         .build()
   );

   public static boolean isShaderLoaded() {
      return BLOCKY_BORDER_SHADER != null && BLOCKY_BORDER_BATCHED_SHADER != null && ROUNDED_BORDER_SHADER != null;
   }
}
