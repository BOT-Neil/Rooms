package tel.endho.rooms.menusystem.menu;


import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import tel.endho.rooms.*;
import tel.endho.rooms.menusystem.Menu;
import tel.endho.rooms.menusystem.PlayerMenuUtility;

import java.util.ArrayList;
import java.util.UUID;

import static org.bukkit.Material.NETHER_STAR;

public class SettingsMenu extends Menu {

    public SettingsMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public String getMenuName() {
        return "Settings";
        //return "Kill " + playerMenuUtility.getPlayerToKill().getDisplayName();
    }

    @Override
    public int getSlots() {
        return 9;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        if(e.getWhoClicked() instanceof Player){
            Player player = (Player)e.getWhoClicked();
            if(RoomWorlds.isRoomWorld(UUID.fromString(player.getLocation().getWorld().getName()))){
                RoomWorld roomWorld = RoomWorlds.getRoomWorldUUID(UUID.fromString(player.getLocation().getWorld().getName()));
                if(roomWorld.isOwner(player)){
                    switch (e.getCurrentItem().getType()){
                        case LIGHT_BLUE_CONCRETE:
                            if(player.hasPermission("rooms.bordercolor.blue")){
                                roomWorld.setBorderColor("blue");
                                RoomWorldManager.updateBorder(roomWorld);
                                player.sendMessage(ChatColor.AQUA+"Your border colour is now blue.");
                            }else {player.sendMessage(
                                    ChatColor.AQUA+"Purchase a rank at endho.tel");}
                            return;
                        case LIME_CONCRETE:
                            if(player.hasPermission("rooms.bordercolor.green")){
                                roomWorld.setBorderColor("green");
                                RoomWorldManager.updateBorder(roomWorld);
                                player.sendMessage(ChatColor.AQUA+"Your border colour is now green.");
                            }else {player.sendMessage(
                                    ChatColor.AQUA+"Purchase a rank at endho.tel");}
                            return;
                        case RED_CONCRETE:
                            if(player.hasPermission("rooms.bordercolor.red")){
                                roomWorld.setBorderColor("red");
                                RoomWorldManager.updateBorder(roomWorld);
                                player.sendMessage(ChatColor.AQUA+"Your border colour is now red.");
                            }else {player.sendMessage(
                                    ChatColor.AQUA+"Purchase a rank at endho.tel");}
                            return;
                    }
                }
            }

        }


    }

    @Override
    public void setMenuItems() {
        if(RoomWorlds.isRoomWorld(UUID.fromString(playerMenuUtility.getOwner().getLocation().getWorld().getName()))){
            RoomWorld roomWorld = RoomWorlds.getRoomWorldUUID(UUID.fromString(playerMenuUtility.getOwner().getLocation().getWorld().getName()));
            ItemStack greenBorder = new ItemStack(Material.LIME_CONCRETE,1);
            ItemMeta grass_meta = greenBorder.getItemMeta();
            grass_meta.setDisplayName(ChatColor.GREEN + "Green Border");
            ArrayList<String> green_lore = new ArrayList<>();
            green_lore.add(ChatColor.AQUA + "Starts at "+Rooms.configs.getGeneralConfig().getInt("worldborder")+" blocks wide");
            green_lore.add(ChatColor.AQUA + "Purchase more colors and");
            green_lore.add(ChatColor.AQUA + "more space at endho.tel");
            grass_meta.setLore(green_lore);
            if(roomWorld.getBorderColor().equals("green")||(roomWorld.getBorderColor().isEmpty()&&Rooms.configs.getGeneralConfig().getString("bordercolour").equals("green"))){
                grass_meta.addEnchant(Enchantment.ARROW_FIRE,1,true);
                grass_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                grass_meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            }
            greenBorder.setItemMeta(grass_meta);

            ItemStack blueBorder = new ItemStack(Material.LIGHT_BLUE_CONCRETE, 1);
            ItemMeta blueBorderItemMeta = blueBorder.getItemMeta();
            blueBorderItemMeta.setDisplayName(ChatColor.DARK_RED + "Blue Border");
            blueBorderItemMeta.getPersistentDataContainer().set(new NamespacedKey(Rooms.getPlugin(),"menu"), PersistentDataType.STRING,"personal");
            ArrayList<String> blue_lore = new ArrayList<>();
            blue_lore.add(ChatColor.AQUA + "Starts at "+Rooms.configs.getGeneralConfig().getInt("worldborder")+" blocks wide");
            blue_lore.add(ChatColor.AQUA + "Purchase more colors and");
            blue_lore.add(ChatColor.AQUA + "more space at endho.tel");
            blueBorderItemMeta.setLore(blue_lore);
            if(roomWorld.getBorderColor().equals("blue")){
                blueBorderItemMeta.addEnchant(Enchantment.ARROW_FIRE,1,true);
                blueBorderItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                blueBorderItemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            }
            blueBorder.setItemMeta(blueBorderItemMeta);

            ItemStack redBorder = new ItemStack(Material.RED_CONCRETE, 1);
            ItemMeta redBorderItemMeta = redBorder.getItemMeta();
            redBorderItemMeta.setDisplayName(ChatColor.DARK_RED + "Red Border");
            redBorderItemMeta.getPersistentDataContainer().set(new NamespacedKey(Rooms.getPlugin(),"menu"), PersistentDataType.STRING,"local");
            ArrayList<String> red_lore = new ArrayList<>();
            red_lore.add(ChatColor.AQUA + "Starts at "+Rooms.configs.getGeneralConfig().getInt("worldborder")+" blocks wide");
            red_lore.add(ChatColor.AQUA + "Purchase more colors and");
            red_lore.add(ChatColor.AQUA + "more space at endho.tel");
            redBorderItemMeta.setLore(red_lore);
            if(roomWorld.getBorderColor().equals("red")){
                redBorderItemMeta.addEnchant(Enchantment.ARROW_FIRE,1,true);
                redBorderItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                redBorderItemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            }
            redBorder.setItemMeta(redBorderItemMeta);

            int membersint = roomWorld.getMembers().size();
            ItemStack membersItem = new ItemStack(Material.BOOK, 1);
            ItemMeta membersItemMeta = membersItem.getItemMeta();
            membersItemMeta.setDisplayName(ChatColor.DARK_RED + "Members");
            membersItemMeta.getPersistentDataContainer().set(new NamespacedKey(Rooms.getPlugin(),"menu"), PersistentDataType.STRING,"members");
            ArrayList<String> membersitem_lore = new ArrayList<>();
            membersitem_lore.add(ChatColor.AQUA + "Players with basic access");
            membersitem_lore.add(ChatColor.AQUA + "/room addmember playername");
            membersitem_lore.add(ChatColor.AQUA + "/room delmember playername");
            membersitem_lore.add(ChatColor.AQUA + "Members: "+membersint);
            membersItemMeta.setLore(membersitem_lore);
            membersItem.setItemMeta(membersItemMeta);

            int trustedint = roomWorld.getTrustedMembers().size();
            ItemStack trustedItem = new ItemStack(Material.BOOK, 1);
            ItemMeta trustedItemMeta = trustedItem.getItemMeta();
            trustedItemMeta.setDisplayName(ChatColor.DARK_RED + "Trusted");
            trustedItemMeta.getPersistentDataContainer().set(new NamespacedKey(Rooms.getPlugin(),"menu"), PersistentDataType.STRING,"trusted");
            ArrayList<String> trusteditem_lore = new ArrayList<>();
            trusteditem_lore.add(ChatColor.AQUA + "Players with extended access");
            trusteditem_lore.add(ChatColor.AQUA + "/room trust playername");
            trusteditem_lore.add(ChatColor.AQUA + "/room untrust playername");
            trusteditem_lore.add(ChatColor.AQUA + "Trusted: "+trustedint);
            trustedItemMeta.setLore(trusteditem_lore);
            trustedItem.setItemMeta(trustedItemMeta);

            //todo blocked

            ItemStack adminmenu = new ItemStack(Material.COMPARATOR);
            ItemMeta adminmenuMeta = adminmenu.getItemMeta();
            adminmenuMeta.setDisplayName(ChatColor.DARK_RED + "Admin");
            inventory.setItem(1, greenBorder);
            inventory.setItem(2, blueBorder);
            inventory.setItem(3, redBorder);
            inventory.setItem(4, membersItem);
            inventory.setItem(5, trustedItem);
            if(playerMenuUtility.getOwner().hasPermission("rooms.admin")){
                inventory.setItem(6,adminmenu);
            }


            setFillerGlass();
        }


    }


}
