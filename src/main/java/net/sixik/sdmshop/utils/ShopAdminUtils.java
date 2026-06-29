package net.sixik.sdmshop.utils;

import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.HoverEvent.Action;
import net.minecraft.world.entity.player.Player;
import net.sixik.sdmshop.config.ShopConfig;

public final class ShopAdminUtils {
   public static final boolean SEND_MESSAGES = (Boolean)ShopConfig.SHOW_ADMIN_MESSAGES.get();
   private static final String MOD_NAME = "SDMShop";
   private static final MutableComponent PREFIX = Component.m_237113_("[")
      .m_130940_(ChatFormatting.DARK_GRAY)
      .m_7220_(Component.m_237113_("SDMShop").m_130944_(new ChatFormatting[]{ChatFormatting.GRAY, ChatFormatting.BOLD}))
      .m_7220_(Component.m_237113_("] ").m_130940_(ChatFormatting.DARK_GRAY));

   private ShopAdminUtils() {
   }

   public static void info(Player player, Component message) {
      send(player, ShopAdminUtils.Level.INFO, message, null);
   }

   public static void warn(Player player, Component message) {
      send(player, ShopAdminUtils.Level.WARN, message, null);
   }

   public static void error(Player player, Component message) {
      send(player, ShopAdminUtils.Level.ERROR, message, null);
   }

   public static void info(Player player, String fmt, Object... args) {
      send(player, ShopAdminUtils.Level.INFO, format(fmt, args), fmt);
   }

   public static void warn(Player player, String fmt, Object... args) {
      send(player, ShopAdminUtils.Level.WARN, format(fmt, args), fmt);
   }

   public static void error(Player player, String fmt, Object... args) {
      send(player, ShopAdminUtils.Level.ERROR, format(fmt, args), fmt);
   }

   private static MutableComponent format(String fmt, Object... args) {
      return Component.m_237113_(String.format(Locale.ROOT, fmt, args));
   }

   private static void send(Player player, ShopAdminUtils.Level level, Component message, String rawFmtForCopy) {
      if (player != null && message != null && SEND_MESSAGES) {
         MutableComponent line = PREFIX.m_6881_()
            .m_7220_(Component.m_237113_(level.icon).m_130940_(level.color))
            .m_7220_(message.m_6881_().m_130940_(ChatFormatting.WHITE));
         String copyText = message.getString();
         line = line.m_130948_(
            Style.f_131099_
               .m_131144_(
                  new HoverEvent(
                     Action.f_130831_,
                     Component.m_237113_("Click to copy\n")
                        .m_130940_(ChatFormatting.DARK_GRAY)
                        .m_7220_(Component.m_237113_("SDMShop admin log").m_130940_(ChatFormatting.GRAY))
                  )
               )
               .m_131142_(new ClickEvent(net.minecraft.network.chat.ClickEvent.Action.COPY_TO_CLIPBOARD, copyText))
         );
         player.m_213846_(line);
      }
   }

   public enum Level {
      INFO("ℹ ", ChatFormatting.AQUA),
      WARN("⚠ ", ChatFormatting.GOLD),
      ERROR("✖ ", ChatFormatting.RED);

      final String icon;
      final ChatFormatting color;

      Level(String icon, ChatFormatting color) {
         this.icon = icon;
         this.color = color;
      }
   }
}
