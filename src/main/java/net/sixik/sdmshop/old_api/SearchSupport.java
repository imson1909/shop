package net.sixik.sdmshop.old_api;

import net.minecraft.tags.TagKey;

public interface SearchSupport {
   boolean isSearch(String var1);

   interface ByTag {
      boolean search(TagKey<?> var1);
   }

   interface ByTooltip {
      boolean search(String var1);
   }
}
