package tel.endho.rooms;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import tel.endho.rooms.Tasks.UnloadEmptyTask;
import tel.endho.rooms.Tasks.UnloadStaleGlobalTask;
import tel.endho.rooms.Tasks.UpdateGlobalTask;
import tel.endho.rooms.Tasks.WorldBorderTask;
import tel.endho.rooms.commands.RoomCommand;
import tel.endho.rooms.listeners.*;
import tel.endho.rooms.menusystem.PlayerMenuUtility;
import tel.endho.rooms.storage.Configs;
import tel.endho.rooms.storage.MySQL;
import tel.endho.rooms.storage.Redis;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class Rooms extends JavaPlugin {
  public static Rooms instance;
  public static MySQL mysql;
  public static Redis redis;
  private static Boolean debugMode;
  private static Boolean isFloodgateLoaded;
  public static RoomWorldManager roomWorldManager;
  public static Configs configs;
  private FaweListener faweListener;
  private static final HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();
  public static HashMap<UUID, HashMap<UUID, Long>> pendingTeleports = new HashMap<>();

  @Override
  public void onEnable() {
    instance = this;
    configs = new Configs();
    configs.loadConfigs();
    File schemfolder = new File(this.getDataFolder() + "/schematics");
    schemfolder.mkdir();
    //this.saveResource("schematics/normal.schem", false);
    //this.saveResource("schematics/nether.schem", false);
    //this.saveResource("schematics/the_end.schem", false);
    debugMode = configs.getGeneralConfig().getBoolean("enabledebugging");
    roomWorldManager = new RoomWorldManager();
    mysql = new MySQL();
    try {
      mysql.initmysql(configs.getStorageConfig().getString("mysqlhost"),
          configs.getStorageConfig().getString("mysqldatabase"), configs.getStorageConfig().getString("mysqlusername"),
          configs.getStorageConfig().getString("mysqlpassword"), configs.getStorageConfig().getInt("mysqlport"));
    } catch (SQLException | ClassNotFoundException e) {
      e.printStackTrace();
    }
    redis = new Redis();
    if (configs.getStorageConfig().getBoolean("enableredis")) {
      try {
        redis.initRedis(configs.getStorageConfig().getString("bungeeservername"),
            configs.getStorageConfig().getString("redishost"), configs.getStorageConfig().getString("redispassword"),
            configs.getStorageConfig().getInt("redisport"));
      } catch (ExecutionException | InterruptedException | TimeoutException e) {
        redis.initRedis();
        e.printStackTrace();
      }
    }else{
      redis.initRedis();
    }
    getCommand("Room").setExecutor(new RoomCommand());
    this.faweListener = new FaweListener();
    faweListener.startListening();
    // Menu listener system
    getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    getServer().getPluginManager().registerEvents(new BlockListener(), this);
    getServer().getPluginManager().registerEvents(new EntitiyListener(), this);
    getServer().getPluginManager().registerEvents(new FallEvent(), this);
    getServer().getPluginManager().registerEvents(new JoinListener(), this);
    getServer().getPluginManager().registerEvents(new LeaveListener(), this);
    getServer().getPluginManager().registerEvents(new BucketListener(), this);
    // getServer().getPluginManager().registerEvents(new FaweListener(), this);
    getServer().getPluginManager().registerEvents(new MenuListener(), this);
    getServer().getPluginManager().registerEvents(new PortalListener(), this);
    getServer().getPluginManager().registerEvents(new VehicleListener(), this);
    if (!Bukkit.getServer().getOnlinePlayers().isEmpty()) {
      Bukkit.getServer().getOnlinePlayers().forEach(e -> {
        try {
          mysql.loadRoomWorlds(e.getPlayer());
        } catch (SQLException ex) {
          ex.printStackTrace();
        }
      });
    }
    // todo purge globalhouselist
    this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new UpdateGlobalTask(), 0, 300);
    // this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new
    // FetchBungeeInfoTask(), 0, 600);
    this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new WorldBorderTask(), 0, 300);
    this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new UnloadEmptyTask(), 0, 200);
    this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new UnloadStaleGlobalTask(), 0, 300);
    if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      new PAPIExpansion(this).register();
    }
    if (Bukkit.getPluginManager().isPluginEnabled("floodgate")) {
      isFloodgateLoaded = true;
    }
  }

  @Override
  public void onDisable() {
    RoomWorlds.getRoomWolrds().forEach((uuid, roomWorld) -> {
      if (roomWorld.isLoaded()) {
        World world = Bukkit.getWorld(roomWorld.getWorldUUID().toString());
        if ((world != null) && !world.getPlayers().isEmpty()) {
          world.getPlayers().forEach(player -> {
            player.kickPlayer("room world plugin disabled");
          });
        }
        if (world != null) {
          try {
            Rooms.mysql.saveRoomWorld(roomWorld, false);
            WorldGuardManager.unloadWorld(roomWorld.getWorldUUID());
            File bob = new File(Rooms.getPlugin().getDataFolder().getParent() + "/WorldGuard/worlds/" + uuid);
            bob.deleteOnExit();
          } catch (SQLException e) {
            e.printStackTrace();
          }
          world.save();
          Bukkit.unloadWorld(world, false);
        }
        // todo use unloadRoomworld
      }
    });
    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    try {
      mysql.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static PlayerMenuUtility getPlayerMenuUtility(Player p) {
    PlayerMenuUtility playerMenuUtility;
    if (!(playerMenuUtilityMap.containsKey(p))) { // See if the player has a playermenuutility "saved" for them

      // This player doesn't. Make one for them add add it to the hashmap
      playerMenuUtility = new PlayerMenuUtility(p);
      playerMenuUtilityMap.put(p, playerMenuUtility);

      return playerMenuUtility;
    } else {
      return playerMenuUtilityMap.get(p); // Return the object by using the provided player
    }
  }

  public static Rooms getPlugin() {
    return instance;
  }

  public static void debug(String string) {
    if (debugMode) {
      getPlugin().getLogger().info(string);
    }
  }

  public static boolean isOffline(UUID uuid) {
    return Bukkit.getOfflinePlayer(uuid).isOnline();
  }

  public void sendPlayer(Player p, String s) {
    ByteArrayDataOutput out = ByteStreams.newDataOutput();
    out.writeUTF("Connect");
    out.writeUTF(s);
    Player player = p;
    player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
  }

  public static Boolean isFloodgateLoaded() {
    return isFloodgateLoaded;
  }

}
