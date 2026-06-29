package net.sixik.sdmshop.shop.sorts;

import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import java.util.Objects;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.sixik.sdmshop.client.screen_new.MainShopScreen;
import net.sixik.sdmshop.old_api.shop.AbstractEntryType;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.shop.ShopTab;

public abstract class AbstractEntryTypeFilter<T extends AbstractEntryType> {
   protected final Class<? extends T> sortElementClass;
   protected boolean acceptSort = true;

   public AbstractEntryTypeFilter(Class<? extends AbstractEntryType> sortElementClass) {
      this.sortElementClass = sortElementClass;
   }

   public final boolean isCurrent(AbstractEntryType entryType) {
      return this.isCurrent((Class<? extends AbstractEntryType>)entryType.getClass());
   }

   public final boolean isCurrent(Class<? extends AbstractEntryType> entryTypeClass) {
      return Objects.equals(entryTypeClass, this.sortElementClass);
   }

   public final boolean isSupportedImpl(AbstractEntryType entryType) {
      return this.isCurrent(entryType) && this.isSupported((T)entryType);
   }

   public final boolean sorting(ShopEntry entry, ShopTab tab, AbstractEntryType entryType) {
      return !this.acceptSort || this.sort(entry, tab, (T)entryType);
   }

   protected abstract boolean isSupported(T var1);

   protected abstract boolean sort(ShopEntry var1, ShopTab var2, T var3);

   public final void collectFromImpl(AbstractEntryType type) {
      this.collectFrom((T)type);
   }

   protected void collectFrom(T entryType) {
   }

   public abstract Component getTitle();

   public void addTooltips(TooltipList tooltipList) {
   }

   @OnlyIn(Dist.CLIENT)
   public abstract void addWidget(Panel var1);

   public final void applyChange() {
      MainShopScreen.Instance.onFilterApply();
   }
}
