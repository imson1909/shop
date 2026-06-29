package net.sixik.sdmshop.config;

import net.minecraft.nbt.CompoundTag;
import net.sixik.sdmshop.utils.DataSerializerCompoundTag;

public class ShopConfigImpl implements DataSerializerCompoundTag {
   protected static final String DISABLE_KEY = "disable_key_bind";
   protected static final String SEND_NOTIFY_KEY = "send_notify";
   protected boolean disableKeyBind;
   protected boolean sendNotify;

   public CompoundTag serialize() {
      CompoundTag nbt = new CompoundTag();
      nbt.m_128379_("disable_key_bind", this.disableKeyBind);
      nbt.m_128379_("send_notify", this.sendNotify);
      return nbt;
   }

   public void deserialize(CompoundTag tag) {
      this.disableKeyBind = tag.m_128471_("disable_key_bind");
      this.sendNotify = tag.m_128471_("send_notify");
   }
}
