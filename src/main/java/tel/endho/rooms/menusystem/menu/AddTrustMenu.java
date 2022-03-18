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
import tel.endho.rooms.menusystem.PaginatedMenu;
import tel.endho.rooms.menusystem.PlayerMenuUtility;

import java.util.ArrayList;

import static org.bukkit.Bukkit.getServer;

public class AddTrustMenu extends PaginatedMenu {

  public AddTrustMenu(PlayerMenuUtility playerMenuUtility) {
    super(playerMenuUtility);
  }

  @Override
  public String getMenuName() {
    return "Click on a player to trust them";
  }

  @Override
  public int getSlots() {
    return 54;
  }

  @Override
  public void handleMenu(InventoryClickEvent e) {
    Player p = (Player) e.getWhoClicked();

    ArrayList<Player> players = new ArrayList<Player>(getServer().getOnlinePlayers());

    /*if (e.getCurrentItem().getType().equals(Material.PLAYER_HEAD)) {

      PlayerMenuUtility playerMenuUtility = Rooms.getPlayerMenuUtility(p);
      playerMenuUtility.setPlayerToKill(Bukkit.getPlayer(UUID.fromString(e.getCurrentItem().getItemMeta()
          .getPersistentDataContainer().get(new NamespacedKey(Rooms.getPlugin(), "uuid"), PersistentDataType.STRING))));

      new KillConfirmMenu(playerMenuUtility).open();

    } else */
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
        if (!((index + 1) >= players.size())) {
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

    // The thing you will be looping through to place items
    ArrayList<Player> players = new ArrayList<Player>(getServer().getOnlinePlayers());

    ///////////////////////////////////// Pagination loop template
    if (players != null && !players.isEmpty()) {
      for (int i = 0; i < getMaxItemsPerPage(); i++) {
        index = getMaxItemsPerPage() * page + i;
        if (index >= players.size())
          break;
        if (players.get(index) != null) {
          ///////////////////////////

          // Create an item from our collection and place it into the inventory
          ItemStack playerItem = new ItemStack(Material.PLAYER_HEAD, 1);
          ItemMeta playerMeta = playerItem.getItemMeta();
          playerMeta.setDisplayName(ChatColor.RED + players.get(index).getDisplayName());

          playerMeta.getPersistentDataContainer().set(new NamespacedKey(Rooms.getPlugin(), "uuid"),
              PersistentDataType.STRING, players.get(index).getUniqueId().toString());
          playerItem.setItemMeta(playerMeta);

          inventory.addItem(playerItem);

          ////////////////////////
        }
      }
    }
    ////////////////////////

  }
}
