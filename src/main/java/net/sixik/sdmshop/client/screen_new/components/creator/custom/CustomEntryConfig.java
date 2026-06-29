package net.sixik.sdmshop.client.screen_new.components.creator.custom;

import java.util.function.BooleanSupplier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.sixik.sdmshop.client.screen_new.components.creator.data.ShopCreatorComponentData;
import net.sixik.sdmshop.client.screen_new.components.creator.entry.ShopCreatorEntryPanel;
import net.sixik.sdmshop.client.screen_new.components.creator.entry.ShopCreatorEntryTypesPanel;
import net.sixik.sdmshop.shop.ShopEntry;

public abstract class CustomEntryConfig {
   @OnlyIn(Dist.CLIENT)
   public abstract void addWidgets(ShopCreatorEntryPanel var1, BooleanSupplier var2, ShopCreatorComponentData var3);

   @OnlyIn(Dist.CLIENT)
   public abstract void alignWidgets(ShopCreatorEntryPanel var1, ShopCreatorEntryTypesPanel var2, ShopCreatorComponentData var3);

   public abstract void applyCreate(ShopCreatorComponentData var1, ShopEntry var2);
}
