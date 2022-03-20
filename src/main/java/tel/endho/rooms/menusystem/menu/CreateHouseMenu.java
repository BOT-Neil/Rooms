package tel.endho.rooms.menusystem.menu;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import tel.endho.rooms.Rooms;
import tel.endho.rooms.menusystem.Menu;
import tel.endho.rooms.menusystem.PaginatedMenu;
import tel.endho.rooms.menusystem.PlayerMenuUtility;
import tel.endho.rooms.util.Preset;

import java.util.ArrayList;
import java.util.Map;

public class CreateHouseMenu extends PaginatedMenu {

  public CreateHouseMenu(PlayerMenuUtility playerMenuUtility) {
    super(playerMenuUtility);
  }

  @Override
  public String getMenuName() {
    return "Choose a local Player to Visit";
  }

  @Override
  public int getSlots() {
    return 54;
  }

  @Override
  public void handleMenu(InventoryClickEvent e){
    Player p = (Player) e.getWhoClicked();
    //ArrayList<RoomWorld> playerhouses = new ArrayList<>(RoomWorlds.getLoadedRoomWorlds().values());
    Map<Integer, Preset> presetMap = Rooms.roomWorldManager.getPresetMap();
    if(e.getCurrentItem().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(Rooms.getPlugin(), "UUID"),PersistentDataType.STRING)){
      Rooms.roomWorldManager.createWorld(worldtype, player);
    }
    int indexxx= Integer.parseInt(e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Rooms.getPlugin(), "UUID"), PersistentDataType.STRING));    
    if (e.getCurrentItem().getType().equals(Material.GRASS_BLOCK)
        || e.getCurrentItem().getType().equals(Material.ENDER_EYE)) {
      // PlayerMenuUtility playerMenuUtility = Rooms.getPlayerMenuUtility(p);
      // playerMenuUtility.setPlayerToKill(Bukkit.getPlayer(UUID.fromString(e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(new
      // NamespacedKey(Rooms.getPlugin(), "uuid"), PersistentDataType.STRING))));
      // playerMenuUtility.setPlayerGetHouseList(Bukkit.getPlayer(UUID.fromString(e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(new
      // NamespacedKey(Rooms.getPlugin(),"UUID"),PersistentDataType.STRING))));
      Rooms.roomWorldManager.TpOrLoadHouseWorld((Player) e.getWhoClicked(),
          UUID.fromString(e.getCurrentItem().getItemMeta().getPersistentDataContainer()
              .get(new NamespacedKey(Rooms.getPlugin(), "UUID"), PersistentDataType.STRING)));
      // new LoadRoomMenu(playerMenuUtility).open();
      // new KillConfirmMenu(playerMenuUtility).open();

    } else if (e.getCurrentItem().getType().equals(Material.BARRIER)) {
      new MainMenu(playerMenuUtility).open();
      // close inventory
      // p.closeInventory();

    } else if (e.getCurrentItem().getType().equals(Material.DARK_OAK_BUTTON)) {
      if (ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Left")) {
        if (page == 0) {
          p.sendMessage(ChatColor.GRAY + "You are already on the first page.");
        } else {
          page = page - 1;
          super.open();
        }
      } else if (ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Right")) {
        if (!((index + 1) >= playerhouses.size())) {
          page = page + 1;
          super.open();
        } else {
          p.sendMessage(ChatColor.GRAY + "You are on the last page.");
        }
      }
    }
  

  @Override
  public void setMenuItems() {

    addMenuBorder();

    // The thing you will be looping through to place items
    // ArrayList<Player> players = new
    // ArrayList<Player>(getServer().getOnlinePlayers());
    //ArrayList<RoomWorld> playerhouses = new ArrayList<>(RoomWorlds.getLoadedRoomWorlds().values());
    Map<Integer, Preset> presetMap = Rooms.roomWorldManager.getPresetMap();

    ///////////////////////////////////// Pagination loop template
    if (presetMap != null && !presetMap.isEmpty()) {
      for (int i = 0; i < getMaxItemsPerPage(); i++) {
        index = getMaxItemsPerPage() * page + i;
        if (index >= presetMap.size())
          break;
        if (presetMap.get(index) != null) {
          ///////////////////////////
          final Preset preset = presetMap.get(index);
          // Create an item from our collection and place it into the inventory
          ItemStack presetIcon = new ItemStack(preset.getIconMaterial());
          ItemMeta presetIconMeta = presetIcon.getItemMeta();
          presetIconMeta.setDisplayName(ChatColor.RED + presetMap.get(index).getName());//todo parse hex
          presetIconMeta.getPersistentDataContainer().set(new NamespacedKey(Rooms.instance, "presetid"),
              PersistentDataType.STRING, String.valueOf(index));
          presetIcon.setItemMeta(presetIconMeta);

          inventory.addItem(presetIcon);

          ////////////////////////
        }
      }
    }
    ////////////////////////

  }
}
