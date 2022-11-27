package tel.endho.rooms.storage;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import tel.endho.rooms.Rooms;
import tel.endho.rooms.util.Preset;
import tel.endho.rooms.util.Presets;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.biome.BiomeTypes;

public class Configs {
  private List<String> mainBiomeList;
  private List<String> netherBiomeList;
  private FileConfiguration storageConfig;
  private FileConfiguration generalConfig;
  private FileConfiguration presetConfig;

  public void loadConfigs() {
    mainBiomeList = new ArrayList<>();
    netherBiomeList = new ArrayList<>();
    createGeneralConfig();
    createStorageConfig();
    createPresetConfig();
    fillPresetmap();
    fillBiomes();
  }

  public FileConfiguration getGeneralConfig() {
    return this.generalConfig;
  }

  public FileConfiguration getStorageConfig() {
    return this.storageConfig;
  }

  public FileConfiguration getPresetConfig() {
    return this.presetConfig;
  }

  private void createGeneralConfig() {
    File generalConfigFile = new File(Rooms.getPlugin().getDataFolder(), "general.yml");
    if (!generalConfigFile.exists()) {
      generalConfigFile.getParentFile().mkdirs();
      Rooms.getPlugin().saveResource("general.yml", false);
    }

    generalConfig = new YamlConfiguration();
    try {
      generalConfig.load(generalConfigFile);
    } catch (IOException | InvalidConfigurationException e) {
      e.printStackTrace();
    }
  }

  private void createStorageConfig() {
    File mysqlConfigFile = new File(Rooms.getPlugin().getDataFolder(), "storage.yml");
    if (!mysqlConfigFile.exists()) {
      mysqlConfigFile.getParentFile().mkdirs();
      Rooms.getPlugin().saveResource("storage.yml", false);
    }

    storageConfig = new YamlConfiguration();
    try {
      storageConfig.load(mysqlConfigFile);
    } catch (IOException | InvalidConfigurationException e) {
      e.printStackTrace();
    }
  }

  private void createPresetConfig() {
    File presetConfigFile = new File(Rooms.getPlugin().getDataFolder(), "preset.yml");
    if (!presetConfigFile.exists()) {
      presetConfigFile.getParentFile().mkdirs();
      Rooms.getPlugin().saveResource("preset.yml", false);
    }

    presetConfig = new YamlConfiguration();
    try {
      presetConfig.load(presetConfigFile);
    } catch (IOException | InvalidConfigurationException e) {
      e.printStackTrace();
    }
  }

  private void fillPresetmap() {
    presetConfig.getKeys(false).forEach(x -> {
      int id = Integer.parseInt(x);
      ConfigurationSection presetSection = presetConfig.getConfigurationSection(x);
      String name = presetSection.getString("name");
      String locString;
      String netherlocString;
      String endlocString;
      String mainEnvironmentString;
      String mainBiome;
      String netherBiome;
      String iconMaterial;
      String mainSchematic;
      String netherSchematic;
      String endSchematic;
      String mainfillblock;
      String netherfillblock;
      String endfillblock;
      Boolean flatbedrock;
      int fillsize;
      if (presetSection.contains("fillsize")) {
        fillsize = presetSection.getInt("fillsize");
      } else {
        fillsize = 69;
      }
      if (presetSection.contains("spawnloc")) {
        locString = presetSection.getString("spawnloc");
      } else {
        locString = "0;" + fillsize + ";0";
      }
      if (presetSection.contains("netherspawnloc")) {
        netherlocString = presetSection.getString("netherspawnloc");
      } else {
        netherlocString = "0;" + fillsize + ";0";
      }
      if (presetSection.contains("endspawnloc")) {
        endlocString = presetSection.getString("endspawnloc");
      } else {
        endlocString = "0;" + fillsize + ";0";
      }
      if (presetSection.contains("mainenvironment")) {
        mainEnvironmentString = presetSection.getString("mainenvironment");
      } else {
        mainEnvironmentString = "normal";
      }
      if (presetSection.contains("biome")) {
        mainBiome = presetSection.getString("biome");
      } else {
        mainBiome = "normal";
      }
      if (presetSection.contains("netherbiome")) {
        netherBiome = presetSection.getString("netherbiome");
      } else {
        netherBiome = "minecraft:crimson_forest";
      }
      if (presetSection.contains("iconmaterial")) {
        iconMaterial = presetSection.getString("iconmaterial");
      } else {
        iconMaterial = "GRASS_BLOCK";
      }
      if (presetSection.contains("mainschematic")) {
        mainSchematic = presetSection.getString("mainschematic");
      } else {
        mainSchematic = "flat";
      }
      if (presetSection.contains("netherschematic")) {
        netherSchematic = presetSection.getString("netherschematic");
      } else {
        netherSchematic = "flat";
      }
      if (presetSection.contains("endschematic")) {
        endSchematic = presetSection.getString("endschematic");
      } else {
        endSchematic = "flat";
      }
      if (presetSection.contains("fillmaterial")) {
        mainfillblock = presetSection.getString("fillmaterial");
      } else {
        mainfillblock = "GRASS_BLOCK";
      }
      if (presetSection.contains("nethermaterial")) {
        netherfillblock = presetSection.getString("nethermaterial");
      } else {
        netherfillblock = "NETHERRACK";
      }
      if (presetSection.contains("endmaterial")) {
        endfillblock = presetSection.getString("endmaterial");
      } else {
        endfillblock = "END_STONE";
      }
      if (presetSection.contains("fillbedrock")) {
        flatbedrock = presetSection.getBoolean("fillbedrock");
      } else {
        flatbedrock = true;
      }
      Preset preset = new Preset(name, locString, netherlocString, endlocString, mainEnvironmentString, mainBiome,
          netherBiome, iconMaterial, mainSchematic, netherSchematic, endSchematic, mainfillblock, netherfillblock,
          endfillblock, fillsize, flatbedrock);
      // Rooms.roomWorldManager.getPresetMap().put(id, preset);
      Presets.getPresetMap().put(id, preset);
    });
  }

  private void fillBiomes() {
    for (BiomeType bt : BiomeTypes.values()) {
      switch (bt.getId()) {
        case "nether" -> {
          break;
        }
        case "custom" -> {
          break;
        }
        case "minecraft:basalt_deltas" -> {
          netherBiomeList.add(bt.getId());
          break;
        }
        case "minecraft:crimson_forest" -> {
          netherBiomeList.add(bt.getId());
          break;
        }
        case "minecraft:nether_wastes" -> {
          netherBiomeList.add(bt.getId());
          break;
        }
        case "minecraft:soul_sand_valley" -> {
          netherBiomeList.add(bt.getId());
          break;
        }
        case "minecraft:warped_forest" -> {
          netherBiomeList.add(bt.getId());
          break;
        }
        default -> {
          mainBiomeList.add(bt.getId());
          break;
        }
      }
    }
  }
}
