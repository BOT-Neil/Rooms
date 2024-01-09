package tel.endho.rooms.menusystem.menu;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import com.infernalsuite.aswm.api.exceptions.CorruptedWorldException;
import com.infernalsuite.aswm.api.exceptions.NewerFormatException;
import com.infernalsuite.aswm.api.exceptions.UnknownWorldException;
import com.infernalsuite.aswm.api.exceptions.WorldLoadedException;

import tel.endho.rooms.RoomWorld;
import tel.endho.rooms.RoomWorlds;
import tel.endho.rooms.Rooms;
import tel.endho.rooms.menusystem.PaginatedMenu;
import tel.endho.rooms.menusystem.PlayerMenuUtility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

public class LoadRoomMenu extends PaginatedMenu {

  public LoadRoomMenu(PlayerMenuUtility playerMenuUtility) {
    super(playerMenuUtility);
  }

  @Override
  public String getMenuName() {
    return "Choose room to visit";
  }

  @Override
  public int getSlots() {
    return 54;
  }

  @Override
  public void handleMenu(InventoryClickEvent e)
      throws IOException {
    Player p = (Player) e.getWhoClicked();

    // ArrayList<Player> players = new
    // ArrayList<Player>(getServer().getOnlinePlayers());
    switch (e.getCurrentItem().getType()) {
      case ENDER_EYE, GRASS_BLOCK, NETHERRACK -> {
        try {
          Rooms.roomWorldManager.TpOrLoadHouseWorld(p,
              e.getCurrentItem().getItemMeta().getPersistentDataContainer()
                  .get(new NamespacedKey(Rooms.getPlugin(), "uuid"), PersistentDataType.STRING));
        } catch (CorruptedWorldException | NewerFormatException | WorldLoadedException | UnknownWorldException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
      }
      default -> {
        break;
      }
    }
    if (e.getCurrentItem().getType().equals(Material.BARRIER)) {

      // close inventory
      p.closeInventory();

    } else if (e.getCurrentItem().getType().equals(Material.DARK_OAK_BUTTON)) {
      if (ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Left")) {
        if (page == 0) {
          p.sendMessage(ChatColor.GRAY + "You are already on the first page.");
        } else {
          page = page - 1;
          super.open();
        }
      } else if (ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Right")) {
        if (((index + 1) >= RoomWorlds.getRoomWorldsPlayer(playerMenuUtility.getPlayerGetHouseList()).size())) {
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
    // Map<UUID, HouseWorld> getHouseWorldsPlayer=
    // HouseWorlds.getHouseWorldsPlayer(playerMenuUtility.getOwner());
    ArrayList<RoomWorld> playerrooms = new ArrayList<>(
        RoomWorlds.getRoomWorldsPlayer(playerMenuUtility.getPlayerGetHouseList()).values());
    // The thing you will be looping through to place items
    // ArrayList<Player> players = new
    // ArrayList<Player>(getServer().getOnlinePlayers());
    playerrooms.sort(Comparator.comparingInt(RoomWorld::getRowid));
    ///////////////////////////////////// Pagination loop template
    if (!RoomWorlds.getRoomWorldsPlayer(playerMenuUtility.getPlayerGetHouseList()).isEmpty()) {
      for (int i = 0; i < getMaxItemsPerPage(); i++) {
        index = getMaxItemsPerPage() * page + i;
        if (index >= playerrooms.size())
          break;
        if (playerrooms.get(index) != null) {
          ///////////////////////////
          ;
          Rooms.debug("enviroment: " + playerrooms.get(index).getPreset());
          // Create an item from our collection and place it into the inventory
          ItemStack itemStack = switch (playerrooms.get(index).getPreset().getmainEnvironment()) {
            case "normal" -> new ItemStack(Material.GRASS_BLOCK, 1);
            case "nether" -> new ItemStack(Material.NETHERRACK, 1);
            case "the_end" -> new ItemStack(Material.ENDER_EYE, 1);
            default -> new ItemStack(Material.BARRIER, 1);
          };
          ItemMeta playerMeta = itemStack.getItemMeta();
          playerMeta.setDisplayName(ChatColor.RED + String.valueOf(index) + ".");

          playerMeta.getPersistentDataContainer().set(new NamespacedKey(Rooms.instance, "uuid"),
              PersistentDataType.STRING, playerrooms.get(index).getWorldUUID().toString());
          itemStack.setItemMeta(playerMeta);

          inventory.addItem(itemStack);

          ////////////////////////
        }
      }
    }
    ////////////////////////

  }
}
