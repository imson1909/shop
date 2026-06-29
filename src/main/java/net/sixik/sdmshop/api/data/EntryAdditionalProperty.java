package net.sixik.sdmshop.api.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class EntryAdditionalProperty {
   public static Codec<EntryAdditionalProperty> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            Codec.DOUBLE.fieldOf("price").forGetter(EntryAdditionalProperty::getPrice),
            Codec.LONG.fieldOf("count").forGetter(EntryAdditionalProperty::getCount),
            Codec.INT.fieldOf("limit").forGetter(EntryAdditionalProperty::getLimit)
         )
         .apply(instance, EntryAdditionalProperty::new)
   );
   protected double price;
   protected long count;
   protected int limit;

   public EntryAdditionalProperty(double price, long count, int limit) {
      this.price = price;
      this.count = count;
      this.limit = limit;
   }

   public EntryAdditionalProperty() {
   }

   public int getLimit() {
      return this.limit;
   }

   public long getCount() {
      return this.count;
   }

   public double getPrice() {
      return this.price;
   }

   public void setLimit(int limit) {
      this.limit = limit;
   }

   public void setCount(long count) {
      this.count = count;
   }

   public void setPrice(double price) {
      this.price = price;
   }

   public boolean isEmpty() {
      return this.price == 0.0 && this.count == 0L && this.limit == 0;
   }
}
