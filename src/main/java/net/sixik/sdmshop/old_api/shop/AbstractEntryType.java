package net.sixik.sdmshop.old_api.shop;

import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.SimpleTextButton;
import dev.ftb.mods.ftblibrary.ui.Theme;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.sixik.sdmshop.client.render.BuyerRenderVariable;
import net.sixik.sdmshop.client.screen.base.buyer.AbstractBuyerScreen;
import net.sixik.sdmshop.old_api.ConfigSupport;
import net.sixik.sdmshop.old_api.ModObjectIdentifier;
import net.sixik.sdmshop.old_api.SearchSupport;
import net.sixik.sdmshop.shop.ShopEntry;
import net.sixik.sdmshop.utils.DataSerializerCompoundTag;
import net.sixik.sdmuilib.client.utils.TextHelper;
import net.sixik.sdmuilib.client.utils.math.Vector2;

public abstract class AbstractEntryType implements DataSerializerCompoundTag, ModObjectIdentifier, SearchSupport, ConfigSupport, ShopObject {
   protected final ShopEntry shopEntry;

   protected AbstractEntryType(ShopEntry shopEntry) {
      this.shopEntry = shopEntry;
   }

   public EntryTypeProperty getProperty() {
      return EntryTypeProperty.DEFAULT_COUNTABLE;
   }

   public abstract AbstractEntryType copy();

   public boolean isCountable() {
      return true;
   }

   public abstract boolean onBuy(Player var1, ShopEntry var2, int var3);

   public abstract boolean onSell(Player var1, ShopEntry var2, int var3);

   public abstract boolean canExecute(Player var1, ShopEntry var2, int var3);

   public abstract int howMany(Player var1, ShopEntry var2);

   public void sendNotifiedMessage(Player player, ShopEntry entry, int count) {
   }

   public Icon getCreativeIcon() {
      return Icons.DIAMOND;
   }

   public abstract Component getTranslatableForCreativeMenu();

   public void addEntryTooltip(TooltipList list, ShopEntry entry) {
   }

   public List<Component> getDescriptionForContextMenu() {
      return List.of(Component.m_237115_("sdm.shop.entry.creator.type." + this.getId() + ".description"));
   }

   public String getModNameForContextMenu() {
      return "";
   }

   public AbstractEntryType updateIcon(ItemStack icon) {
      this.shopEntry.updateIcon(icon);
      return this;
   }

   @OnlyIn(Dist.CLIENT)
   public void drawIcon(ShopEntry entry, GuiGraphics graphics, Theme theme, int x, int y, int w, int h, SimpleTextButton widget, int tick) {
      widget.drawIcon(graphics, theme, x, y, w, h);
   }

   @OnlyIn(Dist.CLIENT)
   public void drawTitle(
      ShopEntry entry, GuiGraphics graphics, Theme theme, int x, int y, int w, int h, BuyerRenderVariable variable, AbstractBuyerScreen screen
   ) {
      String title = entry.getTitle().getString();
      if (!title.isEmpty()) {
         Vector2 pos = variable.pos;
         w = TextHelper.getTextWidth(title);
         int w1 = screen.width - 10 - 2 - variable.iconSize * 2 - w;
         int w2 = w1 / 2;
         theme.drawString(graphics, title, pos.x + w2, pos.y + 1, Color4I.WHITE, 2);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void drawTitleCentered(ShopEntry entry, GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
      this.drawComponentCentered(graphics, entry.getTitle(), theme, x, y, w, h);
   }

   @OnlyIn(Dist.CLIENT)
   public void drawComponentCentered(GuiGraphics graphics, Component title, Theme theme, int x, int y, int w, int h) {
      if (!title.getString().isEmpty()) {
         int titleL = theme.getStringWidth(title);
         theme.drawString(graphics, title, x + (w - titleL) / 2, y);
      }
   }

   @Override
   public final ShopObjectTypes getShopType() {
      return ShopObjectTypes.ENTRY_TYPE;
   }
}
