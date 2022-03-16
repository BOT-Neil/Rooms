package tel.endho.rooms.Tasks;

import tel.endho.rooms.RoomWorlds;
import tel.endho.rooms.Rooms;



public class UpdateGlobalTask implements Runnable{
    @Override
    public void run() {
        RoomWorlds.getRoomWolrds().forEach((uuid, roomWorld) -> {
            if(roomWorld.isLoaded()){
                Rooms.redis.insertGlobal(roomWorld);
            }
        });

    }
}
