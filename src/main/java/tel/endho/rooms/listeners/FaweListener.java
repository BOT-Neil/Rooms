package tel.endho.rooms.listeners;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import tel.endho.rooms.RoomWorld;
import tel.endho.rooms.RoomWorldManager;
import tel.endho.rooms.RoomWorlds;
import tel.endho.rooms.Rooms;

import java.util.UUID;

public class FaweListener {
    public void startListening() {
        WorldEdit.getInstance().getEventBus().register(new Object() /* [1] */ {
            // Make sure you import WorldEdit's @Subscribe!
            @Subscribe
            public void onEditSessionEvent(EditSessionEvent event) {
                Actor actor = event.getActor();
                if(event.getWorld()!=null&&RoomWorldManager.isValidUUID(event.getWorld().getName())&&RoomWorlds.isRoomWorld(UUID.fromString(event.getWorld().getName()))){
                    RoomWorld roomWorld = RoomWorlds.getRoomWorldUUID(UUID.fromString(event.getWorld().getName()));
                    if (actor != null && actor.isPlayer()) {
                        Player player = Bukkit.getPlayer(actor.getName());
                        if(player!=null){
                            if(actor.getSession()!=null){
                                LocalSession session = actor.getSession();
                                if(event.getStage().equals(EditSession.Stage.BEFORE_REORDER)){
                                    if(session.getClipboard()!=null){
                                        if(!session.getClipboard().getClipboards().isEmpty()){
                                            Clipboard clipboard= session.getClipboard().getClipboards().stream().iterator().next();
                                            //int height =clipboard.getHeight();
                                            int length =clipboard.getLength();
                                            BlockVector3 placement = session.getPlacementPosition(actor);
                                            int absX = Math.abs(placement.getX());
                                            int absZ = Math.abs(placement.getZ());
                                            int totalX = absX+length;
                                            int totalZ = absZ+length;
                                            if(Bukkit.getWorld(event.getWorld().getName()).getWorldBorder()==null){
                                                event.setCancelled(true);
                                            }else{
                                                double bordersize = Bukkit.getWorld(event.getWorld().getName()).getWorldBorder().getSize();
                                                int limit = (int) (bordersize/2);
                                                if(totalX>limit||totalZ>limit){
                                                    event.setCancelled(true);
                                                }
                                            }

                                        }
                                    }
                                }
                            }
                            if(!roomWorld.isOwner(player)&&!roomWorld.isTrusted(player)){
                                event.setCancelled(true);
                            }
                        }

                    }

                }

            }
        });
    }
}