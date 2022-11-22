package tel.endho.rooms;

import java.util.UUID;

import com.fastasyncworldedit.core.FaweAPI;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

public class WorldGuardManager {
  //todo call after load
  public void setupRoom(RoomWorld roomWorld) {
    RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
    RegionManager regions = container.get(FaweAPI.getWorld(roomWorld.getWorldUUID().toString()));
    if (regions == null) {
      return;
    }
    if (!regions.hasRegion("__global__")) {
      return;
    }
    ProtectedRegion global = regions.getRegion("__global__");
    if (global == null) {
      return;
    }
    global.getOwners().addPlayer(roomWorld.getOwnerUUID());
    roomWorld.getTrustedMembers().keySet().forEach(uuid -> {
      global.getMembers().addPlayer(uuid);
    });
    roomWorld.getMembers().keySet().forEach(uuid->{
      global.getMembers().addPlayer(uuid);
    });
    global.setFlag(Flags.PASSTHROUGH, StateFlag.State.DENY);
    for (Flag<?> all : WorldGuard.getInstance().getFlagRegistry().getAll()) {
      String named = all.getName();
      //String value = all.getDefault().toString();
     // Rooms.debug("named: "+named+" value: "+value);
    }
  }

  public static void unloadWorld(UUID uuid) {
    RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
    container.unload(FaweAPI.getWorld(uuid.toString()));
  }
}
