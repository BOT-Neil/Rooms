package tel.endho.rooms.storage;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.geysermc.floodgate.api.FloodgateApi;
import tel.endho.rooms.*;
import tel.endho.rooms.menusystem.bmenu.BRKVisitTargetRooms;
import tel.endho.rooms.menusystem.menu.VisitTargetRoomsMenu;
import tel.endho.rooms.util.Preset;
import tel.endho.rooms.util.enums.usergroup;

import javax.annotation.Nullable;
import java.sql.*;
import java.util.HashMap;
import java.util.UUID;

public class MySQL {
  private Connection connection;
  private String host, database, username, password;
  private int port;

  public void initmysql(String host, String database, String username, String password, int port)
      throws SQLException, ClassNotFoundException {
    this.host = host;
    this.database = database;
    this.username = username;
    this.password = password;
    this.port = port;
    openConnection();
  }

  public void openConnection() throws SQLException, ClassNotFoundException {
    if (connection != null && !connection.isClosed()) {
      return;
    }

    synchronized (this) {
      if (connection != null && !connection.isClosed()) {
        return;
      }
      String url = ("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?autoReconnect=true");
      Rooms.debug(url);
      connection = DriverManager.getConnection(url, this.username, this.password);

      createTable();
    }
  }

  //
  public void createTable() throws SQLException {
    String roomworlds = """
        CREATE TABLE `room_worlds` (
         `id` int(11) NOT NULL AUTO_INCREMENT,
         `worlduuid` varchar(40) NOT NULL,
         `owneruuid` varchar(40) NOT NULL,
         `ownername` varchar(40) NOT NULL,
         `timestamp` timestamp NOT NULL DEFAULT current_timestamp(),
         `spawnlocation` varchar(60) NOT NULL,
         `bordercolour` varchar(20) NOT NULL,
         `hasnether` tinyint(1) NOT NULL DEFAULT '0',
         `hasend` tinyint(1) NOT NULL DEFAULT '0',
         `roomname` varchar(100) NULL DEFAULT NULL,
         `icon` varchar(40) NULL DEFAULT NULL,
         `blocked` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '{}',
         `members` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '{}',
         `trusted` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '{}',
         `preset` varchar(40) NOT NULL,
         PRIMARY KEY (`id`)
        ) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8
        """;
    try {
      connection.prepareStatement(roomworlds).executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void loadRoomWorlds(Player player) throws SQLException {
    BukkitRunnable r = new BukkitRunnable() {
      @Override
      public void run() {
        try {
          PreparedStatement statement = connection.prepareStatement(
              "SELECT * FROM `room_worlds` WHERE `owneruuid` LIKE ? ORDER BY `id` ASC;");
          statement.setString(1, player.getUniqueId().toString());
          ResultSet result = statement.executeQuery();
          while (result.next()) {
            int rowid = result.getInt("id");
            UUID uuid = UUID.fromString(result.getString("worlduuid"));
            UUID OwnerUUID = player.getUniqueId();
            String Ownername = player.getName();
            String timestamp = result.getString("timestamp");
            String spawnlocation = result.getString("spawnlocation");
            String bordercolour = result.getString("bordercolour");
            Boolean hasnether = result.getBoolean("hasnether");// if nether-linked island is generated
            Boolean hasend = result.getBoolean("hasend");// if end-linked island is generated
            String roomname = result.getString("roomname");// todo add hex support for gui etc
            String iconMaterial = result.getString("icon");// todo add customizablity
            String preset = result.getString("preset");// selectable on creation only due to world type
            Gson gson = new GsonBuilder().create();
            HashMap<String, HashMap<UUID, String>> groupsMap = new HashMap<>();
            // todoinmysql add colum of users customgroups?
            HashMap<UUID, String> blocked = gson.fromJson(result.getString("blocked"),
                new TypeToken<HashMap<UUID, String>>() {
                }.getType());
            HashMap<UUID, String> trusted = gson.fromJson(result.getString("trusted"),
                new TypeToken<HashMap<UUID, String>>() {
                }.getType());
            HashMap<UUID, String> members = gson.fromJson(result.getString("members"),
                new TypeToken<HashMap<UUID, String>>() {
                }.getType());
            groupsMap.put("MEMBERS", members);
            groupsMap.put("TRUSTED", trusted);
            groupsMap.put("BLOCKED", blocked);
            if (!RoomWorlds.getRoomWolrds().containsKey(uuid) || !RoomWorlds.getRoomWorldUUID(uuid).isLoaded()) {
              RoomWorlds.addRoom(uuid, new RoomWorld(rowid, uuid, OwnerUUID, Ownername, timestamp, spawnlocation,
                  groupsMap, bordercolour, hasnether, hasend, roomname, iconMaterial, preset));
            }

          }
        } catch (SQLException e) {
          try {
            if (connection == null) {
              Rooms.debug("is null");
            } else {
              Rooms.debug("not null");
            }
            Rooms.debug("isconnection()" + connection.isClosed());
          } catch (SQLException ex) {

            ex.printStackTrace();
          }
          e.printStackTrace();
        }
      }
    };
    r.runTaskAsynchronously(Rooms.getPlugin());

  }

  // todo fix roomnumber direct tp? called from cmd only ie room v 69
  public void loadOthersRoomWorlds(Player player, String target, @Nullable Integer roomnumber) throws SQLException {
    BukkitRunnable r = new BukkitRunnable() {
      @Override
      public void run() {
        try {
          PreparedStatement statement = connection.prepareStatement(
              "SELECT * FROM `room_worlds` WHERE `owneruuid` LIKE ? ORDER BY `id` ASC;");
          statement.setString(1, player.getUniqueId().toString());
          ResultSet result = statement.executeQuery();
          while (result.next()) {
            int rowid = result.getInt("id");
            UUID uuid = UUID.fromString(result.getString("worlduuid"));
            UUID OwnerUUID = player.getUniqueId();
            String Ownername = player.getName();
            String timestamp = result.getString("timestamp");
            String spawnlocation = result.getString("spawnlocation");
            String bordercolour = result.getString("bordercolour");
            Boolean hasnether = result.getBoolean("hasnether");// if nether-linked island is generated
            Boolean hasend = result.getBoolean("hasend");// if end-linked island is generated
            String roomname = result.getString("roomname");// todo add hex support for gui etc
            String iconMaterial = result.getString("icon");// todo add customizablity
            String preset = result.getString("preset");// selectable on creation only due to world type
            Gson gson = new GsonBuilder().create();
            HashMap<String, HashMap<UUID, String>> groupsMap = new HashMap<>();
            // todoinmysql add colum of users customgroups?
            HashMap<UUID, String> blocked = gson.fromJson(result.getString("blocked"),
                new TypeToken<HashMap<UUID, String>>() {
                }.getType());
            HashMap<UUID, String> trusted = gson.fromJson(result.getString("trusted"),
                new TypeToken<HashMap<UUID, String>>() {
                }.getType());
            HashMap<UUID, String> members = gson.fromJson(result.getString("members"),
                new TypeToken<HashMap<UUID, String>>() {
                }.getType());
            groupsMap.put("MEMBERS", members);
            groupsMap.put("TRUSTED", trusted);
            groupsMap.put("BLOCKED", blocked);
            if (!RoomWorlds.getRoomWolrds().containsKey(uuid) || !RoomWorlds.getRoomWorldUUID(uuid).isLoaded()) {
              RoomWorlds.addRoom(uuid, new RoomWorld(rowid, uuid, OwnerUUID, Ownername, timestamp, spawnlocation,
                  groupsMap, bordercolour, hasnether, hasend, roomname, iconMaterial, preset));
            }

          }
          if (target != null) {
            Rooms.debug("debugf");
            if (FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) {
              Rooms.debug("debugflood");
              new BRKVisitTargetRooms().makemenu(player, target);
            } else {
              Rooms.debug("debugj");
              BukkitRunnable run = new BukkitRunnable() {
                @Override
                public void run() {
                  Rooms.debug("debugjj");
                  new VisitTargetRoomsMenu(Rooms.getPlayerMenuUtility(player), target).open();
                }
              };
              run.runTask(Rooms.getPlugin());
              // new MainMenu(Rooms.getPlayerMenuUtility(p)).open();
            }
          }

        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    };
    r.runTaskAsynchronously(Rooms.getPlugin());

  }

  // todo timestamp from plot long
  public void insertMigratedRoomWorld(UUID OwnerUUID, String Ownername, SlimeWorld world, SlimePropertyMap sprop,
      HashMap<String, HashMap<UUID, String>> groupsMap, String timeString) throws SQLException {
    BukkitRunnable r = new BukkitRunnable() {
      @Override
      public void run() {
        try {
          UUID uuid = UUID.fromString(world.getName());
          String x = sprop.getValue(SlimeProperties.SPAWN_X).toString();
          String y = sprop.getValue(SlimeProperties.SPAWN_Y).toString();
          String z = sprop.getValue(SlimeProperties.SPAWN_Z).toString();
          String spawnlocation = x + ";" + y + ";" + z + ";" + "0;" + "0";
          Gson gson = new GsonBuilder().create();
          String blocked = gson.toJson(groupsMap.get(usergroup.BLOCKED.name()));
          String members = gson.toJson(groupsMap.get(usergroup.TRUSTED.name()));
          String trusted = gson.toJson(groupsMap.get(usergroup.MEMBER.name()));
          String bordercolour = Rooms.configs.getGeneralConfig().getString("bordercolour");
          PreparedStatement stmt = connection.prepareStatement(
              "INSERT INTO `room_worlds` (`id`, `worlduuid`, `owneruuid`, `ownername`, `timestamp`, `spawnlocation`, `preset`, `blocked`, `members`, `trusted`, `bordercolour`) VALUES (NULL, ?, ?, ?, current_timestamp(), ?, ?, ?, ?, ?, ?);");
          stmt.setString(1, world.getName());
          stmt.setString(2, OwnerUUID.toString());
          stmt.setString(3, Ownername);
          stmt.setString(4, spawnlocation);
          stmt.setString(5, "normal");
          stmt.setString(6, blocked);
          stmt.setString(7, members);
          stmt.setString(8, trusted);
          stmt.setString(9, Rooms.configs.getGeneralConfig().getString("bordercolour"));
          stmt.executeUpdate();
          PreparedStatement stmt2 = connection.prepareStatement(
              "SELECT `id`, `ownername`, `timestamp` FROM `room_worlds` WHERE `worlduuid` = ?");
          stmt2.setString(1, world.getName());
          ResultSet result = stmt2.executeQuery();
          if (result.next()) {
            int rowid = result.getInt("id");
            //String ownername = result.getString("owner");
            String timestamp = result.getString("timestamp");
            if (!RoomWorlds.getRoomWolrds().containsKey(uuid) || !RoomWorlds.getRoomWorldUUID(uuid).isLoaded()) {
              RoomWorlds.addRoom(uuid, new RoomWorld(rowid, uuid, OwnerUUID, Ownername, timestamp, spawnlocation,
                  groupsMap, bordercolour, false, false, null, "GRASS_BLOCK", Rooms.configs.getGeneralConfig().getString("defaultpreset")));
            }
          }

        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    };
    r.runTaskAsynchronously(Rooms.getPlugin());

  }

  // fix data
  public void insertRoomWorld(Player player, SlimeWorld world, SlimePropertyMap sprop, Preset preset)
      throws SQLException {
    BukkitRunnable r = new BukkitRunnable() {
      @Override
      public void run() {
        try {
          UUID uuid = UUID.fromString(world.getName());
          String x = sprop.getValue(SlimeProperties.SPAWN_X).toString();
          String y = sprop.getValue(SlimeProperties.SPAWN_Y).toString();
          String z = sprop.getValue(SlimeProperties.SPAWN_Z).toString();
          String spawnlocation = x + ";" + y + ";" + z + ";" + "0;" + "0";
          String bordercolour = Rooms.configs.getGeneralConfig().getString("bordercolour");
          String Ownername=player.getName();
          UUID OwnerUUID=player.getUniqueId();
          PreparedStatement stmt = connection.prepareStatement(
              "INSERT INTO `room_worlds` (`id`, `worlduuid`, `owneruuid`, `ownername`, `timestamp`, `spawnlocation`, `preset`, `bordercolour`) VALUES (NULL, ?, ?, ?, current_timestamp(), ?, ?, ?);");
          stmt.setString(1, world.getName());
          stmt.setString(2, OwnerUUID.toString());
          stmt.setString(3, Ownername);
          stmt.setString(4, spawnlocation);
          stmt.setString(5, "normal");
          stmt.setString(6, Rooms.configs.getGeneralConfig().getString("bordercolour"));
          stmt.executeUpdate();
          PreparedStatement stmt2 = connection.prepareStatement(
              "SELECT `id`, `ownername`, `timestamp` FROM `room_worlds` WHERE `worlduuid` = ?");
          stmt2.setString(1, world.getName());
          ResultSet result = stmt2.executeQuery();
          if (result.next()) {
            int rowid = result.getInt("id");
            // String ownername = result.getString("owner");
            String timestamp = result.getString("timestamp");
            HashMap<String, HashMap<UUID, String>> groupsMap= new HashMap<>();
            HashMap<UUID, String> blocked = new HashMap<>();
            HashMap<UUID, String> trusted = new HashMap<>();
            HashMap<UUID, String> members = new HashMap<>();
            groupsMap.put("MEMBERS", members);
            groupsMap.put("TRUSTED", trusted);
            groupsMap.put("BLOCKED", blocked);
            if (!RoomWorlds.getRoomWolrds().containsKey(uuid) || !RoomWorlds.getRoomWorldUUID(uuid).isLoaded()) {
              RoomWorlds.addRoom(uuid, new RoomWorld(rowid, uuid, OwnerUUID, Ownername, timestamp, spawnlocation,
                  groupsMap, bordercolour, false, false, null, "GRASS_BLOCK",
                  Rooms.configs.getGeneralConfig().getString("defaultpreset")));
            }
          }

        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    };
    r.runTaskAsynchronously(Rooms.getPlugin());

  }

  public void saveRoomWorld(RoomWorld roomWorld, Boolean async) throws SQLException {
    BukkitRunnable r = new BukkitRunnable() {
      @Override
      public void run() {
        try {
          Gson gson = new GsonBuilder().create();
          String blocked = gson.toJson(roomWorld.getBlocked());
          String trusted = gson.toJson(roomWorld.getTrustedMembers());
          String members = gson.toJson(roomWorld.getMembers());
          PreparedStatement stmt = connection.prepareStatement(
              "UPDATE `room_worlds` SET worlduuid=? ,owneruuid=? ,ownername=? ,spawnlocation=?, roomname=?, preset=? ,bordercolour=?, blocked=?, members=?, trusted=? WHERE `id` = ?;");
          stmt.setString(1, roomWorld.getWorldUUID().toString());
          stmt.setString(2, roomWorld.getOwnerUUID().toString());
          stmt.setString(3, roomWorld.getOwnerName());
          stmt.setString(4, roomWorld.getSpawnString());
          stmt.setString(5, roomWorld.getRoomsName());
          stmt.setString(6, roomWorld.getPreset().getName());
          stmt.setString(7, roomWorld.getBorderColor());
          stmt.setString(8, blocked);
          stmt.setString(9, members);
          stmt.setString(10, trusted);
          stmt.setInt(11, roomWorld.getRowid());
          Rooms.debug(stmt.toString());
          stmt.executeUpdate();
          // todo make sync instead of sync for shutdown
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    };
    if (async) {
      r.runTaskAsynchronously(Rooms.getPlugin());
    } else {
      r.runTask(Rooms.getPlugin());
    }

  }

  public void deleteRoom(UUID worlduuid) {
    BukkitRunnable r = new BukkitRunnable() {
      @Override
      public void run() {
        try {
          PreparedStatement purgestmt = connection.prepareStatement(
              "DELETE FROM `room_worlds` WHERE `worlduuid` = `?`;");
          purgestmt.setString(1, worlduuid.toString());
          purgestmt.executeUpdate();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    };
    r.runTaskAsynchronously(Rooms.getPlugin());

  }

  // todo untrust member
  /*
   * public void insertGlobalRoomWorld(RoomWorld houseWorld) throws SQLException {
   * BukkitRunnable r = new BukkitRunnable() {
   * 
   * @Override
   * public void run() {
   * try {
   * PreparedStatement stmt = connection.prepareStatement(
   * "INSERT INTO `room_global` (`worlduuid`, `lastserver`, `owner_name`) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE lastserver=?, owner_name=?,lastupdate=CURRENT_TIMESTAMP;"
   * );
   * stmt.setString(1, houseWorld.getWorldUUID().toString());
   * stmt.setString(2, bungeesrvname);
   * stmt.setString(3, houseWorld.getOwnerName());
   * stmt.setString(4, bungeesrvname);
   * stmt.setString(5, houseWorld.getOwnerName());
   * //Rooms.debug("sqlstatement: "+stmt);
   * stmt.executeUpdate();
   * } catch (SQLException e) {
   * e.printStackTrace();
   * }
   * }
   * };
   * r.runTaskAsynchronously(Rooms.getPlugin());
   * 
   * }
   */
  /*
   * public void loadGlobalRoomWorlds() throws SQLException {
   * BukkitRunnable r = new BukkitRunnable() {
   * 
   * @Override
   * public void run() {
   * try {
   * if(Rooms.configs.getMySQLConfig().getBoolean("purgeglobaltask")){
   * PreparedStatement purgestmt = connection.prepareStatement(
   * "DELETE FROM `room_global` WHERE `lastupdate` < NOW() - interval 60 SECOND;"
   * );
   * purgestmt.executeUpdate();
   * }
   * PreparedStatement statement = connection.prepareStatement(
   * "SELECT * FROM `room_global`;"
   * );
   * ResultSet result = statement.executeQuery();
   * HashMap<UUID, GlobalRoomWorld> houseWorldBungeeInfoArrayList = new
   * HashMap<>();
   * while (result.next()) {
   * UUID uuid = UUID.fromString(result.getString("worlduuid"));
   * String Ownername = result.getString("owner_name");
   * String lastserver = result.getString("lastserver");
   * houseWorldBungeeInfoArrayList.putIfAbsent(uuid,new
   * GlobalRoomWorld(uuid,Ownername,lastserver));
   * }
   * GlobalRoomWorlds.globalRoomWorldHashMap=houseWorldBungeeInfoArrayList;
   * } catch (SQLException e) {
   * e.printStackTrace();
   * }
   * }
   * };
   * r.runTaskAsynchronously(Rooms.getPlugin());
   * 
   * }
   */
  public void close() throws SQLException {
    connection.close();
  }
}
