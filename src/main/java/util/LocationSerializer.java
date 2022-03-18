import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationSerializer {

  /*public static String getSerializedLocation(Location loc) { // Converts location -> String
    return loc.getX() + ";" + loc.getY() + ";" + loc.getZ() + ";" + loc.getYaw() + ";" + loc.getPitch()
        + ";" + loc.getWorld().getUID();
    // feel free to use something to split them other than semicolons (Don't use
    // periods or numbers)
  }

  public static Location getDeserializedLocation(String s) {// Converts String -> Location
    String[] parts = s.split(";"); // If you changed the semicolon you must change it here too
    double x = Double.parseDouble(parts[0]);
    double y = Double.parseDouble(parts[1]);
    double z = Double.parseDouble(parts[2]);
    float yaw = Float.parseFloat(parts[3]);
    float pitch = Float.parseFloat(parts[4]);
    UUID u = UUID.fromString(parts[5]);
    World w = Bukkit.getServer().getWorld(u);
    return new Location(w, x, y, z, yaw, pitch); // can return null if the world no longer exists
  }*/

  public static String getSerializedPresetLocation(Location loc) { // Converts location -> String
    return loc.getX() + ";" + loc.getY() + ";" + loc.getZ() + ";" + loc.getYaw() + ";" + loc.getPitch();
    // feel free to use something to split them other than semicolons (Don't use
    // periods or numbers)
  }

  public static Location getDeserializedPresetLocation(String s, String worldname) {// Converts String -> Location
    String[] parts = s.split(";"); // If you changed the semicolon you must change it here too
    double x = Double.parseDouble(parts[0]);
    double y = Double.parseDouble(parts[1]);
    double z = Double.parseDouble(parts[2]);
    float yaw = Float.parseFloat(parts[3]);
    float pitch = Float.parseFloat(parts[4]);
    World w = Bukkit.getServer().getWorld(worldname);
    return new Location(w, x, y, z, yaw, pitch); // can return null if the world no longer exists
  }
}