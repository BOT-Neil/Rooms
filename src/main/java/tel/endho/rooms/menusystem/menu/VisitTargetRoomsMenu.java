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
import tel.endho.rooms.RoomWorld;
import tel.endho.rooms.RoomWorlds;
import tel.endho.rooms.Rooms;
import tel.endho.rooms.menusystem.PaginatedMenu;
import tel.endho.rooms.menusystem.PlayerMenuUtility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.UUID;

import static org.bukkit.Bukkit.getServer;

public class VisitTargetRoomsMenu extends PaginatedMenu {
  private Boolean useUUID;
  private String target;
  private UUID targetuuid;
  public VisitTargetRoomsMenu(PlayerMenuUtility playerMenuUtility, String target) {
    super(playerMenuUtility);
    this.target = target;
    this.useUUID=false;
  }

  public VisitTargetRoomsMenu(PlayerMenuUtility playerMenuUtility, UUID uuid) {
    super(playerMenuUtility);
    this.targetuuid = uuid;
    this.useUUID =true;
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

    ArrayList<Player> players = new ArrayList<Player>(getServer().getOnlinePlayers());
    switch (e.getCurrentItem().getType()) {
      case ENDER_EYE, GRASS_BLOCK -> {
        Rooms.roomWorldManager.TpOrLoadHouseWorld(p,
            e.getCurrentItem().getItemMeta().getPersistentDataContainer()
                .get(new NamespacedKey(Rooms.getPlugin(), "uuid"), PersistentDataType.STRING));
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
    ArrayList<RoomWorld> playerhouses = new ArrayList<>();
    if(useUUID){
      if (Bukkit.getPlayer(targetuuid) == null) {
        playerhouses.addAll(RoomWorlds.getRoomWorldsPlayer(targetuuid).values());
      } else {
        playerhouses.addAll(RoomWorlds.getRoomWorldsPlayer(Bukkit.getPlayer(targetuuid)).values());
      }
    }else{
      if (Bukkit.getPlayer(target) == null) {
      playerhouses.addAll(RoomWorlds.getRoomWorldsPlayer(target).values());
    } else {
      playerhouses.addAll(RoomWorlds.getRoomWorldsPlayer(Bukkit.getPlayer(target)).values());
    }
    }
    
    // ArrayList<Player> players = new
    // ArrayList<Player>(getServer().getOnlinePlayers());
    playerhouses.sort(Comparator.comparingInt(RoomWorld::getRowid));
    ///////////////////////////////////// Pagination loop template
    if (!playerhouses.isEmpty()) {
      for (int i = 0; i < getMaxItemsPerPage(); i++) {
        index = getMaxItemsPerPage() * page + i;
        if (index >= playerhouses.size())
          break;
        if (playerhouses.get(index) != null) {
          ///////////////////////////

          // Create an item from our collection and place it into the inventory
          ItemStack itemStack = switch (playerhouses.get(index).getPreset().getmainEnvironment()) {
            case "normal" -> new ItemStack(Material.GRASS_BLOCK, 1);
            case "the_end" -> new ItemStack(Material.ENDER_EYE, 1);
            default -> new ItemStack(Material.BARRIER, 1);
          };
          ItemMeta playerMeta = itemStack.getItemMeta();
          playerMeta.setDisplayName(ChatColor.RED + String.valueOf(index) + ".");

          playerMeta.getPersistentDataContainer().set(new NamespacedKey(Rooms.instance, "uuid"),
              PersistentDataType.STRING, playerhouses.get(index).getWorldUUID().toString());
          itemStack.setItemMeta(playerMeta);

          inventory.addItem(itemStack);

          ////////////////////////
        }
      }
    }
    ////////////////////////

  }
}
