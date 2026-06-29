package net.sixik.sdmshop.utils;

import com.google.common.hash.Hashing;
import java.io.ByteArrayOutputStream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.sixik.sdmshop.SDMShop;

public class HashUtils {
   public static String calculateHash(CompoundTag tags) {
      try {
         ByteArrayOutputStream btc = new ByteArrayOutputStream();
         NbtIo.m_128947_(tags, btc);
         byte[] data = btc.toByteArray();
         return Hashing.sha256().hashBytes(data).toString();
      } catch (Exception e) {
         SDMShop.LOGGER.error("Error calculate hash", e);
         return "error_hash";
      }
   }
}
