package tel.endho.rooms;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import tel.endho.rooms.storage.Redis;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class RoomWorld {
    private int rowid;
    private UUID uuid;
    private UUID OwnerUUID;
    private String Ownername;
    private String timestamp;
    private Integer spawnX;
    private Integer spawnY;
    private Integer spawnZ;
    private Map<UUID, String>blocked;
    private Map<UUID, String>trustedMembers;
    private Map<UUID, String>members;
    private String LastServer;
    private Long systemTime;
    private int inactiveTicks;
    private String enviroment;
    private String BorderColor;

    private Boolean hasNether;
    private Boolean hasEnd;//todo set true after island gen
    private String RoomName;//todo room.user.rename if string null || null { }
    private Material icon;

    public RoomWorld(int rowid, UUID uuid, UUID ownerUUID, String Ownername, String timestamp, Integer spawnX, Integer spawnY, Integer spawnZ,Map<UUID,String> blocked, Map<UUID, String> trustedMembers, Map<UUID, String> members, String enviroment, String borderColor){
        this.rowid=rowid;
        this.uuid = uuid;
        this.OwnerUUID=ownerUUID;
        this.Ownername=Ownername;
        this.timestamp = timestamp;
        this.spawnX=spawnX;
        this.spawnY=spawnY;
        this.spawnZ=spawnZ;
        this.blocked=blocked;
        this.trustedMembers=trustedMembers;
        this.members=members;
        this.LastServer=LastServer;
        this.systemTime=systemTime;
        this.enviroment=enviroment;
        this.BorderColor=borderColor;
        this.inactiveTicks=0;//noplayers in world
        this.hasNether=false;
    };
    public int getRowid(){return this.rowid;}
    public UUID getWorldUUID(){
        return this.uuid;
    }
    public UUID getOwnerUUID(){
        return this.OwnerUUID;
    }
    public String getOwnerName(){ return this.Ownername; }
    public String getTimestamp(){return this.timestamp;}
    public Map<UUID, String> getBlocked(){return this.blocked;}
    public Map<UUID, String> getMembers(){return this.members;}
    public Map<UUID, String> getTrustedMembers(){return this.trustedMembers;}
    public Integer getSpawnX(){return this.spawnX;}
    public Integer getSpawnY(){return this.spawnY;}
    public Integer getSpawnZ(){return this.spawnZ;}
    public Integer getOrderId(){
        RoomWorlds.getRoomWorldsPlayer(getOwnerUUID());
        ArrayList<RoomWorld> playerhouses = new ArrayList<>(RoomWorlds.getRoomWorldsPlayer(getOwnerUUID()).values());
        playerhouses.sort(Comparator.comparingInt(RoomWorld::getRowid));
        AtomicInteger x = new AtomicInteger();
        AtomicBoolean atomicBoolean = new AtomicBoolean();
        atomicBoolean.set(false);
        playerhouses.forEach(roomWorld1 -> {
            if(!atomicBoolean.get()){
                x.getAndAdd(1);
                if(roomWorld1.getWorldUUID().equals(getWorldUUID())){
                    atomicBoolean.set(true);
                }
            }

        });
        return x.get();
    }
    public Long getSystemTime(){return this.systemTime;}
    public void setSystemTime(Long systemTime){
        this.systemTime=systemTime;
    }
    public Boolean isLoaded(){
        return Bukkit.getWorld(uuid.toString()) != null;
        //return loaded;
    }
    public int getInactiveTicks(){return inactiveTicks;}
    public void setInactiveTicks(int inactiveTicks) {
        this.inactiveTicks = inactiveTicks;
    }
    public void addInactiveTick(){
        inactiveTicks= inactiveTicks + 1;
    }
    public String getEnviroment(){
        return this.enviroment;
    }
    public String getBorderColor(){return this.BorderColor;}
    public void setBorderColor(String borderColor){
        this.BorderColor=borderColor;
    }
    public Boolean isTrusted(Player player){
        return trustedMembers.containsKey(player.getUniqueId());
    }
    public Boolean isMember(Player player){
        return members.containsKey(player.getUniqueId());
    }
    public Boolean isOwner(Player player){return player.getUniqueId()==getOwnerUUID();}
    public Boolean isOwnerOnline() {return Bukkit.getOfflinePlayer(OwnerUUID).isOnline();}
    public Boolean getHasNether(){return hasNether;}
    public Boolean getHasEnd(){return hasEnd;}
    public void removeMember(UUID uuid){
        members.remove(uuid);
    }
    public void clearMembers(Boolean updateRedis){
        members.clear();
        if(updateRedis&&Rooms.configs.getStorageConfig().getBoolean("enabledredis")){
            Rooms.redis.clearMembers(getWorldUUID());
        }
    }
    public void setHasNether(Boolean bool){
        hasNether=bool;
    }
}