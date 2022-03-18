package tel.endho.rooms;

import org.bukkit.entity.Player;

import java.util.*;

public class RoomWorlds {
  private static Map<UUID, RoomWorld> roomWorldmap = new HashMap<>();// world uuid

  public static Map<UUID, RoomWorld> getRoomWorldsPlayer(Player player) {
    Map<UUID, RoomWorld> playerHouseMap = new HashMap<>();
    roomWorldmap.values().forEach(e -> {
      if (e.getOwnerUUID().equals(player.getUniqueId())) {
        playerHouseMap.put(e.getWorldUUID(), e);
      }
    });
    return playerHouseMap;
  }

  public static Map<UUID, RoomWorld> getRoomWorldsPlayer(String player) {
    Map<UUID, RoomWorld> playerHouseMap = new HashMap<>();
    roomWorldmap.values().forEach(e -> {
      if (e.getOwnerName().equalsIgnoreCase(player)) {
        playerHouseMap.put(e.getWorldUUID(), e);
      }
    });
    return playerHouseMap;
  }

  public static TreeMap<UUID, RoomWorld> getRoomWorldsPlayer(UUID player) {
    TreeMap<UUID, RoomWorld> playerHouseMap = new TreeMap<>();
    roomWorldmap.values().forEach(e -> {
      if (e.getOwnerUUID().equals(player)) {
        playerHouseMap.put(e.getWorldUUID(), e);
      }
    });
    return playerHouseMap;
  }

  public static Map<UUID, RoomWorld> getLoadedRoomWorlds() {
    Map<UUID, RoomWorld> worldHashMap = new HashMap<>();
    roomWorldmap.values().forEach(e -> {
      if (e.isLoaded()) {
        worldHashMap.put(e.getWorldUUID(), e);
      }
    });
    return worldHashMap;
  }

  public static RoomWorld getRoomWorldUUID(UUID uuid) {
    return roomWorldmap.get(uuid);
  }

  public static RoomWorld getRoomWorldString(String string) {
    if (string.endsWith("rmend")) {
      string = string.substring(0, Math.min(string.length(), 36));
    }
    if (string.endsWith("rmnether")) {
      string = string.substring(0, Math.min(string.length(), 36));
    }
    if (RoomWorldManager.isValidUUID(string)) {
      return getRoomWorldUUID(UUID.fromString(string));
    } else
      return null;
  }

  public static Map<UUID, RoomWorld> getRoomWolrds() {
    return roomWorldmap;
  }

  public static void addHouse(UUID uuid, RoomWorld roomWorld) {
    if (!roomWorldmap.containsKey(uuid))
      roomWorldmap.put(uuid, roomWorld);
  }

  public static Boolean isRoomWorld(String worldname) {
    if (worldname.endsWith("rmend")) {
      worldname = worldname.substring(0, Math.min(worldname.length(), 36));
    }
    if (worldname.endsWith("rmnether")) {
      worldname = worldname.substring(0, Math.min(worldname.length(), 36));
    }
    Rooms.debug("worlduuid: " + worldname);
    if (RoomWorldManager.isValidUUID(worldname)) {
      return isRoomWorld(UUID.fromString(worldname));
    } else {
      return false;
    }
  }

  public static Boolean isRoomWorld(UUID uuid) {
    return roomWorldmap.containsKey(uuid);
  }

  public static void removeRoomWorld(UUID uuid) {
    roomWorldmap.remove(uuid);
  }

}
