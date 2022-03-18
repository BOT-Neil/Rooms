package tel.endho.rooms.storage;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import tel.endho.rooms.Rooms;

import java.io.File;
import java.io.IOException;

public class Configs {
  private FileConfiguration storageConfig;
  private FileConfiguration generalConfig;
  private FileConfiguration presetConfig;

  public void loadConfigs() {
    createGeneralConfig();
    createStorageConfig();
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
}
