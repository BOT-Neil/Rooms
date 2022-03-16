package tel.endho.rooms.Tasks;

import org.bukkit.Bukkit;
import org.bukkit.World;
import tel.endho.rooms.RoomWorlds;
import tel.endho.rooms.Rooms;

public class UnloadEmptyTask implements Runnable {

    @Override
    public void run(){
        RoomWorlds.getRoomWolrds().forEach((uuid, roomWorld) -> {
            if(Bukkit.getWorld(uuid.toString())!=null){
                World world = Bukkit.getWorld(uuid.toString());
                if(world!=null&&world.getPlayers().isEmpty()){
                    roomWorld.addInactiveTick();
                    if(roomWorld.getInactiveTicks()>6){
                        Rooms.roomWorldManager.unloadRoomWorld(roomWorld);
                        //RoomWorldManager.unloadRoomWorld(roomWorld);

                        //HouseWorlds.getHouseWolrds().remove(uuid,roomWorld);
                        //todo remove from globallist
                        //todo remove worldguard folder
                    }
                }else{
                    roomWorld.setInactiveTicks(0);
                }
            }
        });
    }
}
