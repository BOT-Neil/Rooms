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

public class PortalListener implements Listener {
    @EventHandler
    public void onPortal(PlayerPortalEvent portalEvent){
        Rooms.getPlugin().getLogger().info("debug1f");
        if(portalEvent.isCancelled()){
            return;
        }
        Rooms.getPlugin().getLogger().info("debug2f");
        if(!Rooms.configs.getGeneralConfig().getBoolean("islandmode")){
            return;
        }
        Rooms.getPlugin().getLogger().info("debug3f");
        Location currentLocation = portalEvent.getFrom();
        if (currentLocation.getWorld()!=null&&!RoomWorlds.isRoomWorld(currentLocation.getWorld().getName())) {
            Rooms.getPlugin().getLogger().info("currentlocation: "+ currentLocation);
            return;
        }

        Rooms.getPlugin().getLogger().info("debug4f");
        PortalType type;
        if (portalEvent.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL) {
            type = PortalType.ENDER;
        } else if (portalEvent.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            type = PortalType.NETHER;
        } else {
            return;
        }
        RoomWorld roomWorld = RoomWorlds.getRoomWorldString(currentLocation.getWorld().getName());
        Location newlocation;
        if(type.equals(PortalType.NETHER)){
            if(currentLocation.getWorld().getName().endsWith("rmnether")){
                newlocation= new Location(Bukkit.getWorld(roomWorld.getWorldUUID().toString()),1,70,1);
                portalEvent.setTo(newlocation);
            }else {
                if(roomWorld.getHasNether()){
                    newlocation= new Location(Bukkit.getWorld(roomWorld.getWorldUUID().toString()+"rmnether"),1,70,1);
                    portalEvent.setTo(newlocation);
                }else{
                    Rooms.roomWorldManager.genNether(roomWorld, portalEvent.getPlayer());
                    //roomworldmanager.createNether(roomWorld, event.getPlayer()
                }
            }
        }
        if(type.equals(PortalType.ENDER)){
            if(currentLocation.getWorld().getName().endsWith("rmend")){
                newlocation= new Location(Bukkit.getWorld(roomWorld.getWorldUUID().toString()),1,70,1);
                portalEvent.setTo(newlocation);
            }else {
                newlocation= new Location(Bukkit.getWorld(roomWorld.getWorldUUID().toString()+"rmend"),1,70,1);
                portalEvent.setTo(newlocation);
            }
        }

    }
    @EventHandler
    public void onPortal(EntityPortalEnterEvent portalEvent) throws CorruptedWorldException, NewerFormatException, WorldInUseException, UnknownWorldException, IOException {
        Rooms.getPlugin().getLogger().info("debug1");
        if (!(portalEvent.getEntity() instanceof Player)) {
            return;
        }
        Player p = (Player) portalEvent.getEntity();
        Rooms.getPlugin().getLogger().info("debug2");
        if(!Rooms.configs.getGeneralConfig().getBoolean("islandmode")){
            return;
        }
        Rooms.getPlugin().getLogger().info("debug3");
        Location currentLocation = portalEvent.getLocation();
        if (currentLocation.getWorld()!=null&&!RoomWorlds.isRoomWorld(currentLocation.getWorld().getName())) {
            return;
        }
        Rooms.getPlugin().getLogger().info("debug4");
        PortalType type;
        if (currentLocation.getBlock().getType() == Material.END_PORTAL) {
            type = PortalType.ENDER;
        } else if (currentLocation.getBlock().getType() == Material.NETHER_PORTAL) {
            type = PortalType.NETHER;
        } else {
            return;
        }
        Rooms.getPlugin().getLogger().info("debug5");
        RoomWorld roomWorld = RoomWorlds.getRoomWorldString(currentLocation.getWorld().getName());
        Location newlocation;
        if(type.equals(PortalType.NETHER)){
            Rooms.getPlugin().getLogger().info("debug6");
            if(currentLocation.getWorld().getName().endsWith("rmnether")){
                Rooms.getPlugin().getLogger().info("debug7");
                newlocation= new Location(Bukkit.getWorld(roomWorld.getWorldUUID().toString()),1,70,1);
                Rooms.roomWorldManager.TpOrLoadHouseWorld(p,roomWorld.getWorldUUID());
                //p.teleport(newlocation);
            }else {
                if(roomWorld.getHasNether()){
                    newlocation= new Location(Bukkit.getWorld(roomWorld.getWorldUUID().toString()+"rmnether"),1,70,1);
                    p.teleport(newlocation);
                }else{
                    Rooms.getPlugin().getLogger().info("debug9");
                    Rooms.roomWorldManager.genNether(roomWorld, p);
                    //roomworldmanager.createNether(roomWorld, event.getPlayer()
                }
            }
        }
        if(type.equals(PortalType.ENDER)){

        }

    }
    @EventHandler
    public void EntityPortal(EntityPortalEvent event){
        if(event.isCancelled()){
            return;
        }
        //Entity e = event.getEntity();
        Location originalTo = event.getTo();
        Location currentLocation = event.getFrom();
        Rooms.getPlugin().getLogger().info("currentLocation: "+currentLocation.toString());
        assert originalTo != null;
        Rooms.getPlugin().getLogger().info("originalto: "+originalTo.toString());
        if (currentLocation.getWorld()!=null&&!RoomWorlds.isRoomWorld(currentLocation.getWorld().getName())) {
            return;
        }

        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        //Player p = (Player) event.getEntity();
    }
}
