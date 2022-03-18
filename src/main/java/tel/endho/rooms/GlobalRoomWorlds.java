package tel.endho.rooms;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GlobalRoomWorlds {
  // private static Map<UUID, ArrayList<RoomWorld>> offlineRoomWorldmap = new
  // HashMap<>();//player uuid
  public static Map<UUID, GlobalRoomWorld> globalRoomWorldHashMap = new HashMap<>();

  public static Map<UUID, GlobalRoomWorld> getGlobalRoomWorlds() {
    return globalRoomWorldHashMap;
  }

  public static GlobalRoomWorld getGlobalRoomWorldUUID(UUID uuid) {
    return globalRoomWorldHashMap.get(uuid);
  }

  public static void putRoom(UUID uuid, GlobalRoomWorld globalRoomWorld) {
    globalRoomWorldHashMap.put(uuid, globalRoomWorld);
  }

  public static void addRoom(UUID uuid, GlobalRoomWorld globalRoomWorld) {
    if (!globalRoomWorldHashMap.containsKey(uuid))
      globalRoomWorldHashMap.put(uuid, globalRoomWorld);
  }

  public static Boolean isGlobalRoomWorld(UUID uuid) {
    return globalRoomWorldHashMap.containsKey(uuid);
  }

  public static void removeGlobalRoomWorld(UUID uuid) {
    globalRoomWorldHashMap.remove(uuid);
  }

  public static boolean isOnAnotherServer(UUID uuid) {
    try {
      if (globalRoomWorldHashMap.get(uuid) == null) {
        return false;
      } else {
        Rooms.debug("lastserver: " + globalRoomWorldHashMap.get(uuid).lastserver);
        Rooms.debug("currentserver: " + Rooms.configs.getStorageConfig().getString("bungeeservername"));
        return !globalRoomWorldHashMap.get(uuid).lastserver
            .equals(Rooms.configs.getStorageConfig().getString("bungeeservername"));
      }
    } catch (Exception e) {
      return false;
    }
    // return
    // !globalRoomWorldHashMap.get(uuid).lastserver.equals(Rooms.configs.getMySQLConfig().getString("bungeeservername"));
  }
}
