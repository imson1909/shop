package net.sixik.sdmshop.utils.config;

import dev.ftb.mods.ftblibrary.config.ConfigGroup;
import dev.ftb.mods.ftblibrary.config.ui.EditConfigScreen;
import net.sixik.sdmshop.mixin.accessors.EditConfigScreenAccessor;
import net.sixik.sdmshop.old_api.screen.ConfigScreenRefresher;

public class SDMEditConfigScreen extends EditConfigScreen implements ConfigScreenRefresher {
   private EditConfigScreenAccessor accessor = (EditConfigScreenAccessor)this;

   public SDMEditConfigScreen(ConfigGroup configGroup) {
      super(configGroup);
   }

   @Override
   public void refreshAndSafe(ConfigGroup group) {
      if (this.accessor.isChanged()) {
         this.accessor.getGroup().save(true);
      }

      this.accessor.setGroup(group);
      this.refreshWidgets();
   }
}
