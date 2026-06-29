package net.sixik.sdmshop.shop;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.sixik.sdmshop.SDMShop;
import net.sixik.sdmshop.api.ShopBase;
import net.sixik.sdmshop.api.ShopEvents;
import net.sixik.sdmshop.old_api.ConfigSupport;
import net.sixik.sdmshop.utils.DataSerializerCompoundTag;
import org.jetbrains.annotations.Nullable;

public class BaseShop implements DataSerializerCompoundTag, ConfigSupport, ShopBase {
   public static final Codec<BaseShop> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(CompoundTag.f_128325_.fieldOf("shop").forGetter(BaseShop::serialize)).apply(instance, BaseShop::new)
   );
   public static final String ENTRIES_KEY = "shop_entries";
   public static final String TABS_KEY = "shop_tabs";
   public static final String PARAMS_KEY = "shop_params";
   protected final ResourceLocation registryId;
   protected final UUID uuid;
   protected final List<ShopEntry> shopEntries = new ObjectArrayList();
   protected final List<ShopTab> shopTabs = new ObjectArrayList();
   protected final ShopParams shopParams = new ShopParams();
   private String version = "null_hash";
   private boolean dirty = false;
   @Nullable
   private Tag cachedNBT = null;
   private final List<ShopBase.ShopChangeListener> shopChangeListeners = new ObjectArrayList();
   private final List<ShopBase.EntryAddListener> entryAddListeners = new ObjectArrayList();
   private final List<ShopBase.EntryRemoveListener> entryRemoveListeners = new ObjectArrayList();
   private final List<ShopBase.EntryChangeListener> entryChangeListeners = new ObjectArrayList();
   private final List<ShopBase.TabAddListener> tabAddListeners = new ObjectArrayList();
   private final List<ShopBase.TabRemoveListener> tabRemoveListeners = new ObjectArrayList();
   private final List<ShopBase.TabChangeListener> tabChangeListeners = new ObjectArrayList();

   public BaseShop(CompoundTag data) {
      this.registryId = ResourceLocation.m_135820_(data.m_128461_("id"));
      this.uuid = data.m_128342_("uuid");
      this.deserialize(data);
   }

   public BaseShop(ResourceLocation registryId, UUID uuid) {
      this.registryId = registryId;
      this.uuid = uuid;
   }

   @Override
   public void onChangeMethod() {
      this.cachedNBT = this.serialize();
      this.version = this.calculateVersion();
      List<ShopBase.ShopChangeListener> listeners = this.getShopChangeListeners();

      for (int i = 0; i < listeners.size(); i++) {
         listeners.get(i).handle(this);
      }

      ((ShopBase.ShopChangeListener)ShopEvents.SHOP_CHANGE_EVENT.invoker()).handle(this);
      this.onChangeEvent();
      this.setDirty(false);
   }

   @Override
   public ResourceLocation getRegistryId() {
      return this.registryId;
   }

   @Override
   public UUID getId() {
      return this.uuid;
   }

   @Override
   public List<ShopEntry> getEntries() {
      return this.shopEntries;
   }

   @Override
   public List<ShopTab> getTabs() {
      return this.shopTabs;
   }

   @Override
   public <T extends ShopBase> Codec<T> codec() {
      return (Codec<T>)CODEC;
   }

   @Override
   public ShopParams getParams() {
      return this.shopParams;
   }

   @Override
   public boolean isDirty() {
      return this.dirty;
   }

   @Override
   public void setDirty(boolean value) {
      this.dirty = value;
   }

   @Nullable
   @Override
   public Tag getCachedNbt() {
      return this.cachedNBT;
   }

   @Override
   public void setCachedNbt(Tag nbt) {
      this.cachedNBT = nbt;
      this.version = this.calculateVersion();
   }

   @Override
   public String getVersion() {
      return this.version;
   }

   @Override
   public void setVersion(String version) {
      this.version = version;
   }

   @Override
   public List<ShopBase.ShopChangeListener> getShopChangeListeners() {
      return this.shopChangeListeners;
   }

   @Override
   public List<ShopBase.EntryAddListener> getEntryAddListeners() {
      return this.entryAddListeners;
   }

   @Override
   public List<ShopBase.EntryRemoveListener> getEntryRemoveListeners() {
      return this.entryRemoveListeners;
   }

   @Override
   public List<ShopBase.EntryChangeListener> getEntryChangeListeners() {
      return this.entryChangeListeners;
   }

   @Override
   public List<ShopBase.TabAddListener> getTabAddListeners() {
      return this.tabAddListeners;
   }

   @Override
   public List<ShopBase.TabRemoveListener> getTabRemoveListeners() {
      return this.tabRemoveListeners;
   }

   @Override
   public List<ShopBase.TabChangeListener> getTabChangeListeners() {
      return this.tabChangeListeners;
   }

   @Override
   public void getConfig(ConfigGroup group) {
      this.shopParams.getConfig(group);
   }

   @Override
   public CompoundTag serializeOrCache() {
      if (this.cachedNBT == null) {
         this.cachedNBT = this.serialize();
      }

      return (CompoundTag)this.cachedNBT;
   }

   public CompoundTag serialize() {
      CompoundTag nbt = new CompoundTag();
      nbt.m_128359_("id", this.registryId.toString());
      nbt.m_128362_("uuid", this.uuid);
      ListTag tags = new ListTag();

      for (ShopEntry entry : this.shopEntries) {
         tags.add(entry.serialize());
      }

      nbt.m_128365_("shop_entries", tags);
      tags = new ListTag();

      for (ShopTab entry : this.shopTabs) {
         tags.add(entry.serialize());
      }

      nbt.m_128365_("shop_tabs", tags);
      nbt.m_128365_("shop_params", this.shopParams.serialize());
      return nbt;
   }

   public void deserialize(CompoundTag tag) {
      this.getEntries().clear();
      this.getTabs().clear();
      if (tag.m_128441_("shop_entries")) {
         for (Tag tag1 : (ListTag)tag.m_128423_("shop_entries")) {
            try {
               ShopEntry entry = new ShopEntry(this);
               entry.deserialize((CompoundTag)tag1);
               this.getEntries().add(entry);
            } catch (Exception e) {
               SDMShop.LOGGER.error("Error when read ShopEntry", e);
            }
         }
      }

      if (tag.m_128441_("shop_tabs")) {
         for (Tag tag1 : (ListTag)tag.m_128423_("shop_tabs")) {
            try {
               ShopTab entry = new ShopTab(this);
               entry.deserialize((CompoundTag)tag1);
               this.getTabs().add(entry);
            } catch (Exception e) {
               SDMShop.LOGGER.error("Error when read ShopTab", e);
            }
         }
      }

      this.shopParams.deserialize(tag.m_128469_("shop_params"));
      this.version = this.calculateVersion();
   }
}
