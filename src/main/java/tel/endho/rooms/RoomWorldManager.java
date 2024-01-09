package tel.endho.rooms;

import com.fastasyncworldedit.core.FaweAPI;
import com.infernalsuite.aswm.api.SlimePlugin;
import com.infernalsuite.aswm.api.exceptions.CorruptedWorldException;
import com.infernalsuite.aswm.api.exceptions.NewerFormatException;
import com.infernalsuite.aswm.api.exceptions.UnknownWorldException;
import com.infernalsuite.aswm.api.exceptions.WorldAlreadyExistsException;
import com.infernalsuite.aswm.api.exceptions.WorldLoadedException;
import com.infernalsuite.aswm.api.exceptions.WorldLockedException;
import com.infernalsuite.aswm.api.loaders.SlimeLoader;
import com.infernalsuite.aswm.api.world.SlimeWorld;
import com.infernalsuite.aswm.api.world.properties.SlimeProperties;
import com.infernalsuite.aswm.api.world.properties.SlimePropertyMap;
import com.plotsquared.core.PlotAPI;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.biome.BiomeTypes;
import com.sk89q.worldedit.world.block.BlockTypes;
import org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.scheduler.BukkitRunnable;

import tel.endho.rooms.util.Preset;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

public class RoomWorldManager {
  SlimePlugin plugin = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
  SlimeLoader sqlLoader = plugin.getLoader("mysql");
  /*
   * private static Map<Integer, Preset> presetMap = new HashMap<>();
   * 
   * public Map<Integer, Preset> getPresetMap() {
   * return presetMap;
   * }
   */

  public void migrateAll() {
    BukkitRunnable r = new BukkitRunnable() {
      @Override
      public void run() {
        PlotAPI api = new PlotAPI();
        api.getAllPlots().forEach(plot -> {
          try {
            migratePlot(plot);
          } catch (SQLException | WorldAlreadyExistsException | IOException | WorldLockedException |
                   UnknownWorldException e) {
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

  public void migrateOnePlot(Player player) {
    PlotAPI api = new PlotAPI();
    final PlotPlayer<?> pl = api.wrapPlayer(player.getUniqueId());
    assert pl != null;
    final Plot plot = pl.getCurrentPlot();
    if (plot == null) {
      return;
    }
    if (!player.getUniqueId().equals(plot.getOwner()) && !player.hasPermission("rooms.admin")) {
      return;
    }
    try {
      this.migratePlot(plot);
    } catch (WorldAlreadyExistsException | SQLException | IOException | WorldLockedException | UnknownWorldException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  public void migratePlot(Plot plot) throws SQLException, IOException, WorldAlreadyExistsException, WorldLockedException, UnknownWorldException {
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
      plugin.loadWorld(world);
      Map<String, Map<UUID, String>> groupsMap = new HashMap<>();
      Map<UUID, String> blocked = new HashMap<>();
      Map<UUID, String> trusted = new HashMap<>();
      Map<UUID, String> members = new HashMap<>();
      PlotAPI api = new PlotAPI();
      String pusername;
      try {
        pusername = api.getPlotSquared().getImpromptuUUIDPipeline().getSingle(plot.getOwner(), 1000L);
      } catch (Exception e) {
        Rooms.debug(e.toString());
        String generatedString = RandomStringUtils.random(4, true, true);
        pusername = ("migrated" + generatedString);
      }
      plot.getMembers().forEach(uuid -> {
        String username;
        try {
          username = api.getPlotSquared().getImpromptuUUIDPipeline().getSingle(uuid, 1000L);
        } catch (Exception e) {
          String generatedString = RandomStringUtils.random(4, true, true);
          username = ("migrated" + generatedString);
        }
        members.put(uuid, username);
      });
      plot.getTrusted().forEach(uuid -> {
        String username;
        try {
          username = api.getPlotSquared().getImpromptuUUIDPipeline().getSingle(uuid, 1000L);
        } catch (Exception e) {
          String generatedString = RandomStringUtils.random(4, true, true);
          username = ("migrated" + generatedString);
        }
        trusted.put(uuid, username);
      });
      plot.getDenied().forEach(uuid -> {
        String username;
        try {
          username = api.getPlotSquared().getImpromptuUUIDPipeline().getSingle(uuid, 1000L);
        } catch (Exception e) {
          String generatedString = RandomStringUtils.random(4, true, true);
          username = ("migrated" + generatedString);
        }
        blocked.put(uuid, username);
      });
      groupsMap.put("MEMBERS", members);
      groupsMap.put("TRUSTED", trusted);
      groupsMap.put("BLOCKED", blocked);
      Instant timestamp = Instant.ofEpochSecond(plot.getTimestamp());
      Rooms.mysql.insertMigratedRoomWorld(plot.getOwner(), pusername, world, properties, groupsMap,
          timestamp.toString());
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
        int plotsize = -(plot.getLargestRegion().getWidth() / 2);
        @SuppressWarnings("all")
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

  public void createWorld(Preset preset, Player player) {
    try {
      // Note that this method should be called asynchronously
      // SlimeWorld world = plugin.loadWorld(sqlLoader, "my-world", props);
      UUID worlduuid = UUID.randomUUID();
      SlimePropertyMap properties = new SlimePropertyMap();
      properties.setValue(SlimeProperties.WORLD_TYPE, "flat");
      properties.setValue(SlimeProperties.ENVIRONMENT, preset.getmainEnvironment());
      properties.setValue(SlimeProperties.DEFAULT_BIOME, preset.getmainBiome());
      properties.setValue(SlimeProperties.DIFFICULTY, "normal");
      properties.setValue(SlimeProperties.SPAWN_X, 1);
      properties.setValue(SlimeProperties.SPAWN_Y, Rooms.configs.getGeneralConfig().getInt("spawnheight"));
      properties.setValue(SlimeProperties.SPAWN_Z, 1);
      SlimeWorld world = plugin.createEmptyWorld(sqlLoader, String.valueOf(worlduuid), false, properties);
      // This method must be called synchronously
      plugin.loadWorld(world);
      Objects.requireNonNull(Bukkit.getWorld(worlduuid.toString())).setGameRule(GameRule.DO_MOB_SPAWNING, false);
      Objects.requireNonNull(Bukkit.getWorld(worlduuid.toString())).setGameRule(GameRule.DO_FIRE_TICK, false);
      BukkitRunnable r = new BukkitRunnable() {
        @SuppressWarnings("null")
        @Override
        public void run() {
          try (EditSession es2 = WorldEdit.getInstance().newEditSession(FaweAPI.getWorld(world.getName()))) {
            if (preset.getmainSchematic().equals("empty")) {
              es2.setBlock(0, 1, 0, BlockTypes.BEDROCK);
            } else if (preset.getmainSchematic().equals("flat")) {
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
            } else {
              // load schematic
              File file = new File(Rooms.getPlugin().getDataFolder() + "/schematics/" + preset.getmainSchematic());
              BlockVector3 to = BlockVector3.at(0, 0, 0);
              ClipboardFormat format = ClipboardFormats.findByFile(file);
              ClipboardReader reader = null;

              try {
                reader = format.getReader(new FileInputStream(file));
              } catch (IOException e) {
                e.printStackTrace();
              }
              Clipboard clipboard = null;
              try {
                clipboard = reader.read();
              } catch (IOException e) {
                e.printStackTrace();
              }
              @SuppressWarnings("all")
              Operation operation = new ClipboardHolder(clipboard)
                  .createPaste(es2)
                  .to(to)
                  .ignoreAirBlocks(false)
                  .build();
              Operations.complete(operation);

            }

            // do bedrock too
          }
        }
      };
      r.runTaskAsynchronously(Rooms.getPlugin());

      Rooms.mysql.insertRoomWorld(player, world, properties, preset);
      // HouseWorlds.getHouseWolrds().putIfAbsent(worlduuid,new HouseWorld(null));
    } catch (IOException | WorldAlreadyExistsException | SQLException | UnknownWorldException | WorldLockedException ex) {
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
      Preset preset = roomWorld.getPreset();
      SlimePropertyMap properties = new SlimePropertyMap();
      properties.setValue(SlimeProperties.WORLD_TYPE, "flat");
      properties.setValue(SlimeProperties.ENVIRONMENT, "nether");
      properties.setValue(SlimeProperties.DEFAULT_BIOME, preset.getnetherBiome());
      properties.setValue(SlimeProperties.DIFFICULTY, "normal");
      properties.setValue(SlimeProperties.SPAWN_X, 1);
      properties.setValue(SlimeProperties.SPAWN_Y, Rooms.configs.getGeneralConfig().getInt("spawnheight"));
      properties.setValue(SlimeProperties.SPAWN_Z, 1);
      SlimeWorld world = plugin.createEmptyWorld(sqlLoader, roomWorld.getWorldUUID().toString() + "rmnether", false,
          properties);
      // This method must be called synchronously
      plugin.loadWorld(world);
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
              // es2.getBlock(bv).getBlockType().getMaterial().toString();
              es2.setBiome(bv, BiomeTypes.SOUL_SAND_VALLEY);
            });
            // es2.setBiome((Region)region, BiomeTypes.CRIMSON_FOREST);
            TpOrLoadHouseWorld(player, "rmnether");
            /*
             * Bukkit.getScheduler().runTask(Rooms.getPlugin(), () -> {
             * Location loc = new
             * Location(Rooms.getPlugin().getServer().getWorld(world.getName()),
             * Double.valueOf(SlimeProperties.SPAWN_X.getDefaultValue()),
             * Rooms.configs.getGeneralConfig().getInt("spawnheight"),
             * Double.valueOf(SlimeProperties.SPAWN_Z.getDefaultValue()));
             * player.teleport(loc);
             * });
             */
            // do bedrock too
          } catch (CorruptedWorldException | NewerFormatException | WorldLoadedException | UnknownWorldException
              | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
      };
      r.runTaskAsynchronously(Rooms.getPlugin());

      roomWorld.setHasNether(true);
      // todo mysql update hasnether
      // Rooms.mysql.insertRoomWorld(player, world, properties);
      // HouseWorlds.getHouseWolrds().putIfAbsent(worlduuid,new HouseWorld(null));
    } catch (IOException | WorldAlreadyExistsException | UnknownWorldException | WorldLockedException ex) {
      /* Exception handling */
    }
  }

  public void TpOrLoadHouseWorld(Player p, String uuidstring)
      throws CorruptedWorldException, NewerFormatException, WorldLoadedException, UnknownWorldException, IOException {
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
      Rooms.redis.teleportPlayer(p, GlobalRoomWorlds.getGlobalRoomWorldUUID(realuuid).lastserver, realuuid, uuidsuffix);
      Rooms.getPlugin().sendPlayer(p, GlobalRoomWorlds.getGlobalRoomWorldUUID(realuuid).lastserver);
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
            loadWorld(roomWorld, p, uuidsuffix);// add island option
            /*
             * Location location = new Location(world, roomWorld.getSpawnX().doubleValue(),
             * roomWorld.getSpawnY().doubleValue(), roomWorld.getSpawnZ().doubleValue());
             * p.teleport(location);
             */
          }
        }

      }
    }
  }

  public void unloadRoomWorld(RoomWorld roomWorld) {
    World world = Bukkit.getWorld(roomWorld.getWorldUUID().toString());
    assert world != null;
    world.save();
    Rooms.debug("unloadWorld: " + Bukkit.unloadWorld(roomWorld.getWorldUUID().toString(), false));
    if (roomWorld.getHasNether()) {
      World nworld = Bukkit.getWorld(roomWorld.getWorldUUID().toString() + "rmnether");
      assert nworld != null;
      Rooms.debug("unloadWorld: " + Bukkit.unloadWorld(nworld, true));
    }
    // Bukkit.unloadWorld(world,true);
    try {
      Rooms.mysql.saveRoomWorld(roomWorld, true);
      // RoomWorlds.houseWorldBungeeInfoArrayList.remove(roomWorld.getWorldUUID());
      Rooms.debug("system path: " + Rooms.getPlugin().getDataFolder().getAbsolutePath());// system path:
      // /home/creative/CreativeEU1/plugins/Rooms
      // WorldGuardManager.unloadWorld(roomWorld.getWorldUUID());
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

  public void loadWorld(RoomWorld roomWorld, @Nullable Player player, String uuidsuffix)
      throws CorruptedWorldException, NewerFormatException, WorldLoadedException, UnknownWorldException, IOException {
    SlimePropertyMap properties = new SlimePropertyMap();
    Preset preset = roomWorld.getPreset();
    properties.setValue(SlimeProperties.WORLD_TYPE, "flat");
    switch (uuidsuffix) {
      case "rmnether":
        properties.setValue(SlimeProperties.ENVIRONMENT, "nether");
        properties.setValue(SlimeProperties.DEFAULT_BIOME, roomWorld.getPreset().getnetherBiome());
        break;
      case "rmend":
        properties.setValue(SlimeProperties.ENVIRONMENT, "the_end");
        break;
      default:
        properties.setValue(SlimeProperties.ENVIRONMENT, preset.getmainEnvironment());
        break;
    }

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
    // SlimeWorld world = plugin.loadWorld(sqlLoader,
    // roomWorld.getWorldUUID().toString(), false, properties);
    BukkitRunnable r = new BukkitRunnable() {
      @SuppressWarnings("null")
      @Override
      public void run() {
        try {
          SlimeWorld opworld = plugin
              .loadWorld(sqlLoader, roomWorld.getWorldUUID().toString() + uuidsuffix, false, properties);
          SlimeWorld world = opworld;
          BukkitRunnable r = new BukkitRunnable() {
            @SuppressWarnings("null")
            @Override
            public void run() {
              try {
                plugin.loadWorld(world);
                if (Bukkit.getWorld(roomWorld.getWorldUUID().toString() + uuidsuffix) != null) {
                  World world2 = Bukkit.getWorld(roomWorld.getWorldUUID().toString() + uuidsuffix);
                  world2.setGameRule(GameRule.DO_MOB_SPAWNING, false);
                  world2.setGameRule(GameRule.DO_FIRE_TICK, false);
                  WorldGuardManager.setupRoom(roomWorld, uuidsuffix);
                  // RegionContainer container =
                  // WorldGuard.getInstance().getPlatform().getRegionContainer();
                  // RegionManager regions = container.get(FaweAPI.getWorld(world2.getName()));
                  // regions.getRegion("__global__").getOwners().addPlayer(roomWorld.getOwnerUUID());
                }
                if (player != null) {
                  Location location = new Location(Bukkit.getWorld(roomWorld.getWorldUUID().toString() + uuidsuffix),
                      roomWorld.getSpawnX().doubleValue(),
                      roomWorld.getSpawnY().doubleValue(), roomWorld.getSpawnZ().doubleValue());
                  player.teleport(location);
                }
              } catch (Exception e) {
                e.printStackTrace();
              }

            }
          };
          // r.runTask(Rooms.getPlugin());
          r.runTask(Rooms.getPlugin());

        } catch (UnknownWorldException | CorruptedWorldException | NewerFormatException | WorldLockedException
            | IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }

      }
    };
    // r.runTask(Rooms.getPlugin());
    r.runTaskAsynchronously(Rooms.getPlugin());

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
