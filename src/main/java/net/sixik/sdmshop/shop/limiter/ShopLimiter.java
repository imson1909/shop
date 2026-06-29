package net.sixik.sdmshop.shop.limiter;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.sixik.sdmeconomy.utils.ErrorCodes;
import net.sixik.sdmshop.old_api.shop.ShopObjectTypes;
import net.sixik.sdmshop.utils.DataSerializerCompoundTag;

public class ShopLimiter implements DataSerializerCompoundTag {
   private static final Map<UUID, Integer> NULL_MAP = new HashMap<>(0);
   public static final Path LIMITER_FOLDER = Path.of("limiter");
   public static final String FILE_NAME = "limiter.data";
   private static final UUID DEFAULT_UUID = UUID.fromString("57874c37-f8fe-4090-927e-be008faa95ed");
   private static final String PLAYERS_TAG = "players";
   private static final String PLAYER_ID_TAG = "playerID";
   private static final String PLAYER_ENTRY_TAG = "player_entry";
   private static final String PLAYER_TAG = "player";
   private static final String PLAYER_TAB_TAG = "player_tab";
   private static final String ID_TAG = "id";
   private static final String COUNT_TAG = "count";
   private final Map<UUID, Map<UUID, Integer>> playerTabData = new HashMap<>();
   private final Map<UUID, Integer> tabData = new HashMap<>();
   private final Map<UUID, Map<UUID, Integer>> playerEntryData = new HashMap<>();
   private final Map<UUID, Integer> entryData = new HashMap<>();

   public ErrorCodes deleteTabData(UUID tabId) {
      Map<UUID, Integer> data = this.playerTabData.getOrDefault(tabId, null);
      if (data == null) {
         Integer data2 = this.tabData.getOrDefault(tabId, -1);
         if (data2 == -1) {
            return ErrorCodes.NOT_FOUND;
         }

         this.tabData.remove(tabId);
      } else {
         this.playerTabData.remove(tabId);
      }

      return ErrorCodes.SUCCESS;
   }

   public ErrorCodes deleteEntryData(UUID tabId) {
      Map<UUID, Integer> data = this.playerEntryData.getOrDefault(tabId, null);
      if (data == null) {
         Integer data2 = this.entryData.getOrDefault(tabId, -1);
         if (data2 == -1) {
            return ErrorCodes.NOT_FOUND;
         }

         this.entryData.remove(tabId);
      } else {
         this.playerEntryData.remove(tabId);
      }

      return ErrorCodes.SUCCESS;
   }

   public ErrorCodes resetAllDataGlobal() {
      this.tabData.clear();
      this.entryData.clear();
      return ErrorCodes.SUCCESS;
   }

   public ErrorCodes resetAllData(UUID id, ShopObjectTypes types) {
      return switch (types) {
         case SHOP_ENTRY -> this.resetEntryDataAll(id);
         case SHOP_TAB -> this.resetTabDataAll(id);
         default -> throw new IllegalStateException("Unexpected value: " + types);
      };
   }

   public ErrorCodes resetAllData(Player player) {
      return this.resetAllData(player.m_36316_().getId());
   }

   public ErrorCodes resetAllData(UUID playerId) {
      if (this.playerEntryData.containsKey(playerId) && !this.playerTabData.containsKey(playerId)) {
         this.playerEntryData.getOrDefault(playerId, NULL_MAP).clear();
         this.playerTabData.getOrDefault(playerId, NULL_MAP).clear();
         return ErrorCodes.SUCCESS;
      } else {
         return ErrorCodes.NOT_FOUND;
      }
   }

   public ErrorCodes resetTabData(UUID tabId, Player player) {
      return this.resetTabData(tabId, player.m_36316_().getId());
   }

   public ErrorCodes resetTabData(UUID tabId, UUID playerId) {
      Map<UUID, Integer> value = this.playerTabData.getOrDefault(tabId, new HashMap<>());
      if (value.isEmpty()) {
         return ErrorCodes.NOT_FOUND;
      }

      value.put(playerId, 0);
      return ErrorCodes.SUCCESS;
   }

   public ErrorCodes resetTabData(UUID tabId) {
      Integer id = this.tabData.getOrDefault(tabId, -1);
      if (id == -1) {
         return ErrorCodes.NOT_FOUND;
      }

      this.tabData.put(tabId, 0);
      return ErrorCodes.SUCCESS;
   }

   public ErrorCodes resetTabDataAll(UUID tabId) {
      ErrorCodes code = this.resetTabData(tabId);
      if (code.isSuccess()) {
         return code;
      }

      Map<UUID, Integer> tabData = this.playerTabData.getOrDefault(tabId, new HashMap<>());
      if (tabData.isEmpty()) {
         return ErrorCodes.NOT_FOUND;
      }

      tabData.clear();
      return ErrorCodes.SUCCESS;
   }

   public ErrorCodes resetEntryData(UUID entryId, Player player) {
      return this.resetEntryData(entryId, player.m_36316_().getId());
   }

   public ErrorCodes resetEntryData(UUID entryId, UUID playerId) {
      Map<UUID, Integer> value = this.playerEntryData.getOrDefault(entryId, new HashMap<>());
      if (value.isEmpty()) {
         return ErrorCodes.NOT_FOUND;
      }

      value.put(playerId, 0);
      return ErrorCodes.SUCCESS;
   }

   public ErrorCodes resetEntryData(UUID entryId) {
      Integer id = this.entryData.getOrDefault(entryId, -1);
      if (id == -1) {
         return ErrorCodes.NOT_FOUND;
      }

      this.entryData.put(entryId, 0);
      return ErrorCodes.SUCCESS;
   }

   public ErrorCodes resetEntryDataAll(UUID entryId) {
      ErrorCodes code = this.resetEntryData(entryId);
      if (code.isSuccess()) {
         return code;
      }

      Map<UUID, Integer> tabData = this.playerEntryData.getOrDefault(entryId, new HashMap<>());
      if (tabData.isEmpty()) {
         return ErrorCodes.NOT_FOUND;
      }

      tabData.clear();
      return ErrorCodes.SUCCESS;
   }

   public Optional<Integer> getTabData(UUID tabId, Player player) {
      return this.getTabData(tabId, player.m_36316_().getId());
   }

   public Optional<Integer> getTabData(UUID tabId, UUID playerId) {
      Map<UUID, Integer> value = this.playerTabData.getOrDefault(tabId, new HashMap<>());
      if (value.isEmpty()) {
         return Optional.empty();
      }

      Integer result = value.getOrDefault(playerId, null);
      return result == null ? Optional.empty() : Optional.of(result);
   }

   public boolean containsTabData(UUID uuid) {
      return this.tabData.containsKey(uuid) || this.playerTabData.containsKey(uuid);
   }

   public boolean containsEntryData(UUID uuid) {
      return this.entryData.containsKey(uuid) || this.playerEntryData.containsKey(uuid);
   }

   public Optional<Integer> getTabData(UUID tabId) {
      Integer result = this.tabData.getOrDefault(tabId, null);
      return result == null ? Optional.empty() : Optional.of(result);
   }

   public Optional<Integer> getEntryData(UUID entryId, Player player) {
      return this.getEntryData(entryId, player.m_36316_().getId());
   }

   public Optional<Integer> getEntryData(UUID entryId, UUID playerId) {
      Map<UUID, Integer> value = this.playerEntryData.getOrDefault(entryId, new HashMap<>());
      if (value.isEmpty()) {
         return Optional.empty();
      }

      Integer result = value.getOrDefault(playerId, null);
      return result == null ? Optional.empty() : Optional.of(result);
   }

   public Optional<Integer> getEntryData(UUID entryId) {
      Integer result = this.entryData.getOrDefault(entryId, null);
      return result == null ? Optional.empty() : Optional.of(result);
   }

   public void addEntryData(UUID uuid, int count) {
      this.entryData.merge(uuid, count, Integer::sum);
   }

   public void addOrSetEntryData(UUID uuid, int count) {
      if (this.entryData.containsKey(uuid)) {
         this.addEntryData(uuid, count);
      } else {
         this.entryData.put(uuid, count);
      }
   }

   public void addEntryData(UUID uuid, Player player, int count) {
      this.addEntryData(uuid, player.m_36316_().getId(), count);
   }

   public void addEntryData(UUID uuid, UUID playerId, int count) {
      this.playerEntryData.computeIfAbsent(uuid, k -> new HashMap<>()).merge(playerId, count, Integer::sum);
   }

   public void addOrSetEntryData(UUID uuid, UUID playerId, int count) {
      Map<UUID, Integer> map = this.playerEntryData.computeIfAbsent(uuid, k -> new HashMap<>());
      if (map.containsKey(playerId)) {
         map.merge(playerId, count, Integer::sum);
      } else {
         map.put(playerId, count);
      }

      this.playerEntryData.put(uuid, map);
   }

   public void setEntryData(UUID uuid, int count) {
      this.entryData.put(uuid, count);
   }

   public void setEntryData(UUID uuid, Player player, int count) {
      this.setEntryData(uuid, player.m_36316_().getId(), count);
   }

   public void setEntryData(UUID uuid, UUID playerId, int count) {
      this.playerEntryData.computeIfAbsent(uuid, k -> new HashMap<>()).put(playerId, count);
   }

   public void addTabData(UUID uuid, int count) {
      this.tabData.merge(uuid, count, Integer::sum);
   }

   public void addTabData(UUID uuid, Player player, int count) {
      this.addTabData(uuid, player.m_36316_().getId(), count);
   }

   public void addTabData(UUID uuid, UUID playerId, int count) {
      this.playerTabData.computeIfAbsent(uuid, k -> new HashMap<>()).merge(playerId, count, Integer::sum);
   }

   public void setTabData(UUID uuid, int count) {
      this.tabData.put(uuid, count);
   }

   public void setTabData(UUID uuid, Player player, int count) {
      this.setTabData(uuid, player.m_36316_().getId(), count);
   }

   public void setTabData(UUID uuid, UUID playerId, int count) {
      this.playerTabData.computeIfAbsent(uuid, k -> new HashMap<>()).put(playerId, count);
   }

   public CompoundTag serialize() {
      CompoundTag nbt = new CompoundTag();
      ListTag playerData = new ListTag();
      Set<UUID> playerIds = new HashSet<>(this.playerEntryData.keySet());
      playerIds.addAll(this.entryData.keySet());
      if (!playerIds.isEmpty()) {
         for (UUID playerId : playerIds) {
            CompoundTag playerTag = this.serializeClient(playerId);
            playerTag.m_128362_("playerID", playerId);
            playerData.add(playerTag);
         }

         nbt.m_128365_("players", playerData);
      }

      return nbt;
   }

   public void deserialize(CompoundTag nbt) {
      this.playerTabData.clear();
      this.playerEntryData.clear();
      if (nbt.m_128441_("players")) {
         for (Tag tag : nbt.m_128437_("players", 10)) {
            CompoundTag playerTag = (CompoundTag)tag;
            UUID playerId = playerTag.m_128342_("playerID");
            Map<UUID, Integer> entryMap = this.deserializeMap(playerTag, "player_entry", "player");
            this.playerEntryData.put(playerId, entryMap);
            Map<UUID, Integer> tabMap = this.deserializeMap(playerTag, "player_tab");
            this.playerTabData.put(playerId, tabMap);
         }
      }
   }

   public CompoundTag serializeClient(Player player) {
      return this.serializeClient(player.m_36316_().getId());
   }

   public CompoundTag serializeClient(UUID uuid) {
      CompoundTag result = new CompoundTag();
      ListTag entryList = this.serializeMap(this.playerEntryData.getOrDefault(uuid, new HashMap<>()));
      entryList.addAll(this.serializeMap(this.playerEntryData.getOrDefault(DEFAULT_UUID, new HashMap<>())));
      result.m_128365_("player_entry", entryList);
      ListTag tabList = this.serializeMap(this.playerTabData.getOrDefault(uuid, new HashMap<>()));
      tabList.addAll(this.serializeMap(this.playerTabData.getOrDefault(DEFAULT_UUID, new HashMap<>())));
      result.m_128365_("player_tab", tabList);
      return result;
   }

   public void deserializeClient(CompoundTag nbt) {
      this.entryData.clear();
      this.tabData.clear();
      ListTag entryList = this.getListTag(nbt, "player_entry", "player");
      this.entryData.putAll(this.deserializeMap(entryList));
      ListTag tabList = this.getListTag(nbt, "player_tab");
      this.tabData.putAll(this.deserializeMap(tabList));
   }

   private ListTag serializeMap(Map<UUID, Integer> map) {
      ListTag list = new ListTag();

      for (Entry<UUID, Integer> entry : map.entrySet()) {
         CompoundTag tag = new CompoundTag();
         tag.m_128362_("id", entry.getKey());
         tag.m_128405_("count", entry.getValue());
         list.add(tag);
      }

      return list;
   }

   private Map<UUID, Integer> deserializeMap(ListTag list) {
      Map<UUID, Integer> result = new HashMap<>();

      for (Tag tag : list) {
         CompoundTag compound = (CompoundTag)tag;
         result.put(compound.m_128342_("id"), compound.m_128451_("count"));
      }

      return result;
   }

   private Map<UUID, Integer> deserializeMap(CompoundTag tag, String... keys) {
      ListTag list = this.getListTag(tag, keys);
      return this.deserializeMap(list);
   }

   private ListTag getListTag(CompoundTag tag, String... keys) {
      for (String key : keys) {
         if (tag.m_128441_(key)) {
            return tag.m_128437_(key, 10);
         }
      }

      return new ListTag();
   }
}
