package tel.endho.rooms.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

//import com.plotsquared.core.plot.flag.implementations.LiquidFlowFlag;

import tel.endho.rooms.RoomWorld;
import tel.endho.rooms.RoomWorlds;


public class BucketListener implements Listener {

  @EventHandler
  public void onEmpty(BlockFromToEvent e) {
    try {
      if (RoomWorlds.isRoomWorld(e.getToBlock().getLocation().getWorld().getName())) {
        //final RoomWorld roomWorld = RoomWorlds.getRoomWorldString(e.getToBlock().getLocation().getWorld().getName());
        Double absX = Math.abs(e.getToBlock().getLocation().getX());
        Double absZ = Math.abs(e.getToBlock().getLocation().getZ());
        double bordersize = Bukkit.getWorld(e.getToBlock().getWorld().getName()).getWorldBorder().getSize();
        int limit = (int) (bordersize / 2);
        if (absX > limit || absZ > limit) {
          e.setCancelled(true);
        }
      }
    } catch (Exception exception) {

    }
  }

  @EventHandler
  public void onEmpty(PlayerBucketEmptyEvent e) {
    try {
      if (RoomWorlds.isRoomWorld(e.getPlayer().getLocation().getWorld().getName())) {
        final RoomWorld roomWorld = RoomWorlds.getRoomWorldString(e.getPlayer().getLocation().getWorld().getName());
        if (!roomWorld.isMember(e.getPlayer()) && !roomWorld.isTrusted(e.getPlayer())
            && !roomWorld.isOwner(e.getPlayer())) {
          if (!e.getPlayer().hasPermission("housemanager.admin")) {
            e.setCancelled(true);
          }
        }
      }
    } catch (Exception exception) {

    }
  }

  @EventHandler
  public void onFill(PlayerBucketFillEvent e) {
    try {
      if (RoomWorlds.isRoomWorld(e.getPlayer().getLocation().getWorld().getName())) {
        final RoomWorld roomWorld = RoomWorlds.getRoomWorldString(e.getPlayer().getLocation().getWorld().getName());
        if (!roomWorld.isMember(e.getPlayer()) && !roomWorld.isTrusted(e.getPlayer())
            && !roomWorld.isOwner(e.getPlayer())) {
          if (!e.getPlayer().hasPermission("housemanager.admin")) {
            e.setCancelled(true);
          }
        }
      }
    } catch (Exception exception) {

    }
  }
}