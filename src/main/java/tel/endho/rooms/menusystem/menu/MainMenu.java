package tel.endho.rooms.menusystem.menu;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.persistence.PersistentDataType;
import tel.endho.rooms.*;
import tel.endho.rooms.menusystem.Menu;
import tel.endho.rooms.menusystem.PlayerMenuUtility;
import tel.endho.rooms.util.Presets;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class MainMenu extends Menu {

  public MainMenu(PlayerMenuUtility playerMenuUtility) {
    super(playerMenuUtility);
  }

  @Override
  public String getMenuName() {
    return "RoomManager";
    // return "Kill " + playerMenuUtility.getPlayerToKill().getDisplayName();
  }

  @Override
  public int getSlots() {
    return 9;
  }

  @Override
  public void handleMenu(InventoryClickEvent e) {
    switch (e.getCurrentItem().getType()) {
      case NETHER_STAR:
        if (!(RoomWorlds.getRoomWorldsPlayer(playerMenuUtility.getOwner()).size() > getRoomLimitperm(
            playerMenuUtility.getOwner(), Rooms.configs.getGeneralConfig().getDouble("roomlimit")))) {
          if (Rooms.configs.getGeneralConfig().getBoolean("enablepresets")) {
            new CreateRoomMenu(playerMenuUtility).open();
            // options
            return;
          } else {
            Rooms.roomWorldManager.createWorld(Presets.getFirstPreset(), (Player) e.getWhoClicked());
            break;
          }

        } else {
          playerMenuUtility.getOwner().sendMessage("Buy more rooms at endho.tel");
          break;
        }
      case BOOK:
        switch (e.getCurrentItem().getItemMeta().getPersistentDataContainer()
            .get(new NamespacedKey(Rooms.getPlugin(), "menu"), PersistentDataType.STRING)) {
          case "personal" -> {
            playerMenuUtility.setPlayerGetHouseList((Player) e.getWhoClicked());
            new LoadRoomMenu(Rooms.getPlayerMenuUtility((Player) e.getWhoClicked())).open();
          }
          case "local" -> new LocalRoomsMenu(Rooms.getPlayerMenuUtility((Player) e.getWhoClicked())).open();
          case "global" -> new GlobalRoomsMenu(Rooms.getPlayerMenuUtility((Player) e.getWhoClicked())).open();
        }
        break;
      case COMPARATOR:
        if (e.getSlot() == 6) {
          new AdminMenu(playerMenuUtility).open();
          break;
          // Rooms.housemanager.migrateAll();
        }
      case HONEYCOMB:
        new SettingsMenu(playerMenuUtility).open();
        break;
      default:
        break;
    }

  }

  @Override
  public void setMenuItems() {
    ItemStack createHouse = new ItemStack(Material.NETHER_STAR, 1);
    ItemMeta grass_meta = createHouse.getItemMeta();
    grass_meta.setDisplayName(ChatColor.GREEN + "Create Room");
    ArrayList<String> yes_lore = new ArrayList<>();
    yes_lore.add(ChatColor.AQUA + "Click here to start");
    yes_lore
        .add(ChatColor.AQUA + "Starts at " + Rooms.configs.getGeneralConfig().getInt("worldborder") + " blocks wide");
    grass_meta.setLore(yes_lore);
    createHouse.setItemMeta(grass_meta);

    int personalroomsint = RoomWorlds.getRoomWorldsPlayer(playerMenuUtility.getOwner()).size();
    ItemStack personalRooms = new ItemStack(Material.BOOK, 1);
    ItemMeta personalHousesItemMeta = personalRooms.getItemMeta();
    personalHousesItemMeta.setDisplayName(ChatColor.DARK_RED + "Personal Rooms");
    personalHousesItemMeta.getPersistentDataContainer().set(new NamespacedKey(Rooms.getPlugin(), "menu"),
        PersistentDataType.STRING, "personal");
    ArrayList<String> personal_lore = new ArrayList<>();
    personal_lore.add(ChatColor.AQUA + "Your Rooms");
    personal_lore.add(ChatColor.AQUA + "Amount: " + personalroomsint);
    personalHousesItemMeta.setLore(personal_lore);
    personalRooms.setItemMeta(personalHousesItemMeta);

    int localroomsint = RoomWorlds.getLoadedRoomWorlds().size();
    ItemStack localRooms = new ItemStack(Material.BOOK, 1);
    ItemMeta localRoomsItemMeta = localRooms.getItemMeta();
    localRoomsItemMeta.setDisplayName(ChatColor.DARK_RED + "Local Rooms");
    localRoomsItemMeta.getPersistentDataContainer().set(new NamespacedKey(Rooms.getPlugin(), "menu"),
        PersistentDataType.STRING, "local");
    ArrayList<String> local_lore = new ArrayList<>();
    local_lore.add(ChatColor.AQUA + "Rooms loaded on this shard");
    local_lore.add(ChatColor.AQUA + "Amount: " + localroomsint);
    localRoomsItemMeta.setLore(local_lore);
    localRooms.setItemMeta(localRoomsItemMeta);

    AtomicInteger globalroomsint = new AtomicInteger();
    globalroomsint.set(0);
    if (GlobalRoomWorlds.getGlobalRoomWorlds() != null) {
      globalroomsint.set(GlobalRoomWorlds.getGlobalRoomWorlds().size());
    }
    ItemStack globalRooms = new ItemStack(Material.BOOK, 1);
    ItemMeta globalRoomsItemMeta = globalRooms.getItemMeta();
    globalRoomsItemMeta.setDisplayName(ChatColor.DARK_RED + "Global Rooms");
    globalRoomsItemMeta.getPersistentDataContainer().set(new NamespacedKey(Rooms.getPlugin(), "menu"),
        PersistentDataType.STRING, "global");
    ArrayList<String> global_lore = new ArrayList<>();
    global_lore.add(ChatColor.AQUA + "Rooms loaded on other shards");
    global_lore.add(ChatColor.AQUA + "or other regions");
    global_lore.add(ChatColor.AQUA + "Amount: " + globalroomsint.get());
    globalRoomsItemMeta.setLore(global_lore);
    globalRooms.setItemMeta(globalRoomsItemMeta);

    ItemStack settingsmenu = new ItemStack(Material.HONEYCOMB);
    ItemMeta settingsmenuMeta = settingsmenu.getItemMeta();
    settingsmenuMeta.setDisplayName(ChatColor.DARK_RED + "Room Settings");
    settingsmenu.setItemMeta(settingsmenuMeta);
    ItemStack adminmenu = new ItemStack(Material.COMPARATOR);
    ItemMeta adminmenuMeta = adminmenu.getItemMeta();
    adminmenuMeta.setDisplayName(ChatColor.DARK_RED + "Admin");
    inventory.setItem(1, createHouse);
    inventory.setItem(2, personalRooms);
    inventory.setItem(3, localRooms);
    inventory.setItem(4, globalRooms);
    System.out.println("bug123");
    try {
      String worldname = playerMenuUtility.getOwner().getLocation().getWorld().getName();
      System.out.println("bug1234");
      if (RoomWorlds.isRoomWorld(worldname)) {
        System.out.println("bug1235");
        RoomWorld roomWorld = RoomWorlds.getRoomWorldString(worldname);
        System.out.println("bug1236");
        System.out.println("ownername:"+playerMenuUtility.getOwner().getName());
        System.out.println("owneruuid:" + playerMenuUtility.getOwner().getUniqueId().toString());
        System.out.println("roomownername:" + roomWorld.getOwnerName());
        System.out.println("roomowneruuid:" + roomWorld.getOwnerUUID().toString());
        if (roomWorld.isOwner(playerMenuUtility.getOwner())) {
          System.out.println("bug1237");
          inventory.setItem(5, settingsmenu);
        }
      }
    } catch (Exception ignored) {
      System.out.println(ignored.toString());
    }

    if (playerMenuUtility.getOwner().hasPermission("rooms.admin")) {
      inventory.setItem(6, adminmenu);
    }

    setFillerGlass();

  }

  public static double getRoomLimitperm(Player player, double defaultValue) {
    String permissionPrefix = "rooms.limit.";

    for (PermissionAttachmentInfo attachmentInfo : player.getEffectivePermissions()) {
      String permission = attachmentInfo.getPermission();
      if (permission.startsWith(permissionPrefix)) {
        Rooms.debug("permission1; " + permission);
        return Integer.parseInt(permission.substring(permission.lastIndexOf(".") + 1));
      }
    }

    return defaultValue;
  }

}
