package tel.endho.rooms.storage;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.geysermc.floodgate.api.FloodgateApi;
import tel.endho.rooms.*;
import tel.endho.rooms.menusystem.bmenu.BRKMainMenu;
import tel.endho.rooms.menusystem.bmenu.BRKVisitRooms;
import tel.endho.rooms.menusystem.bmenu.BRKVisitTargetRooms;
import tel.endho.rooms.menusystem.menu.MainMenu;
import tel.endho.rooms.menusystem.menu.VisitTargetRoomsMenu;

import javax.annotation.Nullable;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MySQL {
    private Connection connection;
    private String bungeesrvname, host, database, username, password;
    private int port;

    public void initmysql(String bungeesrvname, String host, String database, String username, String password, int port) throws SQLException, ClassNotFoundException {
        this.bungeesrvname = bungeesrvname;
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
            //Class.forName("com.mysql.cj.jdbc.Driver");
            String url = ("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?autoReconnect=true");
            //connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?autoReconnect=true", this.username, this.password);
            System.out.println(url);
            connection = DriverManager.getConnection(url,this.username,this.password);

            createTable();
        }
    }/*
    public void redis() {
        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                if (connection != null && !connection.isClosed()) {
                    return;
                }
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?autoReconnect=true", this.username, this.password);

                    createTable();
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }
        };
        r.runTaskAsynchronously(Rooms.getPlugin());
    }*/
    public void createTable() throws SQLException {
        String roomworld = """
                CREATE TABLE IF NOT EXISTS `room_worlds` (
                 `id` int(11) NOT NULL AUTO_INCREMENT,
                 `worlduuid` varchar(40) NOT NULL,
                 `owneruuid` varchar(40) NOT NULL,
                 `owner` varchar(40) NOT NULL,
                 `timestamp` timestamp NOT NULL DEFAULT current_timestamp(),
                 `x` int(5) NOT NULL,
                 `y` int(5) NOT NULL,
                 `z` int(5) NOT NULL,
                 `enviroment` varchar(20) NOT NULL,
                 `bordercolour` varchar(20) NOT NULL,
                 PRIMARY KEY (`id`)
                ) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8
                """;
        String blocked = """
                CREATE TABLE IF NOT EXISTS `room_blocked` (
                 `roomid` int(10) NOT NULL,
                 `player_uuid` varchar(40) NOT NULL,
                 `player_name` varchar(20) NOT NULL,
                 UNIQUE KEY `roomid_foreignkey` (`roomid`),
                 CONSTRAINT `roomid_blocked_foreignkey` FOREIGN KEY (`roomid`) REFERENCES `room_worlds` (`id`)
                ) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4""";
        String members = """
                CREATE TABLE IF NOT EXISTS `room_members` (
                 `roomid` int(10) NOT NULL,
                 `player_uuid` varchar(40) NOT NULL,
                 `player_name` varchar(20) NOT NULL,
                 UNIQUE KEY `roomid_foreignkey` (`roomid`),
                 CONSTRAINT `roomidd_foreignkey` FOREIGN KEY (`roomid`) REFERENCES `room_worlds` (`id`)
                ) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4""";
        String trustedmembers = """
                CREATE TABLE IF NOT EXISTS `room_trustedmembers` (
                 `roomid` int(10) NOT NULL,
                 `player_uuid` varchar(40) NOT NULL,
                 `player_name` varchar(20) NOT NULL,
                 KEY `roomid_foreignkey` (`roomid`),
                 CONSTRAINT `roomid_foreignkey` FOREIGN KEY (`roomid`) REFERENCES `room_worlds` (`id`)
                ) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4""";
        String roomsglobal = """
                CREATE TABLE IF NOT EXISTS `room_global` (
                 `worlduuid` varchar(40) NOT NULL,
                 `lastserver` varchar(20) NOT NULL,
                 `owner_name` varchar(20) NOT NULL,
                 `lastupdate` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
                 UNIQUE KEY `worlduuid` (`worlduuid`)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4""";
        try {
            connection.prepareStatement(roomworld).executeUpdate();
            connection.prepareStatement(blocked).executeUpdate();
            connection.prepareStatement(members).executeUpdate();
            connection.prepareStatement(trustedmembers).executeUpdate();
            connection.prepareStatement(roomsglobal).executeUpdate();
            // connection.prepareStatement(sql).executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void loadOthersRoomWorlds(Player player, String target, @Nullable Integer roomnumber) throws SQLException {
        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    PreparedStatement statement = connection.prepareStatement(
                            "SELECT * FROM `room_worlds` WHERE `owner` LIKE ? ORDER BY `id` ASC;"
                    );
                    statement.setString(1,target);
                    ResultSet result = statement.executeQuery();
                    while (result.next()) {
                        int rowid = result.getInt("id");

                        UUID uuid = UUID.fromString(result.getString("worlduuid"));
                        System.out.println(uuid.toString());
                        UUID OwnerUUID = UUID.fromString(result.getString("owneruuid"));
                        String Ownername = result.getString("owner");
                        String locktime = result.getString("timestamp");
                        Integer spawnX = result.getInt("x");
                        Integer spawnY = result.getInt("y");
                        Integer spawnZ = result.getInt("z");
                        Map<UUID, String> blockedMembers = new HashMap<>();
                        PreparedStatement blockedstmt = connection.prepareStatement(
                                "SELECT * FROM `room_blocked` WHERE `roomid` = ?;"
                        );
                        blockedstmt.setInt(1, rowid);
                        ResultSet blockedresult = blockedstmt.executeQuery();
                        while(blockedresult.next()){
                            UUID trustuuid= UUID.fromString(result.getString("player_uuid"));
                            String name= result.getString("player_name");
                            blockedMembers.put(trustuuid,name);
                        }
                        Map<UUID, String> trustedMembers = new HashMap<>();
                        PreparedStatement trustedmembersstmt = connection.prepareStatement(
                                "SELECT * FROM `room_trustedmembers` WHERE `roomid` = ?;"
                        );
                        trustedmembersstmt.setInt(1, rowid);
                        ResultSet trustedresult = trustedmembersstmt.executeQuery();
                        while(trustedresult.next()){
                            UUID trustuuid= UUID.fromString(result.getString("player_uuid"));
                            String name= result.getString("player_name");
                            trustedMembers.put(trustuuid,name);
                        }
                        Map<UUID, String> members=new HashMap<>();
                        PreparedStatement membersstmt = connection.prepareStatement(
                                "SELECT * FROM `room_members` WHERE `roomid` = ?;"
                        );
                        membersstmt.setInt(1, rowid);
                        ResultSet memberresult = membersstmt.executeQuery();//https://stackoverflow.com/questions/21442148/java-select-from-array-of-values
                        while(memberresult.next()){
                            UUID memberuuid= UUID.fromString(result.getString("player_uuid"));
                            String membername= result.getString("player_name");
                            members.put(memberuuid,membername);
                        }
                        String enviroment = result.getString("enviroment");
                        String bordercolour = result.getString("bordercolour");
                        //trustedMembers.putIfAbsent();
                        if (!RoomWorlds.getRoomWolrds().containsKey(uuid)) {
                            RoomWorlds.addHouse(uuid,new RoomWorld(rowid,uuid,OwnerUUID,Ownername,locktime,spawnX,spawnY,spawnZ,blockedMembers,trustedMembers,members,enviroment,bordercolour));
                        }else{
                            if(RoomWorlds.getRoomWorldUUID(uuid).isLoaded()){
                                RoomWorlds.addHouse(uuid,new RoomWorld(rowid,uuid,OwnerUUID,Ownername,locktime,spawnX,spawnY,spawnZ,blockedMembers,trustedMembers,members,enviroment,bordercolour));
                            }
                        }


                    }
                    if(target!=null){
                        System.out.println("debugf");
                        if(FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())){
                            System.out.println("debugflood");
                            new BRKVisitTargetRooms().makemenu(player,target);
                        } else{
                            System.out.println("debugj");
                            BukkitRunnable run =new BukkitRunnable() {
                                @Override
                                public void run() {
                                    System.out.println("debugjj");
                                    new VisitTargetRoomsMenu(Rooms.getPlayerMenuUtility(player),target).open();
                                }
                            };
                            run.runTask(Rooms.getPlugin());
                            //new MainMenu(Rooms.getPlayerMenuUtility(p)).open();
                        }
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };
        r.runTaskAsynchronously(Rooms.getPlugin());

    }

    public void loadRoomWorlds(Player player) throws SQLException {
        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    PreparedStatement statement = connection.prepareStatement(
                            "SELECT * FROM `room_worlds` WHERE `owneruuid` LIKE ? ORDER BY `id` ASC;"
                    );
                    statement.setString(1,player.getUniqueId().toString());
                    ResultSet result = statement.executeQuery();
                    while (result.next()) {
                        int rowid = result.getInt("id");

                        UUID uuid = UUID.fromString(result.getString("worlduuid"));
                        System.out.println(uuid.toString());
                        UUID OwnerUUID = player.getUniqueId();
                        String Ownername = player.getName();
                        String locktime = result.getString("timestamp");
                        Integer spawnX = result.getInt("x");
                        Integer spawnY = result.getInt("y");
                        Integer spawnZ = result.getInt("z");

                        Map<UUID, String> blockedMembers = new HashMap<>();
                        PreparedStatement blockedstmt = connection.prepareStatement(
                                "SELECT * FROM `room_blocked` WHERE `roomid` = ?;"
                        );
                        blockedstmt.setInt(1, rowid);
                        ResultSet blockedresult = blockedstmt.executeQuery();
                        while(blockedresult.next()){
                            UUID trustuuid= UUID.fromString(result.getString("player_uuid"));
                            String name= result.getString("player_name");
                            blockedMembers.put(trustuuid,name);
                        }

                        Map<UUID, String> trustedMembers = new HashMap<>();
                        PreparedStatement trustedmembersstmt = connection.prepareStatement(
                                "SELECT * FROM `room_trustedmembers` WHERE `roomid` = ?;"
                        );
                        trustedmembersstmt.setInt(1, rowid);
                        ResultSet trustedresult = trustedmembersstmt.executeQuery();
                        while(trustedresult.next()){
                            UUID trustuuid= UUID.fromString(result.getString("player_uuid"));
                            String name= result.getString("player_name");
                            trustedMembers.put(trustuuid,name);
                        }
                        Map<UUID, String> members=new HashMap<>();
                        PreparedStatement membersstmt = connection.prepareStatement(
                                "SELECT * FROM `room_members` WHERE `roomid` = ?;"
                        );
                        membersstmt.setInt(1, rowid);
                        ResultSet memberresult = membersstmt.executeQuery();//https://stackoverflow.com/questions/21442148/java-select-from-array-of-values
                        while(memberresult.next()){
                            UUID memberuuid= UUID.fromString(result.getString("player_uuid"));
                            String membername= result.getString("player_name");
                            members.put(memberuuid,membername);
                        }
                        String enviroment = result.getString("enviroment");
                        String bordercolour = result.getString("bordercolour");
                        //trustedMembers.putIfAbsent();
                        if (!RoomWorlds.getRoomWolrds().containsKey(uuid)) {
                            RoomWorlds.addHouse(uuid,new RoomWorld(rowid,uuid,OwnerUUID,Ownername,locktime,spawnX,spawnY,spawnZ,blockedMembers,trustedMembers,members,enviroment,bordercolour));
                        }else{
                            if(RoomWorlds.getRoomWorldUUID(uuid).isLoaded()){
                                RoomWorlds.addHouse(uuid,new RoomWorld(rowid,uuid,OwnerUUID,Ownername,locktime,spawnX,spawnY,spawnZ,blockedMembers,trustedMembers,members,enviroment,bordercolour));
                            }
                        }

                    }
                } catch (SQLException e) {
                    try {
                        if(connection==null){
                            Rooms.getPlugin().getLogger().info("is null");
                        }else{
                            Rooms.getPlugin().getLogger().info("not null");
                        }
                        Rooms.getPlugin().getLogger().info("isconnection()"+connection.isClosed());
                    } catch (SQLException ex) {

                        ex.printStackTrace();
                    }
                    e.printStackTrace();
                }
            }
        };
        r.runTaskAsynchronously(Rooms.getPlugin());

    }


    public void insertMigratedRoomWorld(UUID playeruuid, SlimeWorld world, SlimePropertyMap sprop) throws SQLException {
        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    PreparedStatement stmt = connection.prepareStatement(
                            "INSERT INTO `room_worlds` (`id`, `worlduuid`, `owneruuid`, `owner`, `timestamp`, `x`, `y`, `z`, `enviroment`, `bordercolour`) VALUES (NULL, ?, ?, ?, current_timestamp(), ?, ?, ?, ?, ?);"
                    );
                    stmt.setString(1, world.getName());
                    stmt.setString(2, playeruuid.toString());
                    String generatedString = RandomStringUtils.random(3, true, false);
                    try{
                    String playerName =Bukkit.getOfflinePlayer(playeruuid).getName();
                        stmt.setString(3,playerName);
                    }catch(NullPointerException exception){
                        stmt.setString(3,"migrated"+generatedString);
                    }

                    stmt.setInt(4,sprop.getValue(SlimeProperties.SPAWN_X));
                    stmt.setInt(5,sprop.getValue(SlimeProperties.SPAWN_Y));
                    stmt.setInt(6,sprop.getValue(SlimeProperties.SPAWN_Z));
                    stmt.setString(7,world.getPropertyMap().getValue(SlimeProperties.ENVIRONMENT));
                    stmt.setString(8, Rooms.configs.getGeneralConfig().getString("bordercolour"));

                    stmt.executeUpdate();
                    PreparedStatement stmt2 = connection.prepareStatement(
                            "SELECT `id`, `owner`, `timestamp` FROM `room_worlds` WHERE `worlduuid` = ?"
                    );
                    stmt2.setString(1   ,world.getName());
                    ResultSet result = stmt2.executeQuery();
                    if(result.next()){
                        int rowid = result.getInt("id");
                        String ownername = result.getString("owner");
                        String timestamp = result.getString("timestamp");
                        RoomWorlds.addHouse(UUID.fromString(world.getName()), new RoomWorld(rowid, UUID.fromString(world.getName()), playeruuid, ownername, timestamp, sprop.getValue(SlimeProperties.SPAWN_X), sprop.getValue(SlimeProperties.SPAWN_Y), sprop.getValue(SlimeProperties.SPAWN_Z), new HashMap<>(), new HashMap<>(), new HashMap<>(),  world.getPropertyMap().getValue(SlimeProperties.ENVIRONMENT),"green"));
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };
        r.runTaskAsynchronously(Rooms.getPlugin());

    }
    public void insertRoomWorld(Player player, SlimeWorld world, SlimePropertyMap sprop) throws SQLException {
        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    PreparedStatement stmt = connection.prepareStatement(
                            "INSERT INTO `room_worlds` (`worlduuid`, `owneruuid`, `owner`, `timestamp`, `x`, `y`, `z`, `enviroment`, `bordercolour`) VALUES (?, ?, ?, current_timestamp(), ?, ?, ?, ?,?);"
                    );
                    stmt.setString(1, world.getName());
                    stmt.setString(2,player.getUniqueId().toString());
                    stmt.setString(3,player.getName());
                    stmt.setInt(4,sprop.getValue(SlimeProperties.SPAWN_X));
                    stmt.setInt(5,sprop.getValue(SlimeProperties.SPAWN_Y));
                    stmt.setInt(6,sprop.getValue(SlimeProperties.SPAWN_Z));
                    stmt.setString(7,world.getPropertyMap().getValue(SlimeProperties.ENVIRONMENT));
                    stmt.setString(8,Rooms.configs.getGeneralConfig().getString("bordercolour"));

                    stmt.executeUpdate();
                    PreparedStatement stmt2 = connection.prepareStatement(
                            "SELECT `id`, `timestamp` FROM `room_worlds` WHERE `worlduuid` = ?"
                    );
                    stmt2.setString(1   ,world.getName());
                    ResultSet result = stmt2.executeQuery();
                    if(result.next()){
                        int rowid = result.getInt("id");
                        String timestamp = result.getString("timestamp");
                        System.out.println("ROW ID: "+rowid);
                        RoomWorlds.addHouse(UUID.fromString(world.getName()), new RoomWorld(rowid, UUID.fromString(world.getName()), player.getUniqueId(), player.getName(), timestamp, sprop.getValue(SlimeProperties.SPAWN_X), sprop.getValue(SlimeProperties.SPAWN_Y), sprop.getValue(SlimeProperties.SPAWN_Z), new HashMap<>(), new HashMap<>(), new HashMap<>(),  world.getPropertyMap().getValue(SlimeProperties.ENVIRONMENT),"green"));


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
                    PreparedStatement stmt = connection.prepareStatement(
                            "UPDATE `room_worlds` SET worlduuid=? ,owneruuid=? ,owner=? ,x=? ,y=? ,z=? ,enviroment=?, bordercolour=? WHERE `id` = ?;"
                    );
                    stmt.setString(1, roomWorld.getWorldUUID().toString());
                    stmt.setString(2, roomWorld.getOwnerUUID().toString());
                    stmt.setString(3, roomWorld.getOwnerName());
                    stmt.setInt(4,roomWorld.getSpawnX());
                    stmt.setInt(5,roomWorld.getSpawnY());
                    stmt.setInt(6,roomWorld.getSpawnZ());
                    stmt.setString(7 ,roomWorld.getEnviroment());
                    stmt.setString(8,roomWorld.getBorderColor());
                    stmt.setInt(9,roomWorld.getRowid());
                    System.out.println(stmt);
                    stmt.executeUpdate();
                    roomWorld.getMembers().forEach((uuid, s) -> {
                        try {
                            PreparedStatement statement = connection.prepareStatement(
                                    "UPDATE `room_members` SET player_uuid=? ,player_name=? WHERE `roomid` = ?;"
                            );
                            statement.setString(1,uuid.toString());
                            statement.setString(2,s);
                            statement.setString(3,roomWorld.getWorldUUID().toString());
                            statement.executeUpdate();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });
                    roomWorld.getTrustedMembers().forEach((uuid, s) -> {
                        try {
                            PreparedStatement statement = connection.prepareStatement(
                                    "UPDATE `room_trustedmembers` SET player_uuid=? ,player_name=? WHERE `roomid` = ?;"
                            );
                            statement.setString(1,uuid.toString());
                            statement.setString(2,s);
                            statement.setString(3,roomWorld.getWorldUUID().toString());
                            statement.executeUpdate();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });
                    roomWorld.getBlocked().forEach((uuid, s) -> {
                        try {
                            PreparedStatement statement = connection.prepareStatement(
                                    "UPDATE `room_blocked` SET player_uuid=? ,player_name=? WHERE `roomid` = ?;"
                            );
                            statement.setString(1,uuid.toString());
                            statement.setString(2,s);
                            statement.setString(3,roomWorld.getWorldUUID().toString());
                            statement.executeUpdate();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });
                    /*
                    try{
                    if(!Bukkit.getOfflinePlayer(roomWorld.getOwnerUUID()).isOnline()){
                        RoomWorlds.getRoomWolrds().remove(roomWorld.getWorldUUID());
                    }}catch (Exception exception){
                        RoomWorlds.getRoomWolrds().remove(roomWorld.getWorldUUID());
                    }*/

                    //RoomWorlds.houseWorldBungeeInfoArrayList.remove(roomWorld.getWorldUUID());
                    /*if(!Bukkit.getOfflinePlayer(houseWorld.getOwnerUUID()).isOnline()){
                        RoomWorlds.removeRoomWorld(houseWorld.getWorldUUID());
                    }*/
                    //RoomWorlds.removeRoomWorld(houseWorld.getWorldUUID());
                    ///RoomWorlds.getRoomWolrds().remove(houseWorld.getWorldUUID(),houseWorld);
                    //todo make sync instead of sync for shutdown
              } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };
        if(async){
            r.runTaskAsynchronously(Rooms.getPlugin());
        }else{
            r.runTask(Rooms.getPlugin());
        }


    }
    public void deleteRoom(UUID worlduuid){
        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    PreparedStatement purgestmt = connection.prepareStatement(
                            "DELETE FROM `room_worlds` WHERE `worlduuid` = `?`;"
                    );
                    purgestmt.setString(1,worlduuid.toString());
                    purgestmt.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };
        r.runTaskAsynchronously(Rooms.getPlugin());

    }
    //todo untrust member
    /*
    public void insertGlobalRoomWorld(RoomWorld houseWorld) throws SQLException {
        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    PreparedStatement stmt = connection.prepareStatement(
                            "INSERT INTO `room_global` (`worlduuid`, `lastserver`, `owner_name`) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE lastserver=?, owner_name=?,lastupdate=CURRENT_TIMESTAMP;"
                    );
                    stmt.setString(1, houseWorld.getWorldUUID().toString());
                    stmt.setString(2, bungeesrvname);
                    stmt.setString(3, houseWorld.getOwnerName());
                    stmt.setString(4, bungeesrvname);
                    stmt.setString(5, houseWorld.getOwnerName());
                    //System.out.println("sqlstatement: "+stmt);
                    stmt.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };
        r.runTaskAsynchronously(Rooms.getPlugin());

    }*/
    /*
    public void loadGlobalRoomWorlds() throws SQLException {
        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    if(Rooms.configs.getMySQLConfig().getBoolean("purgeglobaltask")){
                        PreparedStatement purgestmt = connection.prepareStatement(
                                "DELETE FROM `room_global` WHERE `lastupdate` < NOW() - interval 60 SECOND;"
                        );
                        purgestmt.executeUpdate();
                    }
                    PreparedStatement statement = connection.prepareStatement(
                            "SELECT * FROM `room_global`;"
                    );
                    ResultSet result = statement.executeQuery();
                    HashMap<UUID, GlobalRoomWorld> houseWorldBungeeInfoArrayList = new HashMap<>();
                    while (result.next()) {
                        UUID uuid = UUID.fromString(result.getString("worlduuid"));
                        String Ownername = result.getString("owner_name");
                        String lastserver = result.getString("lastserver");
                        houseWorldBungeeInfoArrayList.putIfAbsent(uuid,new GlobalRoomWorld(uuid,Ownername,lastserver));
                    }
                    GlobalRoomWorlds.globalRoomWorldHashMap=houseWorldBungeeInfoArrayList;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };
        r.runTaskAsynchronously(Rooms.getPlugin());

    }*/
    public void close() throws SQLException {
        connection.close();
    }
}