package net.sixik.sdmshop.network;

import dev.architectury.networking.simple.MessageType;
import dev.architectury.networking.simple.SimpleNetworkManager;
import net.sixik.sdmshop.network.economy.ShopChangeMoneyC2S;
import net.sixik.sdmshop.network.server.ChangeEditModeC2S;
import net.sixik.sdmshop.network.server.SendBuyEntryC2S;
import net.sixik.sdmshop.network.sync.SendAddEntryS2C;
import net.sixik.sdmshop.network.sync.SendAddTabS2C;
import net.sixik.sdmshop.network.sync.SendChangeEntryS2C;
import net.sixik.sdmshop.network.sync.SendChangeShopParamsS2C;
import net.sixik.sdmshop.network.sync.SendChangeTabS2C;
import net.sixik.sdmshop.network.sync.SendLimiterS2C;
import net.sixik.sdmshop.network.sync.SendMoveEntryS2C;
import net.sixik.sdmshop.network.sync.SendMoveTabS2C;
import net.sixik.sdmshop.network.sync.SendRemoveEntryS2C;
import net.sixik.sdmshop.network.sync.SendRemoveTabS2C;
import net.sixik.sdmshop.network.sync.server.SendAddEntryC2S;
import net.sixik.sdmshop.network.sync.server.SendAddTabC2S;
import net.sixik.sdmshop.network.sync.server.SendChangeEntryC2S;
import net.sixik.sdmshop.network.sync.server.SendChangeShopParamsC2S;
import net.sixik.sdmshop.network.sync.server.SendChangeTabC2S;
import net.sixik.sdmshop.network.sync.server.SendMoveEntryC2S;
import net.sixik.sdmshop.network.sync.server.SendMoveTabC2S;
import net.sixik.sdmshop.network.sync.server.SendRemoveEntryC2S;
import net.sixik.sdmshop.network.sync.server.SendRemoveTabC2S;
import net.sixik.sdmshop.network.sync.server.SendResetLimiterC2S;

public class SDMShopNetwork {
   private static final SimpleNetworkManager NET = SimpleNetworkManager.create("sdmshop");
   public static final MessageType CHANGE_EDIT_MODE_C2S = NET.registerC2S("change_edit_mode", ChangeEditModeC2S::new);
   public static final MessageType SHOP_CHANGE_MONEY = NET.registerC2S("shop_change_money", ShopChangeMoneyC2S::new);
   public static final MessageType SHOP_ADD_ENTRY_C2S = NET.registerC2S("shop_add_entry_c2s", SendAddEntryC2S::new);
   public static final MessageType SHOP_ADD_ENTRY_S2C = NET.registerS2C("shop_add_entry_s2c", SendAddEntryS2C::new);
   public static final MessageType SHOP_REMOVE_ENTRY_C2S = NET.registerC2S("shop_remove_entry_c2s", SendRemoveEntryC2S::new);
   public static final MessageType SHOP_REMOVE_ENTRY_S2C = NET.registerS2C("shop_remove_entry_s2c", SendRemoveEntryS2C::new);
   public static final MessageType SHOP_CHANGE_ENTRY_C2S = NET.registerC2S("shop_change_entry_c2s", SendChangeEntryC2S::new);
   public static final MessageType SHOP_CHANGE_ENTRY_S2C = NET.registerS2C("shop_change_entry_s2c", SendChangeEntryS2C::new);
   public static final MessageType SHOP_ADD_TAB_C2S = NET.registerC2S("shop_add_tab_c2s", SendAddTabC2S::new);
   public static final MessageType SHOP_ADD_TAB_S2C = NET.registerS2C("shop_add_tab_s2c", SendAddTabS2C::new);
   public static final MessageType SHOP_REMOVE_TAB_C2S = NET.registerC2S("shop_remove_tab_c2s", SendRemoveTabC2S::new);
   public static final MessageType SHOP_REMOVE_TAB_S2C = NET.registerS2C("shop_remove_tab_s2c", SendRemoveTabS2C::new);
   public static final MessageType SHOP_CHANGE_TAB_C2S = NET.registerC2S("shop_change_tab_c2s", SendChangeTabC2S::new);
   public static final MessageType SHOP_CHANGE_TAB_S2C = NET.registerS2C("shop_change_tab_s2c", SendChangeTabS2C::new);
   public static final MessageType SHOP_MOVE_ENTRY_C2S = NET.registerC2S("shop_move_entry_c2s", SendMoveEntryC2S::new);
   public static final MessageType SHOP_MOVE_ENTRY_S2C = NET.registerS2C("shop_move_entry_s2c", SendMoveEntryS2C::new);
   public static final MessageType SHOP_MOVE_TAB_C2S = NET.registerC2S("shop_move_tab_c2s", SendMoveTabC2S::new);
   public static final MessageType SHOP_MOVE_TAB_S2C = NET.registerS2C("shop_move_tab_s2c", SendMoveTabS2C::new);
   public static final MessageType SHOP_BUY_ENTRY = NET.registerC2S("shop_buy_entry_cs2", SendBuyEntryC2S::new);
   public static final MessageType SEND_LIMITER = NET.registerS2C("send_limiter", SendLimiterS2C::new);
   public static final MessageType CHANGE_PARAMS_C2S = NET.registerC2S("shop_change_params_c2s", SendChangeShopParamsC2S::new);
   public static final MessageType CHANGE_PARAMS_S2C = NET.registerS2C("shop_change_params_s2c", SendChangeShopParamsS2C::new);
   public static final MessageType RESET_LIMITER_C2S = NET.registerC2S("shop_reset_limiter_c2s", SendResetLimiterC2S::new);

   public static void init() {
   }
}
