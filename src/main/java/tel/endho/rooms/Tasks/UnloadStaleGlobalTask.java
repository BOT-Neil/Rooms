package tel.endho.rooms.Tasks;


import tel.endho.rooms.GlobalRoomWorlds;
import tel.endho.rooms.Rooms;


public class UnloadStaleGlobalTask implements Runnable{
    @Override
    public void run() {

        try {
            if(GlobalRoomWorlds.getGlobalRoomWorlds()!=null){
                GlobalRoomWorlds.getGlobalRoomWorlds().forEach((uuid, globalRoomWorld) -> {
                    if(globalRoomWorld.getSysTime()>System.currentTimeMillis()+30000){
                        GlobalRoomWorlds.removeGlobalRoomWorld(uuid);
                    }
                });
            }

            //Rooms.mysql.loadGlobalRoomWorlds();
        } catch (Exception e) {
            Rooms.logToConsole(e.toString());
        }
    }
}
