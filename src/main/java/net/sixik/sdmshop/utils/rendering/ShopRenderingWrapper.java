package net.sixik.sdmshop.utils.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;

public class ShopRenderingWrapper {
   public static void beginBatch(float width, float height, float cornerSize, float borderWidth) {
      ShopRenderingImpl.beginBatch(width, height, cornerSize, borderWidth);
   }

   public static void addBatchRect(GuiGraphics graphics, float x, float y, float width, float height, int fillColor, int borderColor) {
      addBatchRect(graphics.m_280168_(), x, y, width, height, fillColor, borderColor);
   }

   public static void addBatchRect(PoseStack poseStack, float x, float y, float width, float height, int fillColor, int borderColor) {
      ShopRenderingImpl.addBatchRect(poseStack, x, y, width, height, fillColor, borderColor);
   }

   public static void endBatch() {
      ShopRenderingImpl.endBatch();
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
      ShopRenderingImpl.drawDirectionalGradientRect(poseStack, x, y, width, height, radius, borderWidth, colorStart, colorEnd, borderColor, angleDeg);
   }

   public static void drawRoundedRect(
      PoseStack poseStack, float x, float y, float width, float height, float radius, float borderWidth, int color, int borderColor
   ) {
      ShopRenderingImpl.drawRoundedRect(poseStack, x, y, width, height, radius, borderWidth, color, borderColor);
   }

   public static void drawRoundedGradientRect(
      PoseStack poseStack, float x, float y, float width, float height, float radius, float borderWidth, int colorTop, int colorBottom, int borderColor
   ) {
      ShopRenderingImpl.drawRoundedCornerRect(poseStack, x, y, width, height, radius, borderWidth, colorTop, colorTop, colorBottom, colorBottom, borderColor);
   }

   public static void drawRoundedCornerRect(
      PoseStack poseStack, float x, float y, float width, float height, float radius, float borderWidth, int cTL, int cTR, int cBL, int cBR, int borderColor
   ) {
      ShopRenderingImpl.drawRoundedCornerRect(poseStack, x, y, width, height, radius, borderWidth, cTL, cTR, cBL, cBR, borderColor);
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
      ShopRenderingImpl.drawRoundedCornerRectNoBorder(poseStack, x, y, width, height, radius, cTL, cTR, cBL, cBR);
   }

   public static void drawDirectionalGradientRectNoBorder(
      PoseStack poseStack, float x, float y, float width, float height, float radius, int colorStart, int colorEnd, float angleDeg
   ) {
      ShopRenderingImpl.drawDirectionalGradientRectNoBorder(poseStack, x, y, width, height, radius, colorStart, colorEnd, angleDeg);
   }
}
