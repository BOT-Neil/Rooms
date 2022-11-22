package tel.endho.rooms.listeners;

import org.bukkit.Material;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import tel.endho.rooms.RoomWorld;
import tel.endho.rooms.RoomWorlds;
import tel.endho.rooms.Rooms;

public class BlockListener implements Listener {
  @EventHandler
  public void onBreak(BlockBreakEvent e) {
    try {
      if (RoomWorlds.isRoomWorld(e.getPlayer().getLocation().getWorld().getName())) {
        final RoomWorld roomWorld = RoomWorlds.getRoomWorldString(e.getPlayer().getLocation().getWorld().getName());
        if (!roomWorld.isMember(e.getPlayer()) && !roomWorld.isTrusted(e.getPlayer())
            && !roomWorld.isOwner(e.getPlayer())) {
          if (!e.getPlayer().hasPermission("rooms.admin")) {
            e.setCancelled(true);
          }
        }
      }
    } catch (Exception exception) {

    }
  }

  @EventHandler
  public void onPlace(BlockPlaceEvent e) {
    try {
      if (RoomWorlds.isRoomWorld(e.getPlayer().getLocation().getWorld().getName())) {
        final RoomWorld roomWorld = RoomWorlds.getRoomWorldString(e.getPlayer().getLocation().getWorld().getName());
        if (!roomWorld.isMember(e.getPlayer()) && !roomWorld.isTrusted(e.getPlayer())
            && !roomWorld.isOwner(e.getPlayer())) {
          if (!e.getPlayer().hasPermission("rooms.admin")) {
            e.setCancelled(true);
          }
        }
      }
    } catch (Exception exception) {

    }
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
  public void onBlocksPlace(BlockMultiPlaceEvent e) {
    try {
      if (RoomWorlds.isRoomWorld(e.getPlayer().getLocation().getWorld().getName())) {
        final RoomWorld roomWorld = RoomWorlds.getRoomWorldString(e.getPlayer().getLocation().getWorld().getName());
        if (!roomWorld.isMember(e.getPlayer()) && !roomWorld.isTrusted(e.getPlayer())
            && !roomWorld.isOwner(e.getPlayer())) {
          if (!e.getPlayer().hasPermission("rooms.admin")) {
            e.setCancelled(true);
          }
        }
      }
    } catch (Exception exception) {
    }
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
  public void onBlocksPlace(BlockFertilizeEvent e) {
    try {
      if (RoomWorlds.isRoomWorld(e.getPlayer().getLocation().getWorld().getName())) {
        final RoomWorld roomWorld = RoomWorlds.getRoomWorldString(e.getPlayer().getLocation().getWorld().getName());
        if (!roomWorld.isMember(e.getPlayer()) && !roomWorld.isTrusted(e.getPlayer())
            && !roomWorld.isOwner(e.getPlayer())) {
          if (!e.getPlayer().hasPermission("rooms.admin")) {
            e.setCancelled(true);
          }
        }
      }
    } catch (Exception exception) {
    }
  }

  @EventHandler
  public void onEntityDamage(EntityDamageEvent e) {
    if (e.getEntity().getType().equals(EntityType.PLAYER)) {
      Player player = (Player) e.getEntity();
      try {
        if (RoomWorlds.isRoomWorld(player.getLocation().getWorld().getName())) {
          final RoomWorld roomWorld = RoomWorlds
              .getRoomWorldString(player.getLocation().getWorld().getName());
          if (!roomWorld.isMember(player) && !roomWorld.isTrusted(player) && !roomWorld.isOwner(player)) {
            if (!player.hasPermission("rooms.admin")) {
              e.setCancelled(true);
            }
          }
        }
      } catch (Exception exception) {

      }
    }

  }

  @EventHandler
  public void onEntityDamage(EntityDamageByEntityEvent e) {
    if (e.getEntity().getType().equals(EntityType.PLAYER)) {
      Player player = (Player) e.getEntity();
      try {
        if (RoomWorlds.isRoomWorld(player.getLocation().getWorld().getName())) {
          final RoomWorld roomWorld = RoomWorlds
              .getRoomWorldString(player.getLocation().getWorld().getName());
          if (!roomWorld.isMember(player) && !roomWorld.isTrusted(player) && !roomWorld.isOwner(player)) {
            if (!player.hasPermission("rooms.admin")) {
              e.setCancelled(true);
            }
          }
        }
      } catch (Exception exception) {

      }
    }

  }

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onMelting(BlockFadeEvent event) {
    try {
      if (RoomWorlds.isRoomWorld(event.getBlock().getWorld().getName())) {
        if (event.getBlock().getType().equals(Material.ICE)) {
          event.setCancelled(true);
        }
      }
    } catch (Exception exception) {
    }

  }

  @EventHandler
  public void onDecay(LeavesDecayEvent event) {
    try {
      if (RoomWorlds.isRoomWorld(event.getBlock().getWorld().getName())) {
        event.setCancelled(true);
        Leaves leaf = (Leaves) event.getBlock().getBlockData();
        leaf.setPersistent(true);
        event.getBlock().setBlockData(leaf);
      }
    } catch (Exception exception) {
    }
  }

  @EventHandler
  public void onBlockPhysics(BlockPhysicsEvent event) {
    try {
      if (RoomWorlds.isRoomWorld(event.getBlock().getWorld().getName())) {
        if (event.getBlock().getType().equals(Material.ICE) && event.getBlock().getType().equals(Material.SNOW_BLOCK)) {
          event.setCancelled(true);
        }
      }
    } catch (Exception exception) {
    }
  }

  @EventHandler
  public void blockBreak(BlockBreakEvent e) {
    if (Rooms.configs.getGeneralConfig().getStringList("spawnworlds")
        .contains(e.getBlock().getLocation().getWorld().getName())) {
      if (e.getPlayer().hasPermission("rooms.spawnbypass")) {
        return;
      }
      e.setCancelled(true);
    }

  }

  @EventHandler
  public void blockPlace(BlockPlaceEvent e) {
    if (Rooms.configs.getGeneralConfig().getStringList("spawnworlds")
        .contains(e.getBlock().getLocation().getWorld().getName())) {
      if (e.getPlayer().hasPermission("rooms.spawnbypass")) {
        return;
      }
      e.setCancelled(true);
    }

  }

  @EventHandler
  public void onInteract(PlayerInteractEvent e) {
    if (Rooms.configs.getGeneralConfig().getStringList("spawnworlds")
        .contains(e.getPlayer().getLocation().getWorld().getName())) {
      if (e.getPlayer().hasPermission("rooms.spawnbypass")) {
        return;
      }
      e.setCancelled(true);
    }

  }
}