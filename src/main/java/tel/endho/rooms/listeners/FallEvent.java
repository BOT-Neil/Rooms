package tel.endho.rooms.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import tel.endho.rooms.RoomWorlds;

import java.util.UUID;

public class FallEvent implements Listener {
    @EventHandler
    public void onFall(EntityDamageEvent e){
        try{
        if(RoomWorlds.isRoomWorld(UUID.fromString(e.getEntity().getLocation().getWorld().getName()))&&e.getCause().equals(EntityDamageEvent.DamageCause.FALL)){
            e.setCancelled(true);
        }}catch (Exception exception){}
    }
}
