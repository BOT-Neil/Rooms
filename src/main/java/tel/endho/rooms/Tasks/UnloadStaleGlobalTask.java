package tel.endho.rooms.Tasks;


import tel.endho.rooms.GlobalRoomWorlds;


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
            e.printStackTrace();
        }
    }
}
