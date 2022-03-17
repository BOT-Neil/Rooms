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
import tel.endho.rooms.RoomWorld;
import tel.endho.rooms.RoomWorlds;
import tel.endho.rooms.Rooms;
import tel.endho.rooms.menusystem.PaginatedMenu;
import tel.endho.rooms.menusystem.PlayerMenuUtility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class LocalRoomsMenu extends PaginatedMenu {

    public LocalRoomsMenu(PlayerMenuUtility playerMenuUtility) {
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
    public void handleMenu(InventoryClickEvent e) throws CorruptedWorldException, NewerFormatException, WorldInUseException, UnknownWorldException, IOException {
        Player p = (Player) e.getWhoClicked();
        ArrayList<RoomWorld>playerhouses= new ArrayList<>(RoomWorlds.getLoadedRoomWorlds().values());

        if (e.getCurrentItem().getType().equals(Material.GRASS_BLOCK)||e.getCurrentItem().getType().equals(Material.ENDER_EYE)) {
            //PlayerMenuUtility playerMenuUtility = Rooms.getPlayerMenuUtility(p);
            //playerMenuUtility.setPlayerToKill(Bukkit.getPlayer(UUID.fromString(e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Rooms.getPlugin(), "uuid"), PersistentDataType.STRING))));
            //playerMenuUtility.setPlayerGetHouseList(Bukkit.getPlayer(UUID.fromString(e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Rooms.getPlugin(),"UUID"),PersistentDataType.STRING))));
            Rooms.roomWorldManager.TpOrLoadHouseWorld((Player)e.getWhoClicked(),UUID.fromString(e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Rooms.getPlugin(),"UUID"),PersistentDataType.STRING)));
            //new LoadRoomMenu(playerMenuUtility).open();
            //new KillConfirmMenu(playerMenuUtility).open();

        }else if (e.getCurrentItem().getType().equals(Material.BARRIER)) {
            new MainMenu(playerMenuUtility).open();
            //close inventory
            //p.closeInventory();

        }else if(e.getCurrentItem().getType().equals(Material.DARK_OAK_BUTTON)){
            if (ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Left")){
                if (page == 0){
                    p.sendMessage(ChatColor.GRAY + "You are already on the first page.");
                }else{
                    page = page - 1;
                    super.open();
                }
            }else if (ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Right")){
                if (!((index + 1) >= playerhouses.size())){
                    page = page + 1;
                    super.open();
                }else{
                    p.sendMessage(ChatColor.GRAY + "You are on the last page.");
                }
            }
        }
    }

    @Override
    public void setMenuItems() {

        addMenuBorder();

        //The thing you will be looping through to place items
        //ArrayList<Player> players = new ArrayList<Player>(getServer().getOnlinePlayers());
        ArrayList<RoomWorld>playerhouses= new ArrayList<>(RoomWorlds.getLoadedRoomWorlds().values());
        ///////////////////////////////////// Pagination loop template
        if(playerhouses != null && !playerhouses.isEmpty()) {
            for(int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * page + i;
                if(index >= playerhouses.size()) break;
                if (playerhouses.get(index) != null){
                    ///////////////////////////

                    //Create an item from our collection and place it into the inventory
                    ItemStack playerItem = switch (playerhouses.get(index).getEnviroment()) {
                        case "normal" -> new ItemStack(Material.GRASS_BLOCK, 1);
                        case "nether" -> new ItemStack(Material.NETHERRACK, 1);
                        case "the_end" -> new ItemStack(Material.ENDER_EYE, 1);
                        default -> throw new IllegalStateException("Unexpected value: " + playerhouses.get(index).getEnviroment());
                    };
                    ItemMeta playerMeta = playerItem.getItemMeta();
                    playerMeta.setDisplayName(ChatColor.RED + playerhouses.get(index).getOwnerName());

                    playerMeta.getPersistentDataContainer().set(new NamespacedKey(Rooms.instance, "uuid"), PersistentDataType.STRING, playerhouses.get(index).getWorldUUID().toString());
                    playerItem.setItemMeta(playerMeta);

                    inventory.addItem(playerItem);

                    ////////////////////////
                }
            }
        }
        ////////////////////////


    }
}
