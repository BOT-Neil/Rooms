package tel.endho.rooms.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEvent;

import tel.endho.rooms.RoomWorld;
import tel.endho.rooms.RoomWorlds;

public class VehicleListener implements Listener {

  @EventHandler
  public void onDamage(VehicleDamageEvent e) {
    try {
      if(e.getAttacker() instanceof Player){
        Player player = ((Player)e.getAttacker());
        if (RoomWorlds.isRoomWorld(player.getLocation().getWorld().getName())) {
        final RoomWorld roomWorld = RoomWorlds.getRoomWorldString(player.getLocation().getWorld().getName());
        if (!roomWorld.isMember(player) && !roomWorld.isTrusted(player)
            && !roomWorld.isOwner(player)) {
          if (!player.hasPermission("rooms.admin")) {
            e.setCancelled(true);
          }
        }
      }
      }
      
    } catch (Exception exception) {

    }
  }

}
