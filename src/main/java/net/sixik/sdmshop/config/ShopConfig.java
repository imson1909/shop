package net.sixik.sdmshop.config;

import dev.ftb.mods.ftblibrary.config.NameMap;
import dev.ftb.mods.ftblibrary.snbt.config.BooleanValue;
import dev.ftb.mods.ftblibrary.snbt.config.ConfigUtil;
import dev.ftb.mods.ftblibrary.snbt.config.EnumValue;
import dev.ftb.mods.ftblibrary.snbt.config.SNBTConfig;
import dev.ftb.mods.ftblibrary.snbt.config.StringValue;
import net.sixik.sdmshop.SDMShopPaths;

public class ShopConfig {
   public static final SNBTConfig CONFIG = SNBTConfig.create("sdmshop-common");
   public static final BooleanValue DISABLE_KEYBIND;
   public static final BooleanValue SEND_NOTIFY;
   public static final StringValue DEFAULT_SHOP_ID;
   public static final BooleanValue SHOW_ADMIN_MESSAGES;
   public static final BooleanValue USE_CACHED_SHOP_DATA;
   public static final EnumValue<ShopConfig.UIStyle> GUI_STYLE;

   public static void reload() {
      CONFIG.load(SDMShopPaths.getModConfig());
   }

   public static void loadConfig() {
      ConfigUtil.loadDefaulted(CONFIG, SDMShopPaths.getModFolder(), "sdmshop");
   }

   static {
      SNBTConfig group = CONFIG.addGroup("server");
      DISABLE_KEYBIND = (BooleanValue)group.addBoolean("disable_key_bind", false)
         .comment(
            new String[]{
               "Determines if the client is allowed to request opening a shop (e.g., via keybindings). If enabled, shops can only be opened by the server using the /sdmshop open_shop <player> <shopId> command. Useful for custom shop implementations."
            }
         );
      SEND_NOTIFY = (BooleanValue)group.addBoolean("send_notify", true)
         .comment(new String[]{"Whether to display a notification in chat about purchasing an item."});
      DEFAULT_SHOP_ID = (StringValue)group.addString("default_shop_id", "default")
         .comment(
            new String[]{
               "The store ID that you specify when creating via /sdmshop create_shop <id>. After specifying the ID, clicking on the store button or the button in the menu will open the current store."
            }
         );
      SHOW_ADMIN_MESSAGES = (BooleanValue)group.addBoolean("show_admin_messages", true)
         .comment(new String[]{"Debugging messages when editing, purchasing, etc. It is recommended to enable them when editing."});
      group = CONFIG.addGroup("client");
      GUI_STYLE = group.addEnum("ui_style", NameMap.of(ShopConfig.UIStyle.BlockyModern, ShopConfig.UIStyle.values()).create());
      group = group.addGroup("caching");
      USE_CACHED_SHOP_DATA = (BooleanValue)group.addBoolean("use_cached_shop_data", true)
         .comment(
            new String[]{
               "Allows you to reduce the load on the network by caching store data on the player's client. In this case, the player will be able to copy your store data without any obstacles."
            }
         );
   }

   public enum UIStyle {
      Modern,
      BlockyModern;
   }
}
