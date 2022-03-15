package tel.endho.rooms.menusystem.menu;


import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tel.endho.rooms.Rooms;
import tel.endho.rooms.menusystem.Menu;
import tel.endho.rooms.menusystem.PlayerMenuUtility;

import java.util.ArrayList;

public class CreateHouseMenu extends Menu {

    public CreateHouseMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public String getMenuName() {
        return "Create World - Choose Type";
        //return "Kill " + playerMenuUtility.getPlayerToKill().getDisplayName();
    }

    @Override
    public int getSlots() {
        return 9;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {

        switch (e.getCurrentItem().getType()){
            case GRASS_BLOCK:
                Rooms.roomWorldManager.createWorld("normal",(Player)e.getWhoClicked());
                e.getWhoClicked().closeInventory();
                break;
            case NETHERRACK:
                Rooms.roomWorldManager.createWorld("nether",(Player)e.getWhoClicked());
                e.getWhoClicked().closeInventory();
                break;
            case ENDER_EYE:
                Rooms.roomWorldManager.createWorld("the_end",(Player)e.getWhoClicked());
                e.getWhoClicked().closeInventory();
                break;
        }

    }

    @Override
    public void setMenuItems() {
        ItemStack grass = new ItemStack(Material.GRASS_BLOCK,1);
        ItemMeta grass_meta = grass.getItemMeta();
        grass_meta.setDisplayName(ChatColor.GREEN + "Normal");
        ArrayList<String> yes_lore = new ArrayList<>();
        yes_lore.add(ChatColor.AQUA + "Overworld");
        grass_meta.setLore(yes_lore);
        grass.setItemMeta(grass_meta);
        ItemStack nether = new ItemStack(Material.NETHERRACK, 1);
        ItemMeta nether_meta = nether.getItemMeta();
        nether_meta.setDisplayName(ChatColor.DARK_RED + "Nether");
        nether.setItemMeta(nether_meta);
        ArrayList<String> nether_lore = new ArrayList<>();
        nether_lore.add(ChatColor.AQUA + "Nether");
        ItemStack end = new ItemStack(Material.ENDER_EYE, 1);
        ItemMeta end_meta = end.getItemMeta();
        end_meta.setDisplayName(ChatColor.DARK_RED + "End");
        end.setItemMeta(end_meta);
        ArrayList<String> end_lore = new ArrayList<>();
        end_lore.add(ChatColor.AQUA + "End");


        inventory.setItem(3, grass);
        inventory.setItem(4, nether);
        inventory.setItem(5,end);

        setFillerGlass();

    }


}
