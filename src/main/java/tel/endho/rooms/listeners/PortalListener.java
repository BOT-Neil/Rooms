package tel.endho.rooms.listeners;

import com.grinderwolf.swm.api.exceptions.CorruptedWorldException;
import com.grinderwolf.swm.api.exceptions.NewerFormatException;
import com.grinderwolf.swm.api.exceptions.UnknownWorldException;
import com.grinderwolf.swm.api.exceptions.WorldInUseException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.PortalType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import tel.endho.rooms.RoomWorld;
import tel.endho.rooms.RoomWorlds;
import tel.endho.rooms.Rooms;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class PortalListener implements Listener {
  public static Map<UUID,Integer> portalcooldowns;
  @EventHandler
  public void onPortal(PlayerPortalEvent portalEvent) {
    Rooms.debug("debug1f");
    if (portalEvent.isCancelled()) {
      return;
    }
    Rooms.debug("debug2f");
    if (!Rooms.configs.getGeneralConfig().getBoolean("islandmode")) {
      return;
    }
    Rooms.debug("debug3f");
    Location currentLocation = portalEvent.getFrom();
    if (currentLocation.getWorld() != null && !RoomWorlds.isRoomWorld(currentLocation.getWorld().getName())) {
      Rooms.debug("currentlocation: " + currentLocation);
      return;
    }

    Rooms.debug("debug4f");
    PortalType type;
    if (portalEvent.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL) {
      type = PortalType.ENDER;
    } else if (portalEvent.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
      type = PortalType.NETHER;
    } else {
      return;
    }
    System.out.println("tomatoe");
    RoomWorld roomWorld = RoomWorlds.getRoomWorldString(currentLocation.getWorld().getName());
    Location newlocation;
    if (type.equals(PortalType.NETHER)) {
      if (currentLocation.getWorld().getName().endsWith("rmnether")) {
        newlocation = new Location(Bukkit.getWorld(roomWorld.getWorldUUID().toString()), 1, 70, 1);
        portalEvent.setTo(newlocation);
      } else {
        if (roomWorld.getHasNether()) {
          newlocation = new Location(Bukkit.getWorld(roomWorld.getWorldUUID().toString() + "rmnether"), 1, 70, 1);
          portalEvent.setTo(newlocation);
        } else {
          Rooms.roomWorldManager.genNether(roomWorld, portalEvent.getPlayer());
          // roomworldmanager.createNether(roomWorld, event.getPlayer()
        }
      }
    }
    if (type.equals(PortalType.ENDER)) {
      if (currentLocation.getWorld().getName().endsWith("rmend")) {
        newlocation = new Location(Bukkit.getWorld(roomWorld.getWorldUUID().toString()), 1, 70, 1);
        portalEvent.setTo(newlocation);
      } else {
        newlocation = new Location(Bukkit.getWorld(roomWorld.getWorldUUID().toString() + "rmend"), 1, 70, 1);
        portalEvent.setTo(newlocation);
      }
    }

  }

  @EventHandler
  public void onPortal(EntityPortalEnterEvent portalEvent)
      throws CorruptedWorldException, NewerFormatException, WorldInUseException, UnknownWorldException, IOException {
    Rooms.debug("debug1");
    if (!(portalEvent.getEntity() instanceof Player)) {
      return;
    }
    Player p = (Player) portalEvent.getEntity();
    if(portalcooldowns.containsKey(p.getUniqueId())){
      return;
    }
    portalcooldowns.put(p.getUniqueId() ,0);
    Rooms.debug("debug2");
    if (!Rooms.configs.getGeneralConfig().getBoolean("islandmode")) {
      return;
    }
    Rooms.debug("debug3");
    Location currentLocation = portalEvent.getLocation();
    if (currentLocation.getWorld() == null && !RoomWorlds.isRoomWorld(currentLocation.getWorld().getName())) {
      return;
    }
    Rooms.debug("debug4");
    PortalType type;
    if (currentLocation.getBlock().getType() == Material.END_PORTAL) {
      type = PortalType.ENDER;
    } else if (currentLocation.getBlock().getType() == Material.NETHER_PORTAL) {
      type = PortalType.NETHER;
    } else {
      return;
    }
    Rooms.debug("debug5");
    RoomWorld roomWorld = RoomWorlds.getRoomWorldString(currentLocation.getWorld().getName());
    //Location newlocation;
    if (type.equals(PortalType.NETHER)) {
      Rooms.debug("debug6");
      if (currentLocation.getWorld().getName().endsWith("rmnether")) {
        Rooms.debug("debug7");
        //newlocation = new Location(Bukkit.getWorld(roomWorld.getWorldUUID().toString()), 1, 70, 1);
        Rooms.roomWorldManager.TpOrLoadHouseWorld(p, roomWorld.getWorldUUID().toString());
        // p.teleport(newlocation);
      } else {
        if (roomWorld.getHasNether()) {
          //is nether loaded or load|| just fix unload task so all islands are loaded
          System.out.println("pumpkin");
          Rooms.roomWorldManager.TpOrLoadHouseWorld(p, roomWorld.getWorldUUID().toString() + "rmnether");
          //newlocation = new Location(Bukkit.getWorld(roomWorld.getWorldUUID().toString() + "rmnether"), 1, 70, 1);
          //p.teleport(newlocation);
        } else {
          Rooms.debug("debug9");
          Rooms.roomWorldManager.genNether(roomWorld, p);
          // roomworldmanager.createNether(roomWorld, event.getPlayer()
        }
      }
    }
    if (type.equals(PortalType.ENDER)) {

    }

  }

  @EventHandler
  public void EntityPortal(EntityPortalEvent event) {
    if (event.isCancelled()) {
      return;
    }
    // Entity e = event.getEntity();
    Location originalTo = event.getTo();
    Location currentLocation = event.getFrom();
    Rooms.debug("currentLocation: " + currentLocation.toString());
    assert originalTo != null;
    Rooms.debug("originalto: " + originalTo.toString());
    if (currentLocation.getWorld() != null && !RoomWorlds.isRoomWorld(currentLocation.getWorld().getName())) {
      return;
    }

    if (!(event.getEntity() instanceof Player)) {
      return;
    }
    // Player p = (Player) event.getEntity();
  }
}
