package tel.endho.rooms.menusystem.menu;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tel.endho.rooms.menusystem.Menu;
import tel.endho.rooms.menusystem.PlayerMenuUtility;

import java.util.ArrayList;

public class KillConfirmMenu extends Menu {

    public KillConfirmMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public String getMenuName() {
        return "Kill " + playerMenuUtility.getPlayerToKill().getDisplayName();
    }

    @Override
    public int getSlots() {
        return 9;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {

        switch (e.getCurrentItem().getType()){
            case EMERALD:
                //they pressed yes, kill player
                e.getWhoClicked().closeInventory();
                playerMenuUtility.getPlayerToKill().setHealth(0.0); //grab the data from the previous menu
                e.getWhoClicked().sendMessage(ChatColor.RED + "HE DEAD!");
                break;
            case BARRIER:

                //go back to the previous menu
                //new KillPlayerMenu(playerMenuUtility).open();

                break;
        }

    }

    @Override
    public void setMenuItems() {

        ItemStack yes = new ItemStack(Material.EMERALD, 1);
        ItemMeta yes_meta = yes.getItemMeta();
        yes_meta.setDisplayName(ChatColor.GREEN + "Yes");
        ArrayList<String> yes_lore = new ArrayList<>();
        yes_lore.add(ChatColor.AQUA + "Would you like to add ");
        yes_lore.add(ChatColor.AQUA + "this player to your lock?");
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
