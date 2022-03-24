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
import tel.endho.rooms.util.Preset;

import java.util.Map;

public class CreateRoomMenu extends PaginatedMenu {

  public CreateRoomMenu(PlayerMenuUtility playerMenuUtility) {
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
  public void handleMenu(InventoryClickEvent e) {
    Player p = (Player) e.getWhoClicked();
    Map<Integer, Preset> presetMap = Rooms.roomWorldManager.getPresetMap();
    // int indexxx=
    // Integer.parseInt(e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(new
    // NamespacedKey(Rooms.getPlugin(), "UUID"), PersistentDataType.STRING));

    if (e.getCurrentItem().getItemMeta().getPersistentDataContainer()
        .has(new NamespacedKey(Rooms.getPlugin(), "presetid"), PersistentDataType.STRING)) {
      int presetindex = Integer.parseInt(e.getCurrentItem().getItemMeta().getPersistentDataContainer()
          .get(new NamespacedKey(Rooms.getPlugin(), "presetid"), PersistentDataType.STRING));
      Rooms.roomWorldManager.createWorld(presetMap.get(presetindex), p);
    } else if (e.getCurrentItem().getType().equals(Material.BARRIER)) {
      new MainMenu(playerMenuUtility).open();
    } else if (e.getCurrentItem().getType().equals(Material.DARK_OAK_BUTTON)) {
      if (ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Left")) {
        if (page == 0) {
          p.sendMessage(ChatColor.GRAY + "You are already on the first page.");
        } else {
          page = page - 1;
          super.open();
        }
      } else if (ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Right")) {
        if (!((index + 1) >= presetMap.size())) {
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
    // ArrayList<Player> players = new
    // ArrayList<Player>(getServer().getOnlinePlayers());
    // ArrayList<RoomWorld> playerhouses = new
    // ArrayList<>(RoomWorlds.getLoadedRoomWorlds().values());
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
          presetIconMeta.setDisplayName(ChatColor.RED + presetMap.get(index).getName());// todo parse hex
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
