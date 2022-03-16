package tel.endho.rooms.Tasks;

import tel.endho.rooms.RoomWorldManager;
import tel.endho.rooms.RoomWorlds;

public class WorldBorderTask implements Runnable {
    @Override
    public void run(){
        RoomWorlds.getRoomWolrds().values().forEach(RoomWorldManager::updateBorder);
    }

}
