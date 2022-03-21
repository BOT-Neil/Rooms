package tel.endho.rooms.storage;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import tel.endho.rooms.Rooms;
import tel.endho.rooms.util.Preset;

import java.io.File;
import java.io.IOException;

public class Configs {
  private FileConfiguration storageConfig;
  private FileConfiguration generalConfig;
  private FileConfiguration presetConfig;

  public void loadConfigs() {
    createGeneralConfig();
    createStorageConfig();
    createPresetConfig();
    fillPresetmap();
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
    File mysqlConfigFile = new File(Rooms.getPlugin().getDataFolder(), "creation-preset.yml");
    if (!mysqlConfigFile.exists()) {
      mysqlConfigFile.getParentFile().mkdirs();
      Rooms.getPlugin().saveResource("creation-preset.yml", false);
    }

    storageConfig = new YamlConfiguration();
    try {
      storageConfig.load(mysqlConfigFile);
    } catch (IOException | InvalidConfigurationException e) {
      e.printStackTrace();
    }
  }

  private void fillPresetmap(){
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
          endfillblock, flatbedrock);
      Rooms.roomWorldManager.getPresetMap().put(id, preset);
    });
  }
}
