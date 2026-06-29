package net.sixik.sdmshop.utils;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public class ShopInputHelper {
   public static final int INSERT_KEY = 90;
   public static final int SWAP_KEY = 88;

   public static boolean isControl() {
      return Screen.m_96637_();
   }

   public static boolean isShift() {
      return Screen.m_96638_();
   }

   public static boolean isMoveInsert() {
      return isControl() && isKeyDown(90);
   }

   public static boolean isMoveSwap() {
      return isControl() && isKeyDown(88);
   }

   public static boolean isKeyDown(int key) {
      return InputConstants.m_84830_(Minecraft.m_91087_().m_91268_().m_85439_(), key);
   }
}
