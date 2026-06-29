package net.sixik.sdmshop.compat.kubejs.events;

import dev.latvian.mods.kubejs.player.PlayerEventJS;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.sixik.sdmshop.api.ShopBase;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.shop.ShopTab;

public class ShopEventEntrySellJS extends PlayerEventJS {
   private final ShopBase base;
   private final ShopEntry entry;
   private final ShopTab tab;
   private final ServerPlayer player;
   private final int count;

   public ShopEventEntrySellJS(ShopBase base, ShopEntry entry, ShopTab tab, ServerPlayer player, int count) {
      this.base = base;
      this.entry = entry;
      this.tab = tab;
      this.player = player;
      this.count = count;
   }

   public ShopEntry getEntry() {
      return this.entry;
   }

   public int getCount() {
      return this.count;
   }

   public ShopBase getShop() {
      return this.base;
   }

   public ShopTab getTab() {
      return this.tab;
   }

   public Player getEntity() {
      return this.player;
   }
}
