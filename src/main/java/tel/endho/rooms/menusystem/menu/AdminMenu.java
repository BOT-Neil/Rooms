package tel.endho.rooms.menusystem.menu;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import tel.endho.rooms.RoomWorlds;
import tel.endho.rooms.Rooms;
import tel.endho.rooms.Tasks.MigrateOnePlot;
import tel.endho.rooms.menusystem.Menu;
import tel.endho.rooms.menusystem.PlayerMenuUtility;

import java.util.ArrayList;

public class AdminMenu extends Menu {

  public AdminMenu(PlayerMenuUtility playerMenuUtility) {
    super(playerMenuUtility);
  }

  @Override
  public String getMenuName() {
    return "RoomManagerAdmin";
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
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Rooms.getPlugin(), new MigrateOnePlot(), 0, 160);
        // Rooms.housemanager.migrateAll();
        break;
      case SUNFLOWER:
        Rooms.roomWorldManager.migrateOnePlot(playerMenuUtility.getOwner());
      case BOOK:
        switch (e.getCurrentItem().getItemMeta().getPersistentDataContainer()
            .get(new NamespacedKey(Rooms.getPlugin(), "menu"), PersistentDataType.STRING)) {
          case "personal" -> {
            playerMenuUtility.setPlayerGetHouseList((Player) e.getWhoClicked());
            new LoadRoomMenu(Rooms.getPlayerMenuUtility((Player) e.getWhoClicked())).open();
          }
          case "local" -> new LocalRoomsMenu(Rooms.getPlayerMenuUtility((Player) e.getWhoClicked())).open();
        }
        break;
      case COMPARATOR:
        if (e.getSlot() == 6) {
          //
        }
      default:
        break;
    }

  }

  @Override
  public void setMenuItems() {
    ItemStack createHouse = new ItemStack(Material.NETHER_STAR, 1);
    ItemMeta grass_meta = createHouse.getItemMeta();
    grass_meta.setDisplayName(ChatColor.GREEN + "Migrate All Plots");
    ArrayList<String> yes_lore = new ArrayList<>();
    yes_lore.add(ChatColor.AQUA + "Click here to migrate");
    yes_lore.add(ChatColor.AQUA + "Bye Bye Plotsqaured");
    grass_meta.setLore(yes_lore);
    createHouse.setItemMeta(grass_meta);

    ItemStack MigrateOne = new ItemStack(Material.SUNFLOWER, 1);
    ItemMeta MigrateOnePlotItemMeta = MigrateOne.getItemMeta();
    MigrateOnePlotItemMeta.setDisplayName(ChatColor.DARK_RED + "Migrate one plot to Rooms");
    MigrateOnePlotItemMeta.getPersistentDataContainer().set(new NamespacedKey(Rooms.getPlugin(), "menu"),
        PersistentDataType.STRING, "personal");
    ArrayList<String> personal_lore = new ArrayList<>();
    personal_lore.add(ChatColor.AQUA + "Migrate one plot to Rooms");
    MigrateOnePlotItemMeta.setLore(personal_lore);
    ArrayList<String> personalHousesLore = new ArrayList<>();
    personalHousesLore.add(ChatColor.AQUA + "Migrate one plot to Rooms");
    MigrateOne.setItemMeta(MigrateOnePlotItemMeta);
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

    ItemStack adminmenu = new ItemStack(Material.COMPARATOR);
    ItemMeta adminmenuMeta = adminmenu.getItemMeta();
    adminmenuMeta.setDisplayName(ChatColor.DARK_RED + "Admin");
    inventory.setItem(1, createHouse);
    inventory.setItem(2, MigrateOne);
    // inventory.setItem(3, localRooms);
    // inventory.setItem(4, globalRooms);
    // inventory.setItem(5, settings);

    setFillerGlass();

  }

}
