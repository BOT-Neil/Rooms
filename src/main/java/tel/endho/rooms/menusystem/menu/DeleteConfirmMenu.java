package tel.endho.rooms.menusystem.menu;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.infernalsuite.aswm.api.exceptions.UnknownWorldException;

import tel.endho.rooms.RoomWorld;
import tel.endho.rooms.RoomWorlds;
import tel.endho.rooms.Rooms;
import tel.endho.rooms.menusystem.Menu;
import tel.endho.rooms.menusystem.PlayerMenuUtility;

import java.io.IOException;
import java.util.ArrayList;

public class DeleteConfirmMenu extends Menu {

  public DeleteConfirmMenu(PlayerMenuUtility playerMenuUtility) {
    super(playerMenuUtility);
  }

  @Override
  public String getMenuName() {
    return "Delete your room? ";
  }

  @Override
  public int getSlots() {
    return 9;
  }

  @Override
  public void handleMenu(InventoryClickEvent e) {
    if (RoomWorlds.isRoomWorld(playerMenuUtility.getOwner().getLocation().getWorld().getName())
        || playerMenuUtility.getOwner().hasPermission("rooms.admin")) {
      RoomWorld roomWorld = RoomWorlds
          .getRoomWorldString(playerMenuUtility.getOwner().getLocation().getWorld().getName());
      switch (e.getCurrentItem().getType()) {
        case EMERALD:
          try {
            Rooms.roomWorldManager.deleteRoomWorld(roomWorld);
          } catch (UnknownWorldException | IOException ex) {
            ex.printStackTrace();
          }
          break;
        case BARRIER:

          break;
        default:
          break;
      }
    }

  }

  @Override
  public void setMenuItems() {

    ItemStack yes = new ItemStack(Material.EMERALD, 1);
    ItemMeta yes_meta = yes.getItemMeta();
    yes_meta.setDisplayName(ChatColor.GREEN + "Yes");
    ArrayList<String> yes_lore = new ArrayList<>();
    yes_lore.add(ChatColor.AQUA + "Would you like to delete this room?");
    yes_meta.setLore(yes_lore);
    yes.setItemMeta(yes_meta);
    ItemStack no = new ItemStack(Material.BARRIER, 1);
    ItemMeta no_meta = no.getItemMeta();
    no_meta.setDisplayName(ChatColor.DARK_RED + "No");
    no.setItemMeta(no_meta);

    inventory.setItem(3, yes);
    inventory.setItem(5, no);

    setFillerGlass();

  }

}
