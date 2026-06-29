package net.sixik.sdmshop.old_api;

import net.minecraft.server.MinecraftServer;

public interface DataSaver {
   void save(MinecraftServer var1);

   void load(MinecraftServer var1);
}
