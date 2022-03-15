package tel.endho.rooms;

import java.util.UUID;

public class GlobalRoomWorld {
    UUID worlduuid;
    String lastserver;
    String Ownername;
    Long systime;
    public GlobalRoomWorld(UUID worlduuid, String Ownername, String lastserver,Long systime){
        this.worlduuid=worlduuid;
        this.lastserver=lastserver;
        this.Ownername=Ownername;
        this.systime=systime;
    }
    public UUID getWorldUUID(){
        return this.worlduuid;
    }
    public String getOwnername(){
        return this.Ownername;
    }
    public String getLastserver(){return this.lastserver;}
    public long getSysTime( ){return this.systime;}
}
