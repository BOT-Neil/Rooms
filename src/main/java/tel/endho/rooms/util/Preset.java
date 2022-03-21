package tel.endho.rooms.util;

import org.bukkit.Material;

public class Preset {
  private String name;
  private String spawnlocString;
  private String netherlocString;
  private String endlocString;
  private String mainEnvironment;
  private String mainBiome;
  private String netherBiome;
  private Material iconMaterial;
  private String mainSchematic;
  private String netherSchematic;
  private String endSchematic;
  private int fillHeight;
  private Material mainFillMaterial;
  private Material netherMaterial;
  private Material endMaterial;
  private Boolean fillBedrock;

  public Preset(String name, String spawnlocString,String netherlocString, String endlocString, String mainEnvString, String mainBiome, String netherBiome, String iconString,String mainschem,String netherschem, String endschem,String mainfillblock, String netherfillblock , String endfillblock, Boolean flatbedrock) {
    this.name = name;
    this.spawnlocString=spawnlocString;
    this.netherlocString=netherlocString;
    this.endlocString=endlocString;
    this.mainEnvironment=mainEnvString;
    this.mainBiome=mainBiome;
    this.netherBiome=netherBiome;
    this.iconMaterial=Material.getMaterial(iconString);
    this.mainSchematic=mainschem;
  }

  public String getName() {
    return name;
  }

  public String getSpawnlocString() {
    return spawnlocString;
  }
  public String getnetherSpawnlocString(){
    return netherlocString;
  }

  public String getendSpawnlocString() {
    return endlocString;
  }
  public String getmainEnvironment() {
    return mainEnvironment;
  }

  public String getmainBiome() {
    return mainBiome;
  }

  public String getnetherBiome() {
    return netherBiome;
  }

  public Material getIconMaterial() {
    return iconMaterial;
  }

  public String getmainSchematic() {
    return mainSchematic;
  }

  public String getnetherSchematic() {
    return netherSchematic;
  }

  public String getendSchematic() {
    return endSchematic;
  }

  public int getFillHeight() {
    return fillHeight;
  }

  public Material getmainFillMaterial() {
    return mainFillMaterial;
  }

  public Material getnetherMaterial() {
    return netherMaterial;
  }

  public Material getendMaterial() {
    return endMaterial;
  }

  public Boolean getFillBedrock() {
    return fillBedrock;
  }
}
