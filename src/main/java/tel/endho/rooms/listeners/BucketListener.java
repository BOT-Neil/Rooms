package tel.endho.rooms.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import tel.endho.rooms.RoomWorld;
import tel.endho.rooms.RoomWorlds;

import java.util.UUID;

public class BucketListener implements Listener {
    @EventHandler
    public void onEmpty(PlayerBucketEmptyEvent e){
        try{
        if(RoomWorlds.isRoomWorld(UUID.fromString(e.getPlayer().getLocation().getWorld().getName()))){
            final RoomWorld roomWorld= RoomWorlds.getRoomWorldUUID(UUID.fromString(e.getPlayer().getLocation().getWorld().getName()));
            if(!roomWorld.isMember(e.getPlayer())&&!roomWorld.isTrusted(e.getPlayer())&&!roomWorld.isOwner(e.getPlayer())){
                if(!e.getPlayer().hasPermission("housemanager.admin")){
                    e.setCancelled(true);
                }
            }
        }}
            catch (Exception exception){

    }
    }
    @EventHandler
    public void onFill(PlayerBucketFillEvent e){
        try{
            if(RoomWorlds.isRoomWorld(UUID.fromString(e.getPlayer().getLocation().getWorld().getName()))){
                final RoomWorld roomWorld= RoomWorlds.getRoomWorldUUID(UUID.fromString(e.getPlayer().getLocation().getWorld().getName()));
                if(!roomWorld.isMember(e.getPlayer())&&!roomWorld.isTrusted(e.getPlayer())&&!roomWorld.isOwner(e.getPlayer())){
                    if(!e.getPlayer().hasPermission("housemanager.admin")){
                        e.setCancelled(true);
                    }
                }
            }}
        catch (Exception exception){

        }
    }
}