package net.sixik.sdmshop.utils.rendering;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.renderer.ShaderInstance;
import org.joml.Matrix4f;

class ShopRenderingImpl {
   public static void beginBatch(float width, float height, float cornerSize, float borderWidth) {
      ShaderInstance shader = ShopRenderingComponents.BLOCKY_BORDER_BATCHED_SHADER;
      if (shader != null) {
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableDepthTest();
         RenderSystem.depthMask(false);
         RenderSystem.setShader(() -> shader);
         safeSetUniform(shader, "u_Size", width, height);
         safeSetUniform(shader, "u_CornerSize", cornerSize);
         safeSetUniform(shader, "u_BorderWidth", borderWidth);
         Tesselator.m_85913_().m_85915_().m_166779_(Mode.QUADS, ShopRenderingComponents.BATCH_FORMAT);
      }
   }

   public static void addBatchRect(PoseStack poseStack, float x, float y, float width, float height, int fillColor, int borderColor) {
      Matrix4f matrix = poseStack.m_85850_().m_252922_();
      BufferBuilder buffer = Tesselator.m_85913_().m_85915_();
      int fA = fillColor >> 24 & 0xFF;
      if (fA == 0) {
         fA = 255;
      }

      int fR = fillColor >> 16 & 0xFF;
      int fG = fillColor >> 8 & 0xFF;
      int fB = fillColor & 0xFF;
      int bA = borderColor >> 24 & 0xFF;
      if (bA == 0) {
         bA = 255;
      }

      int bR = borderColor >> 16 & 0xFF;
      int bG = borderColor >> 8 & 0xFF;
      int bB = borderColor & 0xFF;
      writeVertex(buffer, matrix, x, y + height, fR, fG, fB, fA, 0.0F, 1.0F, bR, bG, bB, bA);
      writeVertex(buffer, matrix, x + width, y + height, fR, fG, fB, fA, 1.0F, 1.0F, bR, bG, bB, bA);
      writeVertex(buffer, matrix, x + width, y, fR, fG, fB, fA, 1.0F, 0.0F, bR, bG, bB, bA);
      writeVertex(buffer, matrix, x, y, fR, fG, fB, fA, 0.0F, 0.0F, bR, bG, bB, bA);
   }

   private static void writeVertex(
      BufferBuilder buf, Matrix4f mat, float x, float y, int r, int g, int b, int a, float u, float v, int bR, int bG, int bB, int bA
   ) {
      buf.m_252986_(mat, x, y, 0.0F);
      buf.m_6122_(r, g, b, a);
      buf.m_7421_(u, v);
      buf.m_7122_(bR, bG);
      buf.m_7120_(bB, bA);
      buf.m_5752_();
   }

   public static void endBatch() {
      Tesselator.m_85913_().m_85914_();
      RenderSystem.depthMask(true);
      RenderSystem.enableDepthTest();
      RenderSystem.disableBlend();
   }

   public static void drawBlockyRect(
      PoseStack poseStack, float x, float y, float width, float height, float cornerSize, float borderWidth, int fillColor, int borderColor
   ) {
      ShaderInstance shader = ShopRenderingComponents.BLOCKY_BORDER_SHADER;
      if (shader != null) {
         Matrix4f matrix = poseStack.m_85850_().m_252922_();
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableDepthTest();
         RenderSystem.depthMask(false);
         RenderSystem.setShader(() -> shader);
         if ((fillColor & 0xFF000000) == 0 && fillColor != 0) {
            fillColor |= -16777216;
         }

         if ((borderColor & 0xFF000000) == 0 && borderColor != 0) {
            borderColor |= -16777216;
         }

         safeSetUniform(shader, "u_Size", width, height);
         safeSetUniform(shader, "u_CornerSize", cornerSize);
         safeSetUniform(shader, "u_BorderWidth", borderWidth);
         safeSetColorUniform(shader, "u_FillColor", fillColor);
         safeSetColorUniform(shader, "u_BorderColor", borderColor);
         Tesselator tesselator = Tesselator.m_85913_();
         BufferBuilder buffer = tesselator.m_85915_();
         buffer.m_166779_(Mode.QUADS, DefaultVertexFormat.f_85817_);
         buffer.m_252986_(matrix, x, y + height, 0.0F).m_7421_(0.0F, 1.0F).m_5752_();
         buffer.m_252986_(matrix, x + width, y + height, 0.0F).m_7421_(1.0F, 1.0F).m_5752_();
         buffer.m_252986_(matrix, x + width, y, 0.0F).m_7421_(1.0F, 0.0F).m_5752_();
         buffer.m_252986_(matrix, x, y, 0.0F).m_7421_(0.0F, 0.0F).m_5752_();
         tesselator.m_85914_();
         RenderSystem.depthMask(true);
         RenderSystem.enableDepthTest();
         RenderSystem.disableBlend();
      }
   }

   public static void drawRoundedRect(
      PoseStack poseStack, float x, float y, float width, float height, float radius, float borderWidth, int color, int borderColor
   ) {
      drawRoundedCornerRect(poseStack, x, y, width, height, radius, borderWidth, color, color, color, color, borderColor);
   }

   public static void drawRoundedGradientRect(
      PoseStack poseStack, float x, float y, float width, float height, float radius, float borderWidth, int colorTop, int colorBottom, int borderColor
   ) {
      drawRoundedCornerRect(poseStack, x, y, width, height, radius, borderWidth, colorTop, colorTop, colorBottom, colorBottom, borderColor);
   }

   public static void drawRoundedCornerRect(
      PoseStack poseStack, float x, float y, float width, float height, float radius, float borderWidth, int cTL, int cTR, int cBL, int cBR, int borderColor
   ) {
      ShaderInstance shader = ShopRenderingComponents.ROUNDED_BORDER_SHADER;
      if (shader != null) {
         Matrix4f matrix = poseStack.m_85850_().m_252922_();
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableDepthTest();
         RenderSystem.depthMask(false);
         RenderSystem.setShader(() -> shader);
         if ((cTL & 0xFF000000) == 0) {
            cTL |= -16777216;
         }

         if ((cTR & 0xFF000000) == 0) {
            cTR |= -16777216;
         }

         if ((cBL & 0xFF000000) == 0) {
            cBL |= -16777216;
         }

         if ((cBR & 0xFF000000) == 0) {
            cBR |= -16777216;
         }

         if ((borderColor & 0xFF000000) == 0) {
            borderColor |= -16777216;
         }

         safeSetBoolUniform(shader, "u_UseBorder", true);
         safeSetUniform(shader, "u_Size", width, height);
         safeSetUniform(shader, "u_Radius", radius);
         safeSetUniform(shader, "u_BorderWidth", borderWidth);
         safeSetColorUniform(shader, "u_BorderColor", borderColor);
         safeSetColorUniform(shader, "u_ColorTL", cTL);
         safeSetColorUniform(shader, "u_ColorTR", cTR);
         safeSetColorUniform(shader, "u_ColorBL", cBL);
         safeSetColorUniform(shader, "u_ColorBR", cBR);
         Tesselator tesselator = Tesselator.m_85913_();
         BufferBuilder buffer = tesselator.m_85915_();
         buffer.m_166779_(Mode.QUADS, DefaultVertexFormat.f_85817_);
         buffer.m_252986_(matrix, x, y + height, 0.0F).m_7421_(0.0F, 1.0F).m_5752_();
         buffer.m_252986_(matrix, x + width, y + height, 0.0F).m_7421_(1.0F, 1.0F).m_5752_();
         buffer.m_252986_(matrix, x + width, y, 0.0F).m_7421_(1.0F, 0.0F).m_5752_();
         buffer.m_252986_(matrix, x, y, 0.0F).m_7421_(0.0F, 0.0F).m_5752_();
         tesselator.m_85914_();
         RenderSystem.depthMask(true);
         RenderSystem.enableDepthTest();
         RenderSystem.disableBlend();
      }
   }

   public static void drawDirectionalGradientRect(
      PoseStack poseStack,
      float x,
      float y,
      float width,
      float height,
      float radius,
      float borderWidth,
      int colorStart,
      int colorEnd,
      int borderColor,
      float angleDeg
   ) {
      ShaderInstance shader = ShopRenderingComponents.ROUNDED_BORDER_SHADER;
      if (shader != null) {
         Matrix4f matrix = poseStack.m_85850_().m_252922_();
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableDepthTest();
         RenderSystem.depthMask(false);
         RenderSystem.setShader(() -> shader);
         if ((colorStart & 0xFF000000) == 0) {
            colorStart |= -16777216;
         }

         if ((colorEnd & 0xFF000000) == 0) {
            colorEnd |= -16777216;
         }

         if ((borderColor & 0xFF000000) == 0) {
            borderColor |= -16777216;
         }

         safeSetUniform(shader, "u_Size", width, height);
         safeSetUniform(shader, "u_Radius", radius);
         safeSetUniform(shader, "u_BorderWidth", borderWidth);
         safeSetColorUniform(shader, "u_BorderColor", borderColor);
         safeSetIntUniform(shader, "u_GradientType", 1);
         safeSetUniform(shader, "u_Angle", (float)Math.toRadians(angleDeg));
         safeSetColorUniform(shader, "u_ColorTL", colorStart);
         safeSetColorUniform(shader, "u_ColorBR", colorEnd);
         Tesselator tesselator = Tesselator.m_85913_();
         BufferBuilder buffer = tesselator.m_85915_();
         buffer.m_166779_(Mode.QUADS, DefaultVertexFormat.f_85817_);
         buffer.m_252986_(matrix, x, y + height, 0.0F).m_7421_(0.0F, 1.0F).m_5752_();
         buffer.m_252986_(matrix, x + width, y + height, 0.0F).m_7421_(1.0F, 1.0F).m_5752_();
         buffer.m_252986_(matrix, x + width, y, 0.0F).m_7421_(1.0F, 0.0F).m_5752_();
         buffer.m_252986_(matrix, x, y, 0.0F).m_7421_(0.0F, 0.0F).m_5752_();
         tesselator.m_85914_();
         safeSetIntUniform(shader, "u_GradientType", 0);
         RenderSystem.depthMask(true);
         RenderSystem.enableDepthTest();
         RenderSystem.disableBlend();
      }
   }

   public static void drawRoundedRectNoBorder(PoseStack poseStack, float x, float y, float width, float height, float radius, int color) {
      drawRoundedCornerRectNoBorder(poseStack, x, y, width, height, radius, color, color, color, color);
   }

   public static void drawRoundedGradientRectNoBorder(
      PoseStack poseStack, float x, float y, float width, float height, float radius, int colorTop, int colorBottom
   ) {
      drawRoundedCornerRectNoBorder(poseStack, x, y, width, height, radius, colorTop, colorTop, colorBottom, colorBottom);
   }

   public static void drawRoundedCornerRectNoBorder(
      PoseStack poseStack, float x, float y, float width, float height, float radius, int cTL, int cTR, int cBL, int cBR
   ) {
      ShaderInstance shader = ShopRenderingComponents.ROUNDED_BORDER_SHADER;
      if (shader != null) {
         Matrix4f matrix = poseStack.m_85850_().m_252922_();
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableDepthTest();
         RenderSystem.depthMask(false);
         RenderSystem.setShader(() -> shader);
         if ((cTL & 0xFF000000) == 0) {
            cTL |= -16777216;
         }

         if ((cTR & 0xFF000000) == 0) {
            cTR |= -16777216;
         }

         if ((cBL & 0xFF000000) == 0) {
            cBL |= -16777216;
         }

         if ((cBR & 0xFF000000) == 0) {
            cBR |= -16777216;
         }

         safeSetUniform(shader, "u_Size", width, height);
         safeSetUniform(shader, "u_Radius", radius);
         safeSetBoolUniform(shader, "u_UseBorder", false);
         safeSetUniform(shader, "u_BorderWidth", 0.0F);
         safeSetColorUniform(shader, "u_ColorTL", cTL);
         safeSetColorUniform(shader, "u_ColorTR", cTR);
         safeSetColorUniform(shader, "u_ColorBL", cBL);
         safeSetColorUniform(shader, "u_ColorBR", cBR);
         safeSetIntUniform(shader, "u_GradientType", 0);
         safeSetUniform(shader, "u_Angle", 0.0F);
         Tesselator tesselator = Tesselator.m_85913_();
         BufferBuilder buffer = tesselator.m_85915_();
         buffer.m_166779_(Mode.QUADS, DefaultVertexFormat.f_85817_);
         buffer.m_252986_(matrix, x, y + height, 0.0F).m_7421_(0.0F, 1.0F).m_5752_();
         buffer.m_252986_(matrix, x + width, y + height, 0.0F).m_7421_(1.0F, 1.0F).m_5752_();
         buffer.m_252986_(matrix, x + width, y, 0.0F).m_7421_(1.0F, 0.0F).m_5752_();
         buffer.m_252986_(matrix, x, y, 0.0F).m_7421_(0.0F, 0.0F).m_5752_();
         tesselator.m_85914_();
         RenderSystem.depthMask(true);
         RenderSystem.enableDepthTest();
         RenderSystem.disableBlend();
      }
   }

   public static void drawDirectionalGradientRectNoBorder(
      PoseStack poseStack, float x, float y, float width, float height, float radius, int colorStart, int colorEnd, float angleDeg
   ) {
      ShaderInstance shader = ShopRenderingComponents.ROUNDED_BORDER_SHADER;
      if (shader != null) {
         Matrix4f matrix = poseStack.m_85850_().m_252922_();
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableDepthTest();
         RenderSystem.depthMask(false);
         RenderSystem.setShader(() -> shader);
         if ((colorStart & 0xFF000000) == 0) {
            colorStart |= -16777216;
         }

         if ((colorEnd & 0xFF000000) == 0) {
            colorEnd |= -16777216;
         }

         safeSetUniform(shader, "u_Size", width, height);
         safeSetUniform(shader, "u_Radius", radius);
         safeSetBoolUniform(shader, "u_UseBorder", false);
         safeSetUniform(shader, "u_BorderWidth", 0.0F);
         safeSetIntUniform(shader, "u_GradientType", 1);
         safeSetUniform(shader, "u_Angle", (float)Math.toRadians(angleDeg));
         safeSetColorUniform(shader, "u_ColorTL", colorStart);
         safeSetColorUniform(shader, "u_ColorBR", colorEnd);
         Tesselator tesselator = Tesselator.m_85913_();
         BufferBuilder buffer = tesselator.m_85915_();
         buffer.m_166779_(Mode.QUADS, DefaultVertexFormat.f_85817_);
         buffer.m_252986_(matrix, x, y + height, 0.0F).m_7421_(0.0F, 1.0F).m_5752_();
         buffer.m_252986_(matrix, x + width, y + height, 0.0F).m_7421_(1.0F, 1.0F).m_5752_();
         buffer.m_252986_(matrix, x + width, y, 0.0F).m_7421_(1.0F, 0.0F).m_5752_();
         buffer.m_252986_(matrix, x, y, 0.0F).m_7421_(0.0F, 0.0F).m_5752_();
         tesselator.m_85914_();
         safeSetIntUniform(shader, "u_GradientType", 0);
         RenderSystem.depthMask(true);
         RenderSystem.enableDepthTest();
         RenderSystem.disableBlend();
      }
   }

   private static void safeSetUniform(ShaderInstance shaderInstance, String name, float... values) {
      Uniform uniform = shaderInstance.m_173348_(name);
      if (uniform != null) {
         uniform.m_5941_(values);
      }
   }

   private static void safeSetIntUniform(ShaderInstance shaderInstance, String name, int value) {
      Uniform uniform = shaderInstance.m_173348_(name);
      if (uniform != null) {
         uniform.m_142617_(value);
      }
   }

   private static void safeSetColorUniform(ShaderInstance shaderInstance, String name, int color) {
      Uniform uniform = shaderInstance.m_173348_(name);
      if (uniform != null) {
         float a = (color >> 24 & 0xFF) / 255.0F;
         float r = (color >> 16 & 0xFF) / 255.0F;
         float g = (color >> 8 & 0xFF) / 255.0F;
         float b = (color & 0xFF) / 255.0F;
         uniform.m_5805_(r, g, b, a);
      }
   }

   private static void safeSetBoolUniform(ShaderInstance shader, String name, boolean v) {
      safeSetIntUniform(shader, name, v ? 1 : 0);
   }
}
