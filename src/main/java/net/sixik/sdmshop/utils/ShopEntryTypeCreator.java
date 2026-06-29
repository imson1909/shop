package net.sixik.sdmshop.utils;

import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.ContextMenuItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.function.Function;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.sixik.sdmshop.client.SDMShopClient;
import net.sixik.sdmshop.client.screen.base.AbstractShopScreen;
import net.sixik.sdmshop.old_api.shop.AbstractEntryType;
import net.sixik.sdmshop.registers.ShopContentRegister;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.shop.entry_types.MissingEntryType;
import net.sixik.sdmshop.shop.seller_types.MoneySellerType;

public class ShopEntryTypeCreator {
   public static Optional<AbstractEntryType> createEntryType(ShopEntry entry, CompoundTag nbt) {
      if (!nbt.m_128441_("type_id")) {
         return Optional.empty();
      }

      String type_id = nbt.m_128461_("type_id");

      try {
         Optional<Function<ShopEntry, AbstractEntryType>> opt = ShopContentRegister.getEntryType(type_id);
         if (opt.isEmpty()) {
            return Optional.of(new MissingEntryType(entry, nbt));
         }

         AbstractEntryType result = opt.get().apply(entry);
         result.deserialize(nbt);
         return Optional.of(result);
      } catch (NoClassDefFoundError e) {
         return Optional.of(new MissingEntryType(entry, nbt));
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static List<ContextMenuItem> createContext(AbstractShopScreen screen) {
      List<ContextMenuItem> contextMenu = new ArrayList<>();

      for (Entry<String, Function<ShopEntry, AbstractEntryType>> entry : ShopContentRegister.getEntryTypes().entrySet()) {
         ShopEntry shopEntry = new ShopEntry(screen.currentShop, UUID.randomUUID(), screen.getCurrentTabUuid(), new MoneySellerType());
         AbstractEntryType entryType = entry.getValue().apply(shopEntry);
         if (entryType.isModLoaded() && SDMShopClient.userData.getCreator().contains(entry.getKey())) {
            contextMenu.add(
               new ContextMenuItem(
                  entryType.getTranslatableForCreativeMenu(), entryType.getCreativeIcon(), button -> ShopUtilsClient.addEntry(screen.currentShop, shopEntry)
               )
            );
         }
      }

      contextMenu.add(new ContextMenuItem(Component.m_237115_("sdm.shop.entry.creator.contextmenu.info"), Icons.BOOK, button -> screen.openCreateEntryScreen()));
      return contextMenu;
   }
}
