package tel.endho.rooms.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.*;
import tel.endho.rooms.RoomWorlds;


public class EntitiyListener implements Listener {

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent event){
        try{
        if(RoomWorlds.isRoomWorld(event.getLocation().getWorld().getName())){
            if(event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.EGG)){
                event.setCancelled(true);
            }

        }}catch(Exception ex){}
    }
    @EventHandler
    public void onMobSpawn(SpawnerSpawnEvent event){
        try{
        if(RoomWorlds.isRoomWorld(event.getLocation().getWorld().getName())){
            event.setCancelled(true);
        }}catch (Exception exception){}
    }
    @EventHandler
    public void onMobSpawn(EntitySpawnEvent event){
        try{
        if(RoomWorlds.isRoomWorld(event.getLocation().getWorld().getName())){
            //if(event)
            event.setCancelled(true);
        }}catch(Exception ex){}
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBlockExplode(BlockExplodeEvent explodeEvent){
        try{
        if(RoomWorlds.isRoomWorld(explodeEvent.getBlock().getLocation().getWorld().getName())){
            explodeEvent.setCancelled(true);
        }}catch(Exception ex){}
    }
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBlockExplode(EntityExplodeEvent explodeEvent){
        try{
        if(RoomWorlds.isRoomWorld(explodeEvent.getLocation().getWorld().getName())){
            explodeEvent.setCancelled(true);
        }}catch(Exception ex){}
    }
    @EventHandler
    public void onSplashPotion(PotionSplashEvent e){
        try{
        if(RoomWorlds.isRoomWorld(e.getEntity().getLocation().getWorld().getName())){
            //add config
            e.setCancelled(true);
        }}catch(Exception ex){}
    }
    @EventHandler
    public void onThrow(ProjectileLaunchEvent e){
        try{
        if(RoomWorlds.isRoomWorld(e.getEntity().getLocation().getWorld().getName())){
            //add config
            e.setCancelled(true);
        }}catch(Exception ex){}
    }
}
