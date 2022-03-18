package tel.endho.rooms.menusystem.menu;

import com.grinderwolf.swm.api.exceptions.CorruptedWorldException;
import com.grinderwolf.swm.api.exceptions.NewerFormatException;
import com.grinderwolf.swm.api.exceptions.UnknownWorldException;
import com.grinderwolf.swm.api.exceptions.WorldInUseException;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import tel.endho.rooms.*;
import tel.endho.rooms.menusystem.PaginatedMenu;
import tel.endho.rooms.menusystem.PlayerMenuUtility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class GlobalRoomsMenu extends PaginatedMenu {

  public GlobalRoomsMenu(PlayerMenuUtility playerMenuUtility) {
    super(playerMenuUtility);
  }

  @Override
  public String getMenuName() {
    return "Choose a global room to visit";
  }

  @Override
  public int getSlots() {
    return 54;
  }

  @Override
  public void handleMenu(InventoryClickEvent e)
      throws CorruptedWorldException, NewerFormatException, WorldInUseException, UnknownWorldException, IOException {
    Player p = (Player) e.getWhoClicked();
    ArrayList<GlobalRoomWorld> globalrooms = new ArrayList<>(GlobalRoomWorlds.getGlobalRoomWorlds().values());
    // ArrayList<Player> players = new
    // ArrayList<Player>(getServer().getOnlinePlayers());

    if (e.getCurrentItem().getType().equals(Material.GRASS_BLOCK)
        || e.getCurrentItem().getType().equals(Material.ENDER_EYE)
        || e.getCurrentItem().getType().equals(Material.PODZOL)) {
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
        if (!((index + 1) >= globalrooms.size())) {
          page = page + 1;
          super.open();
        } else {
          p.sendMessage(ChatColor.GRAY + "You are on the last page.");
        }
      }
    }
  }

  @Override
  public void setMenuItems() {

    addMenuBorder();

    ArrayList<GlobalRoomWorld> globalrooms = new ArrayList<>();
    GlobalRoomWorlds.getGlobalRoomWorlds().values().forEach(globalRoomWorld -> {
      globalrooms.add(globalRoomWorld);
      /*
       * if(!globalRoomWorld.getLastserver().equals(Rooms.configs.getStorageConfig().
       * getString("bungeeservername"))){
       * globalrooms.add(globalRoomWorld);//todo just add all
       * }
       */
    });
    ///////////////////////////////////// Pagination loop template
    if (!globalrooms.isEmpty()) {
      for (int i = 0; i < getMaxItemsPerPage(); i++) {
        index = getMaxItemsPerPage() * page + i;
        if (index >= globalrooms.size())
          break;
        if (globalrooms.get(index) != null) {
          ///////////////////////////

          // Create an item from our collection and place it into the inventory
          ItemStack roomItem = new ItemStack(Material.PODZOL, 1);
          ItemMeta playerMeta = roomItem.getItemMeta();
          playerMeta.setDisplayName(ChatColor.RED + globalrooms.get(index).getOwnername());

          playerMeta.getPersistentDataContainer().set(new NamespacedKey(Rooms.instance, "uuid"),
              PersistentDataType.STRING, globalrooms.get(index).getWorldUUID().toString());
          roomItem.setItemMeta(playerMeta);

          inventory.addItem(roomItem);

          ////////////////////////
        }
      }
    }
    ////////////////////////

  }
}
