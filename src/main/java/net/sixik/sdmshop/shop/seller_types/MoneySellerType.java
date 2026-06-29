package net.sixik.sdmshop.shop.seller_types;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.NameMap;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.ui.Widget;
import dev.ftb.mods.ftblibrary.ui.WidgetType;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.sixik.sdmeconomy.api.EconomyAPI;
import net.sixik.sdmeconomy.currencies.BaseCurrency;
import net.sixik.sdmeconomy.currencies.data.CurrencyData;
import net.sixik.sdmshop.currencies.SDMCoin;
import net.sixik.sdmshop.old_api.shop.AbstractEntrySellerType;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.utils.ShopUtils;
import org.jetbrains.annotations.Nullable;

public class MoneySellerType extends AbstractEntrySellerType<Double> {
   public static final String DEFAULT_MONEY = SDMCoin.getId();
   protected String money_id = DEFAULT_MONEY;

   public MoneySellerType() {
      this(0.0);
   }

   public MoneySellerType(Double objectType) {
      super(objectType);
   }

   @Override
   public void _getConfig(ConfigGroup configGroup) {
      configGroup.addEnum("money_id", this.money_id, s -> this.money_id = s, getList());
   }

   @Override
   public boolean onBuy(Player player, ShopEntry shopEntry, long countSell) {
      double value = ShopUtils.getMoney(player, this.money_id);
      return ShopUtils.setMoney(player, this.money_id, value - shopEntry.getPrice() * countSell);
   }

   @Override
   public boolean onSell(Player player, ShopEntry shopEntry, long countSell) {
      double value = ShopUtils.getMoney(player, this.money_id);
      return ShopUtils.setMoney(player, this.money_id, value + shopEntry.getPrice() * countSell);
   }

   @Override
   public double getMoney(Player player, ShopEntry shopEntry) {
      return ShopUtils.getMoney(player, this.money_id);
   }

   @Override
   public AbstractEntrySellerType<Double> copy() {
      return new MoneySellerType(this.objectType);
   }

   @Override
   public String getId() {
      return "money_seller";
   }

   @Override
   public String getEnumName() {
      return "MONEY";
   }

   @Override
   public String moneyToString(ShopEntry entry) {
      return entry.getPrice()
         + " "
         + ((CurrencyData)EconomyAPI.getAllCurrency().value).currencies.stream().filter(s -> s.getName().equals(this.money_id)).findFirst().get().symbol.value;
   }

   @Override
   public CompoundTag _serialize() {
      CompoundTag nbt = new CompoundTag();
      nbt.m_128359_("money_id", this.money_id);
      return nbt;
   }

   @Override
   public void _deserialize(CompoundTag tag) {
      this.money_id = tag.m_128461_("money_id");
   }

   public static NameMap<String> getList() {
      List<String> str = new ArrayList<>();

      for (BaseCurrency currency : ((CurrencyData)EconomyAPI.getAllCurrency().value).currencies) {
         str.add(currency.getName());
      }

      return NameMap.of(DEFAULT_MONEY, str).create();
   }

   @Override
   public int getRenderWight(GuiGraphics graphics, Theme theme, int x, int y, int width, int height, double count, @Nullable Widget widget, int additionSize) {
      return theme.getStringWidth(ShopUtils.moneyToString(count, this.money_id));
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void draw(GuiGraphics graphics, Theme theme, int x, int y, int width, int height, double count, @Nullable Widget widget, int additionSize) {
      theme.drawString(graphics, ShopUtils.moneyToString(count, this.money_id), x + 2, y + 1, theme.getContentColor(WidgetType.NORMAL), 2);
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public int draw(GuiGraphics graphics, Theme theme, int x, int y, int width, int height, double count) {
      String txt = ShopUtils.moneyToString(count, this.money_id);
      theme.drawString(graphics, txt, x + 2, y + 1, theme.getContentColor(WidgetType.NORMAL), 2);
      return theme.getStringWidth(txt);
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public int getRenderSize(GuiGraphics graphics, Theme theme, int x, int y, int width, int height, double count) {
      return theme.getStringWidth(ShopUtils.moneyToString(count, this.money_id));
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void drawCentered(GuiGraphics graphics, Theme theme, int x, int y, int width, int height, double count) {
      String txt = ShopUtils.moneyToString(count, this.money_id);
      int tL = theme.getStringWidth(txt);
      theme.drawString(graphics, txt, x + (width - tL) / 2, y);
   }
}
