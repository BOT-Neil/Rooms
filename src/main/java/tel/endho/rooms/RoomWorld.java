package tel.endho.rooms;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import tel.endho.rooms.util.LocationSerializer;
import tel.endho.rooms.util.Preset;
import tel.endho.rooms.util.Presets;
import tel.endho.rooms.util.enums.usergroup;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nullable;

public class RoomWorld {
  private int rowid;
  private UUID uuid;
  private UUID OwnerUUID;
  private String Ownername;
  private String timestamp;
  private String spawnLocation;
  private HashMap<String, HashMap<UUID, String>> groupsMap= new HashMap<>();
  private int inactiveTicks;// temp
  private String preset;// presetconfig options
  private String BorderColor;// red, blue, green
  private Boolean hasNether;//
  private Boolean hasEnd;// todo set true after island gen
  private String roomName;// todo room.user.rename if string null || null { }
  private Material icon;

  public RoomWorld(int rowid, UUID uuid, UUID ownerUUID, String ownername, String timestamp, String spawnlocation,
      HashMap<String, HashMap<UUID, String>> groupsMap, String bordercolour, Boolean hasNether, Boolean hasEnd,
      @Nullable String roomName,
      String icon, String preset) {
    this.rowid = rowid;
    this.uuid = uuid;
    this.OwnerUUID = ownerUUID;
    this.Ownername = ownername;
    this.timestamp = timestamp;
    this.spawnLocation = spawnlocation;
    this.groupsMap = groupsMap;
    this.BorderColor = bordercolour;
    this.hasNether = hasNether;
    this.hasEnd = hasEnd;
    this.roomName = roomName;
    this.icon = Material.getMaterial(icon);
    this.preset = preset;
  }

  public int getRowid() {
    return this.rowid;
  }

  public UUID getWorldUUID() {
    return this.uuid;
  }

  public UUID getOwnerUUID() {
    return this.OwnerUUID;
  }

  public String getOwnerName() {
    return this.Ownername;
  }

  public String getTimestamp() {
    return this.timestamp;
  }

  public HashMap<String, HashMap<UUID, String>> getGroupMap() {
    return this.groupsMap;
  }

  public Map<UUID, String> getUserGroup(String string) {// doesnt look good
    return getGroupMap().get(string);
  }

  public Map<UUID, String> getBlocked() {
    return this.groupsMap.getOrDefault(usergroup.BLOCKED.toString(), new HashMap<>());
    // return getGroupMap().get(usergroup.BLOCKED.toString());
  }

  // public Map<UUID, String> getBlocked(){return this.blocked;}
  public Map<UUID, String> getMembers() {
    return this.groupsMap.getOrDefault(usergroup.MEMBER.toString(), new HashMap<>());
    // return getGroupMap().get(usergroup.MEMBER.toString());
  }

  public Map<UUID, String> getTrustedMembers() {
    //return this.groupsMap.getOrDefault(usergroup.TRUSTED.toString(), new HashMap<>());
    this.groupsMap.keySet().forEach(key->{
      System.out.println("key+"+key);
    });
    return getGroupMap().get(usergroup.TRUSTED.toString());
  }

  // todo getcustommembers(?)
  public Location getSpawnLocation() {
    return LocationSerializer.getDeserializedPresetLocation(this.spawnLocation, this.uuid.toString());
  }

  public String getSpawnString() {
    return this.spawnLocation;
  }

  public String getRoomName() {
    return this.roomName;
  }

  public Integer getSpawnX() {
    return LocationSerializer.getDeserializedXYZ(this.spawnLocation)[0];
  }

  public Integer getSpawnY() {
    return LocationSerializer.getDeserializedXYZ(this.spawnLocation)[1];
  }

  public Integer getSpawnZ() {
    return LocationSerializer.getDeserializedXYZ(this.spawnLocation)[2];
  }

  public Integer getOrderId() {
    RoomWorlds.getRoomWorldsPlayer(getOwnerUUID());
    ArrayList<RoomWorld> playerhouses = new ArrayList<>(RoomWorlds.getRoomWorldsPlayer(getOwnerUUID()).values());
    playerhouses.sort(Comparator.comparingInt(RoomWorld::getRowid));
    AtomicInteger x = new AtomicInteger();
    AtomicBoolean atomicBoolean = new AtomicBoolean();
    atomicBoolean.set(false);
    playerhouses.forEach(roomWorld1 -> {
      if (!atomicBoolean.get()) {
        x.getAndAdd(1);
        if (roomWorld1.getWorldUUID().equals(getWorldUUID())) {
          atomicBoolean.set(true);
        }
      }

    });
    return x.get();
  }

  public Boolean isLoaded() {
    return Bukkit.getWorld(uuid.toString()) != null;
    // return loaded;
  }

  public int getInactiveTicks() {
    return inactiveTicks;
  }

  public void setInactiveTicks(int inactiveTicks) {
    this.inactiveTicks = inactiveTicks;
  }

  public void addInactiveTick() {
    inactiveTicks = inactiveTicks + 1;
  }

  public String getEnviroment() {
    // return this.preset;
    return Presets.getPreset(this.preset).getmainEnvironment();
  }

  public String getBorderColor() {
    return this.BorderColor;
  }

  public void setBorderColor(String borderColor) {
    this.BorderColor = borderColor;
  }

  public Boolean isTrusted(Player player) {
    return this.getTrustedMembers().containsKey(player.getUniqueId());
  }

  public Boolean isMember(Player player) {
    return this.getMembers().containsKey(player.getUniqueId());
  }

  public Boolean isOwner(Player player) {
    return player.getUniqueId() == getOwnerUUID();
  }

  public Boolean isOwnerOnline() {
    return Bukkit.getOfflinePlayer(OwnerUUID).isOnline();
  }

  public Boolean getHasNether() {
    return hasNether;
  }

  public Boolean getHasEnd() {
    return hasEnd;
  }

  public void removeMember(UUID uuid) {
    getMembers().remove(uuid);
  }

  public void clearMembers(Boolean updateRedis) {
    getMembers().clear();
    if (updateRedis && Rooms.configs.getStorageConfig().getBoolean("enabledredis")) {
      Rooms.redis.clearMembers(getWorldUUID());
    }
  }

  public void setHasNether(Boolean bool) {
    hasNether = bool;
  }

  public Preset getPreset() {
    return Presets.getPreset(preset);
  }

  public String getRoomsName() {
    return roomName;
  }

  public void setIconMaterial(Material material) {
    this.icon = material;
  }

  public Material getIcon() {
    return this.icon;
  }
}
