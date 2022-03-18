package tel.endho.rooms;

import com.fastasyncworldedit.core.FaweAPI;
import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.exceptions.*;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import com.plotsquared.core.PlotAPI;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.biome.BiomeTypes;
import com.sk89q.worldedit.world.block.BlockTypes;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

public class RoomWorldManager {
  SlimePlugin plugin = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
  SlimeLoader sqlLoader = plugin.getLoader("mysql");

  public void migrateAll() {
    BukkitRunnable r = new BukkitRunnable() {
      @Override
      public void run() {
        PlotAPI api = new PlotAPI();
        api.getAllPlots().forEach(plot -> {
          try {
            migratePlot(plot);
          } catch (SQLException | WorldAlreadyExistsException | IOException e) {
            e.printStackTrace();
          }
          try {
            Thread.sleep(10000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        });

      }
    };
    r.runTaskAsynchronously(Rooms.getPlugin());
  }

  public void migratePlot(Plot plot) throws SQLException, IOException, WorldAlreadyExistsException {
    if (plot != null && !plot.isMerged() && plot.getOwner() != null) {
      /*
       * for (Plot allPlot : api.getAllPlots()) {
       * allPlot.getLargestRegion();
       * }
       */
      UUID worlduuid = UUID.randomUUID();
      SlimePropertyMap properties = new SlimePropertyMap();
      properties.setValue(SlimeProperties.WORLD_TYPE, "flat");
      properties.setValue(SlimeProperties.ENVIRONMENT, "normal");
      properties.setValue(SlimeProperties.DIFFICULTY, "normal");
      properties.setValue(SlimeProperties.SPAWN_Y, Rooms.configs.getGeneralConfig().getInt("plotsquaredheight"));
      SlimeWorld world = plugin.createEmptyWorld(sqlLoader, String.valueOf(worlduuid), false, properties);

      plugin.generateWorld(world);
      Rooms.mysql.insertMigratedRoomWorld(plot.getOwner(), world, properties);
      defaultBorder(world.getName());
      CuboidRegion region = plot.getLargestRegion();
      BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
      try (EditSession es1 = WorldEdit.getInstance().newEditSession(FaweAPI.getWorld(plot.getWorldName()))) {
        ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(
            es1, region, clipboard, region.getMinimumPoint());
        Operations.complete(forwardExtentCopy);
        es1.close();
      } // it is automatically closed/flushed when the code exits the block
      try (EditSession es2 = WorldEdit.getInstance().newEditSession(FaweAPI.getWorld(worlduuid.toString()))) {
        int plotsize = -(Rooms.configs.getGeneralConfig().getInt("plotsquaredsize") / 2);
        Operation operation = new ClipboardHolder(clipboard)
            .createPaste(es2)
            .to(BlockVector3.at(plotsize, 0, plotsize))
            // configure here
            .build();
        Operations.complete(operation);
        es2.close();
      } // it is automatically closed/flushed when the code exits the block

      plot.unclaim();
    }
  }

  public void migratePlot(Player player) throws SQLException, IOException, WorldAlreadyExistsException {
    PlotAPI api = new PlotAPI();
    Rooms.debug("debug1");
    // final PlotPlayer pl = PlotPlayer.get(p.getName());
    final PlotPlayer<?> pl = api.wrapPlayer(player.getUniqueId());
    Rooms.debug("debug2");
    assert pl != null;
    Rooms.debug("debug3");
    final Plot plot = pl.getCurrentPlot();
    Rooms.debug("debug4");
    if (plot != null) {
      Rooms.debug("debug6");
      if (!plot.isMerged()) {
        Rooms.debug("debug7");
        Rooms.debug("plotowneruuid: " + plot.getOwner().toString());
        Rooms.debug("playeruuid: " + player.getUniqueId());
        if (player.getUniqueId().equals(plot.getOwner())) {
          Rooms.debug("debug8");
        }
      }
    }
    if (plot != null && !plot.isMerged() && player.getUniqueId().equals(plot.getOwner())) {
      /*
       * for (Plot allPlot : api.getAllPlots()) {
       * allPlot.getLargestRegion();
       * }
       */
      Rooms.debug("debug5");
      UUID worlduuid = UUID.randomUUID();
      SlimePropertyMap properties = new SlimePropertyMap();
      properties.setValue(SlimeProperties.WORLD_TYPE, "flat");
      properties.setValue(SlimeProperties.ENVIRONMENT, "normal");
      properties.setValue(SlimeProperties.DIFFICULTY, "normal");
      properties.setValue(SlimeProperties.SPAWN_Y, Rooms.configs.getGeneralConfig().getInt("plotsquaredheight"));
      SlimeWorld world = plugin.createEmptyWorld(sqlLoader, String.valueOf(worlduuid), false, properties);
      // This method must be called synchronously
      plugin.generateWorld(world);

      CuboidRegion region = plot.getLargestRegion();
      BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
      try (EditSession es1 = WorldEdit.getInstance().newEditSession(FaweAPI.getWorld(plot.getWorldName()))) {
        ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(
            es1, region, clipboard, region.getMinimumPoint());
        Operations.complete(forwardExtentCopy);
      } // it is automatically closed/flushed when the code exits the block
      try (EditSession es2 = WorldEdit.getInstance().newEditSession(FaweAPI.getWorld(plot.getWorldName()))) {
        int plotsize = -(Rooms.configs.getGeneralConfig().getInt("plotsquaredsize") / 2);
        Operation operation = new ClipboardHolder(clipboard)
            .createPaste(es2)
            .to(BlockVector3.at(plotsize, 0, plotsize))
            // configure here
            .build();
        Operations.complete(operation);
      } // it is automatically closed/flushed when the code exits the block
      plot.unclaim();
      Rooms.mysql.insertRoomWorld(player, world, properties);
      // Location location= new
      // Location(Bukkit.getWorld(worlduuid.toString()),1,255,1);
      // player.teleport(location);
    }
  }

  public void createWorld(String worldtype, Player player) {
    try {
      // Note that this method should be called asynchronously
      // SlimeWorld world = plugin.loadWorld(sqlLoader, "my-world", props);
      UUID worlduuid = UUID.randomUUID();
      SlimePropertyMap properties = new SlimePropertyMap();
      properties.setValue(SlimeProperties.WORLD_TYPE, "flat");
      properties.setValue(SlimeProperties.ENVIRONMENT, worldtype);
      Rooms.debug("test123: " + Biome.CRIMSON_FOREST.toString());
      if (worldtype.equals("nether")) {
        properties.setValue(SlimeProperties.DEFAULT_BIOME, "minecraft:crimson_forest");
        Rooms.debug("propbiome: " + properties.getValue(SlimeProperties.DEFAULT_BIOME));
      }
      properties.setValue(SlimeProperties.DIFFICULTY, "normal");
      properties.setValue(SlimeProperties.SPAWN_X, 1);
      properties.setValue(SlimeProperties.SPAWN_Y, Rooms.configs.getGeneralConfig().getInt("spawnheight"));
      properties.setValue(SlimeProperties.SPAWN_Z, 1);
      SlimeWorld world = plugin.createEmptyWorld(sqlLoader, String.valueOf(worlduuid), false, properties);
      // This method must be called synchronously
      plugin.generateWorld(world);
      Objects.requireNonNull(Bukkit.getWorld(worlduuid.toString())).setGameRule(GameRule.DO_MOB_SPAWNING, false);
      Objects.requireNonNull(Bukkit.getWorld(worlduuid.toString())).setGameRule(GameRule.DO_FIRE_TICK, false);
      BukkitRunnable r = new BukkitRunnable() {
        @Override
        public void run() {
          try (EditSession es2 = WorldEdit.getInstance().newEditSession(FaweAPI.getWorld(world.getName()))) {
            int wbsize = Rooms.configs.getGeneralConfig().getInt("worldborder");
            int fillsize = Rooms.configs.getGeneralConfig().getInt("fillsize");
            String fillmaterial = Rooms.configs.getGeneralConfig().getString("fillmaterial").toLowerCase();
            int halfsize = wbsize / 2;
            CuboidRegion region = new CuboidRegion(BlockVector3.at(halfsize - 1, 0, halfsize - 1),
                BlockVector3.at(-halfsize, fillsize, -halfsize));
            es2.setBlocks((Region) region, BlockTypes.get(fillmaterial));
            Bukkit.getScheduler().runTask(Rooms.getPlugin(), () -> {
              Location loc = new Location(Rooms.getPlugin().getServer().getWorld(world.getName()),
                  Double.valueOf(SlimeProperties.SPAWN_X.getDefaultValue()),
                  Rooms.configs.getGeneralConfig().getInt("spawnheight"),
                  Double.valueOf(SlimeProperties.SPAWN_Z.getDefaultValue()));
              player.teleport(loc);
            });
            // do bedrock too
          }
        }
      };
      r.runTaskAsynchronously(Rooms.getPlugin());

      Rooms.mysql.insertRoomWorld(player, world, properties);
      // HouseWorlds.getHouseWolrds().putIfAbsent(worlduuid,new HouseWorld(null));
    } catch (IOException | WorldAlreadyExistsException | SQLException ex) {
      /* Exception handling */
    }
  }

  public void genNether(RoomWorld roomWorld, Player player) {

    try {
      if (sqlLoader.listWorlds().contains(roomWorld.getWorldUUID().toString() + "rmnether")) {
        roomWorld.setHasNether(true);
        return;
      }
      // Note that this method should be called asynchronously
      // SlimeWorld world = plugin.loadWorld(sqlLoader, "my-world", props);
      // UUID worlduuid = UUID.randomUUID();
      /*
       * sqlLoader.listWorlds().forEach(a->{
       * Rooms.debug(a);
       * });
       */
      // Rooms.debug("test: "+
      // plugin.getWorld(sqlLoader,roomWorld.getWorldUUID().toString()+"rmnether").);
      SlimePropertyMap properties = new SlimePropertyMap();
      properties.setValue(SlimeProperties.WORLD_TYPE, "flat");
      properties.setValue(SlimeProperties.ENVIRONMENT, "nether");
      properties.setValue(SlimeProperties.DEFAULT_BIOME, "minecraft:crimson_forest");
      properties.setValue(SlimeProperties.DIFFICULTY, "normal");
      properties.setValue(SlimeProperties.SPAWN_X, 1);
      properties.setValue(SlimeProperties.SPAWN_Y, Rooms.configs.getGeneralConfig().getInt("spawnheight"));
      properties.setValue(SlimeProperties.SPAWN_Z, 1);
      SlimeWorld world = plugin.createEmptyWorld(sqlLoader, roomWorld.getWorldUUID().toString() + "rmnether", false,
          properties);
      // This method must be called synchronously
      plugin.generateWorld(world);
      String schematic = Rooms.configs.getPresetConfig().getString("netherschematic");
      BukkitRunnable r = new BukkitRunnable() {
        @Override
        public void run() {
          try (EditSession es2 = WorldEdit.getInstance().newEditSession(FaweAPI.getWorld(world.getName()))) {
            int wbsize = Rooms.configs.getGeneralConfig().getInt("worldborder");
            int halfsize = wbsize / 2;
            if (schematic.equals("flat")) {
              int fillsize = Rooms.configs.getGeneralConfig().getInt("fillsize");
              String fillmaterial = "netherrack";
              // String fillmaterial =
              // Rooms.configs.getGeneralConfig().getString("fillmaterial").toLowerCase();

              CuboidRegion region = new CuboidRegion(BlockVector3.at(halfsize - 1, 0, halfsize - 1),
                  BlockVector3.at(-halfsize, fillsize, -halfsize));
              es2.setBlocks((Region) region, BlockTypes.get(fillmaterial));

            }
            CuboidRegion regionn = new CuboidRegion(BlockVector3.at(halfsize - 1, 0, halfsize - 1),
                BlockVector3.at(-halfsize, 256, -halfsize));
            regionn.forEach(bv -> {
              es2.setBiome(bv, BiomeTypes.SOUL_SAND_VALLEY);
            });
            // es2.setBiome((Region)region, BiomeTypes.CRIMSON_FOREST);
            Bukkit.getScheduler().runTask(Rooms.getPlugin(), () -> {
              Location loc = new Location(Rooms.getPlugin().getServer().getWorld(world.getName()),
                  Double.valueOf(SlimeProperties.SPAWN_X.getDefaultValue()),
                  Rooms.configs.getGeneralConfig().getInt("spawnheight"),
                  Double.valueOf(SlimeProperties.SPAWN_Z.getDefaultValue()));
              player.teleport(loc);
            });
            // do bedrock too
          }
        }
      };
      r.runTaskAsynchronously(Rooms.getPlugin());

      roomWorld.setHasNether(true);
      // todo mysql update hasnether
      // Rooms.mysql.insertRoomWorld(player, world, properties);
      // HouseWorlds.getHouseWolrds().putIfAbsent(worlduuid,new HouseWorld(null));
    } catch (IOException | WorldAlreadyExistsException ex) {
      /* Exception handling */
    }
  }

  public void TpOrLoadHouseWorld(Player p, UUID uuid)
      throws CorruptedWorldException, NewerFormatException, WorldInUseException, UnknownWorldException, IOException {

    if (GlobalRoomWorlds.isOnAnotherServer(uuid)) {
      // maybe if globalroomworld(uuid).getserver=thisserver{load, but that dont load
      // duplicate idk tired}
      // todo &&if roomworld isnt on this server
      // todo if() roomworld is on this server
      // todo if(globalroomworld.region!= config.region)
      //
      // todo implement lobby mode
      Rooms.getPlugin().sendPlayer(p, GlobalRoomWorlds.getGlobalRoomWorldUUID(uuid).lastserver);
      Rooms.redis.teleportPlayer(p, GlobalRoomWorlds.getGlobalRoomWorldUUID(uuid).lastserver, uuid);
    } else {
      if (Rooms.configs.getStorageConfig().getBoolean("redislobby") && Rooms.redis.isLoaded()) {
        Rooms.getPlugin().sendPlayer(p, GlobalRoomWorlds.getGlobalRoomWorldUUID(uuid).lastserver);
      } else {
        if (RoomWorlds.isRoomWorld(uuid)) {
          RoomWorld roomWorld = RoomWorlds.getRoomWorldUUID(uuid);
          if (Bukkit.getWorld(roomWorld.getWorldUUID().toString()) != null) {
            Location location = new Location(Bukkit.getWorld(uuid.toString()), roomWorld.getSpawnX().doubleValue(),
                roomWorld.getSpawnY().doubleValue(), roomWorld.getSpawnZ().doubleValue());
            p.teleport(location);
          } else {
            // todo make async andput player teleport after
            loadWorld(roomWorld);
            Location location = new Location(Bukkit.getWorld(uuid.toString()), roomWorld.getSpawnX().doubleValue(),
                roomWorld.getSpawnY().doubleValue(), roomWorld.getSpawnZ().doubleValue());
            p.teleport(location);
          }
        }

      }
    }
  }

  public void TpOrLoadHouseWorld(Player p, String uuidstring)
      throws CorruptedWorldException, NewerFormatException, WorldInUseException, UnknownWorldException, IOException {
    UUID realuuid = UUID.fromString(uuidstring.substring(0, Math.min(uuidstring.length(), 36)));
    String uuidsuffix = "";
    if (uuidstring.endsWith("rmnether")) {
      uuidsuffix = "rmnether";
    }
    if (uuidstring.endsWith("rmend")) {
      uuidsuffix = "rmend";
    }
    if (GlobalRoomWorlds.isOnAnotherServer(realuuid)) {
      // maybe if globalroomworld(uuid).getserver=thisserver{load, but that dont load
      // duplicate idk tired}
      // todo &&if roomworld isnt on this server
      // todo if() roomworld is on this server
      // todo if(globalroomworld.region!= config.region)
      //
      // todo implement lobby mode
      Rooms.getPlugin().sendPlayer(p, GlobalRoomWorlds.getGlobalRoomWorldUUID(realuuid).lastserver);
      Rooms.redis.teleportPlayer(p, GlobalRoomWorlds.getGlobalRoomWorldUUID(realuuid).lastserver, realuuid);
    } else {
      if (Rooms.configs.getStorageConfig().getBoolean("redislobby") && Rooms.redis.isLoaded()) {
        Rooms.getPlugin().sendPlayer(p, GlobalRoomWorlds.getGlobalRoomWorldUUID(realuuid).lastserver);
      } else {
        if (RoomWorlds.isRoomWorld(realuuid)) {
          RoomWorld roomWorld = RoomWorlds.getRoomWorldUUID(realuuid);
          final World world = Bukkit.getWorld(roomWorld.getWorldUUID().toString() + uuidsuffix);
          if (world != null) {
            Location location = new Location(world, roomWorld.getSpawnX().doubleValue(),
                roomWorld.getSpawnY().doubleValue(), roomWorld.getSpawnZ().doubleValue());
            p.teleport(location);
          } else {
            // todo make async andput player teleport after
            loadWorld(roomWorld);
            Location location = new Location(world, roomWorld.getSpawnX().doubleValue(),
                roomWorld.getSpawnY().doubleValue(), roomWorld.getSpawnZ().doubleValue());
            p.teleport(location);
          }
        }

      }
    }
  }

  public void unloadRoomWorld(RoomWorld roomWorld) {
    World world = Bukkit.getWorld(roomWorld.getWorldUUID().toString());
    assert world != null;
    world.save();
    Rooms.debug("unloadWorld: " + Bukkit.unloadWorld(roomWorld.getWorldUUID().toString(), true));
    // Bukkit.unloadWorld(world,true);
    try {
      Rooms.mysql.saveRoomWorld(roomWorld, true);
      // RoomWorlds.houseWorldBungeeInfoArrayList.remove(roomWorld.getWorldUUID());
      Rooms.debug("system path: " + Rooms.getPlugin().getDataFolder().getAbsolutePath());// system path:
                                                                                         // /home/creative/CreativeEU1/plugins/Rooms
      File bob = new File(
          Rooms.getPlugin().getDataFolder().getParent() + "/WorldGuard/worlds/" + roomWorld.getWorldUUID().toString());
      bob.deleteOnExit();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void deleteRoomWorld(RoomWorld roomWorld) throws UnknownWorldException, IOException {
    Rooms.debug("");
    World world = Bukkit.getWorld(roomWorld.getWorldUUID().toString());
    assert world != null;
    UUID worlduuid = roomWorld.getWorldUUID();
    Bukkit.unloadWorld(world, false);
    sqlLoader.deleteWorld(roomWorld.getWorldUUID().toString());
    RoomWorlds.removeRoomWorld(worlduuid);
    Rooms.mysql.deleteRoom(worlduuid);
    Rooms.redis.delete(worlduuid);

  }

  public void loadWorld(RoomWorld roomWorld)
      throws CorruptedWorldException, NewerFormatException, WorldInUseException, UnknownWorldException, IOException {
    SlimePropertyMap properties = new SlimePropertyMap();
    properties.setValue(SlimeProperties.WORLD_TYPE, "flat");
    properties.setValue(SlimeProperties.ENVIRONMENT, roomWorld.getEnviroment());
    properties.setValue(SlimeProperties.DIFFICULTY, "normal");
    properties.setValue(SlimeProperties.SPAWN_X, 1);
    properties.setValue(SlimeProperties.SPAWN_Y, roomWorld.getSpawnY());
    properties.setValue(SlimeProperties.SPAWN_Z, 1);
    properties.setValue(SlimeProperties.ALLOW_MONSTERS, false);
    properties.setValue(SlimeProperties.ALLOW_ANIMALS, false);
    Rooms.debug("allowanimals: " + properties.getValue(SlimeProperties.ALLOW_ANIMALS));
    // properties.setValue(SlimeProperties.ALLOW_ANIMALS,
    // Rooms.configs.getGeneralConfig().getBoolean("spawnanimals"));
    // properties.setValue(SlimeProperties.ALLOW_MONSTERS,
    // Rooms.configs.getGeneralConfig().getBoolean("spawnmonsters"));

    SlimeWorld world = plugin.loadWorld(sqlLoader, roomWorld.getWorldUUID().toString(), false, properties);
    plugin.generateWorld(world);
    Objects.requireNonNull(Bukkit.getWorld(roomWorld.getWorldUUID().toString())).setGameRule(GameRule.DO_MOB_SPAWNING,
        false);
    Objects.requireNonNull(Bukkit.getWorld(roomWorld.getWorldUUID().toString())).setGameRule(GameRule.DO_FIRE_TICK,
        false);
    // plugin.loadWorld(sqlLoader,houseWorld.getWorldUUID().toString(),false,properties);

  }

  public static void updateBorder(RoomWorld roomWorld) {
    if (roomWorld.isLoaded() && Bukkit.getOfflinePlayer(roomWorld.getOwnerUUID()).isOnline()) {
      int wbsize = Rooms.configs.getGeneralConfig().getInt("worldborder");
      double realsize = getBorderperm(Bukkit.getPlayer(roomWorld.getOwnerUUID()), wbsize);
      Bukkit.getWorld(roomWorld.getWorldUUID().toString()).getWorldBorder().setSize(realsize);
      switch (roomWorld.getBorderColor()) {
        case "blue":
          break;
        case "green":
          Bukkit.getWorld(roomWorld.getWorldUUID().toString()).getWorldBorder().setSize(realsize + 0.4, 10000);
          break;
        case "red":
          Bukkit.getWorld(roomWorld.getWorldUUID().toString()).getWorldBorder().setSize(realsize - 0.4, 10000);
          break;
        case "":
          switch (Rooms.configs.getGeneralConfig().getString("bordercolour")) {
            case "blue":
              break;
            case "green":
              Bukkit.getWorld(roomWorld.getWorldUUID().toString()).getWorldBorder().setSize(realsize + 0.4, 10000);
              break;
            case "red":
              Bukkit.getWorld(roomWorld.getWorldUUID().toString()).getWorldBorder().setSize(realsize - 0.4, 10000);
              break;
          }
      }

    }
  }

  public static void defaultBorder(String worldname) {
    int realsize = Rooms.configs.getGeneralConfig().getInt("worldborder");
    Bukkit.getWorld(worldname).getWorldBorder().setSize(realsize);
    switch (Rooms.configs.getGeneralConfig().getString("bordercolour")) {
      case "blue":
        break;
      case "green":
        Bukkit.getWorld(worldname).getWorldBorder().setSize(realsize + 0.4, Long.MAX_VALUE);
        break;
      case "red":
        Bukkit.getWorld(worldname).getWorldBorder().setSize(realsize - 0.4, Long.MAX_VALUE);
        break;
    }
  }

  public static double getBorderperm(Player player, double defaultValue) {
    String permissionPrefix = "housing.worldborder.";

    for (PermissionAttachmentInfo attachmentInfo : player.getEffectivePermissions()) {
      String permission = attachmentInfo.getPermission();
      if (permission.startsWith(permissionPrefix)) {
        Rooms.debug("permission1; " + permission);
        return Integer.parseInt(permission.substring(permission.lastIndexOf(".") + 1));
      }
    }

    return defaultValue;
  }

  private final static Pattern UUID_REGEX_PATTERN = Pattern
      .compile("^[{]?[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}[}]?$");

  public static boolean isValidUUID(String str) {
    if (str == null) {
      return false;
    }
    return UUID_REGEX_PATTERN.matcher(str).matches();
  }
}
