package tel.endho.rooms.Tasks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import tel.endho.rooms.RoomWorldManager;
import tel.endho.rooms.RoomWorlds;
import tel.endho.rooms.Rooms;

public class WorldBorderTask implements Runnable {
    @Override
    public void run(){
        RoomWorlds.getRoomWolrds().values().forEach(RoomWorldManager::updateBorder);
    }

}
