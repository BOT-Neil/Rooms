package tel.endho.rooms;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.fastasyncworldedit.core.FaweAPI;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import tel.endho.rooms.util.enums.usergroup;

public class WorldGuardManager {
  //todo add island suffix rmnether rmend
  public static void addPlayerGroup(RoomWorld roomWorld, Player player, usergroup usergroup) {

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
    switch (usergroup) {
      case BLOCKED:
        break;
      case MEMBER:
        global.getMembers().addPlayer(player.getUniqueId());
        ;
        break;
      case TRUSTED:
        global.getMembers().addPlayer(player.getUniqueId());
        break;
      default:
        break;

    }
    ProtectedRegion region = new GlobalProtectedRegion("roomworld");
  }

  @SuppressWarnings("null")
  public static void setupRoom(RoomWorld roomWorld, String uuidsuffix) {
    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "wg reload");
    RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
    RegionManager regions = container.get(FaweAPI.getWorld(roomWorld.getWorldUUID().toString() + uuidsuffix));
    if (regions == null) {
      return;
    }

    if (!regions.hasRegion("__global__")) {
      ProtectedRegion region = new GlobalProtectedRegion("__global__");
      regions.addRegion(region);
      // return;
    }
    ProtectedRegion global = regions.getRegion("__global__");
    if (global == null) {
      return;
    }

    global.getOwners().addPlayer(roomWorld.getOwnerUUID());
    roomWorld.getTrustedMembers().keySet().forEach(uuid -> {
      global.getMembers().addPlayer(uuid);
    });
    roomWorld.getMembers().keySet().forEach(uuid -> {
      global.getMembers().addPlayer(uuid);
    });
    global.setFlag(Flags.PASSTHROUGH, StateFlag.State.DENY);

    for (Flag<?> all : WorldGuard.getInstance().getFlagRegistry().getAll()) {
      String named = all.getName();
      String value;
      if (all.getDefault() == null) {
        value = "null";
      } else {

        value = all.getDefault().toString();
      }

      System.out.println("named: " + named + " value: " + value);
    }
  }

  public static void unloadWorld(UUID uuid) {
    RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
    container.unload(FaweAPI.getWorld(uuid.toString()));
  }
}
