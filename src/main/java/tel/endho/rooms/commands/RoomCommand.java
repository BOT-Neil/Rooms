package tel.endho.rooms.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;
import tel.endho.rooms.RoomWorld;
import tel.endho.rooms.RoomWorlds;
import tel.endho.rooms.Rooms;
import tel.endho.rooms.menusystem.bmenu.BRKConfirmDeleteMenu;
import tel.endho.rooms.menusystem.bmenu.BRKMainMenu;
import tel.endho.rooms.menusystem.bmenu.BRKVisitTargetRooms;
import tel.endho.rooms.menusystem.menu.DeleteConfirmMenu;
import tel.endho.rooms.menusystem.menu.MainMenu;
import tel.endho.rooms.menusystem.menu.VisitTargetRoomsMenu;
import tel.endho.rooms.util.LocationSerializer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RoomCommand implements CommandExecutor, TabCompleter {
  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

    if (command.getName().equalsIgnoreCase("rooms")) { // checking if my command is the one i'm after

      List<String> autoCompletes = new ArrayList<>(); // create a new string list for tab completion
      autoCompletes.add("visit");
      // todo if isroomworld&&isOwner
      autoCompletes.add("trust");
      autoCompletes.add("add");

      if (args.length == 1) { // only interested in the first sub command, if you wanted to cover more deeper
                              // sub commands, you could have multiple if statements or a switch statement

        return autoCompletes; // then return the list

      }

    }

    return null; // this will return nothing if it wasn't the disguise command I have
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

    if (sender instanceof Player) {

      Player player = (Player) sender;
      if (Arrays.stream(args).findFirst().isPresent()) {
        Rooms.debug("argslength: " + args.length);
        switch (args[0]) {
          case "seticon" -> {
            if (args.length == 1) {
              if (RoomWorlds.getRoomWorldString(player.getLocation().getWorld().getName()) != null) {
                RoomWorld roomWorld = RoomWorlds
                    .getRoomWorldString(player.getLocation().getWorld().getName());
                if (roomWorld.isOwner(player)) {
                  roomWorld.setIconMaterial(player.getInventory().getItemInMainHand().getType());
                }
              }
            }
          }
          case "delete" -> {
            if (args.length == 1) {
              if (RoomWorlds.getRoomWorldString(player.getLocation().getWorld().getName()) != null) {
                RoomWorld roomWorld = RoomWorlds
                    .getRoomWorldString(player.getLocation().getWorld().getName());
                if (roomWorld.isOwner(player)) {
                  if (Rooms.isFloodgateLoaded() && FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) {
                    new BRKConfirmDeleteMenu().makemenu(player);
                  } else {
                    new DeleteConfirmMenu(Rooms.getPlayerMenuUtility(player)).open();
                    // new MainMenu(Rooms.getPlayerMenuUtility(p)).open();
                  }
                }
              }
            }
          }
          case "visit" -> {
            if (args.length == 3) {
              try {
                Rooms.mysql.loadOthersRoomWorlds(player, args[1], Integer.valueOf(args[2]));
              } catch (SQLException e) {
                e.printStackTrace();
              }
            } else if (args.length == 2) {
              try {
                if (RoomWorlds.getRoomWorldsPlayer(args[1]).isEmpty()) {
                  Rooms.mysql.loadOthersRoomWorlds(player, args[1], null);
                } else {
                  if (Rooms.isFloodgateLoaded() &&FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) {
                    new BRKVisitTargetRooms().makemenu(player, args[1]);
                  } else {
                    new VisitTargetRoomsMenu(Rooms.getPlayerMenuUtility(player), args[1]).open();
                    // new MainMenu(Rooms.getPlayerMenuUtility(p)).open();
                  }
                }

              } catch (SQLException e) {
                e.printStackTrace();
              }
            }
          }
          case "trust" -> {
            if (args.length == 2) {
              if (Bukkit.getPlayer(args[1]) != null) {
                Player target = Bukkit.getPlayer(args[1]);
                Rooms.debug("debug59");
                if (RoomWorlds.isRoomWorld(player.getLocation().getWorld().getName())) {
                  RoomWorld roomWorld = RoomWorlds
                      .getRoomWorldString(player.getLocation().getWorld().getName());
                  if (roomWorld.getOwnerUUID().equals(player.getUniqueId())) {
                    try {
                      roomWorld.getTrustedMembers().put(target.getUniqueId(), target.getName());
                      player.sendMessage("You trusted " + target.getName());
                    } catch (Exception exception) {
                      player.sendMessage("Unknown error");
                    }
                  }
                }
              } else {
                player.sendMessage("Player not found, or offline.");
              }

            }

          }
          case "clearmembers" -> {
            if (args.length == 2) {
              if (Bukkit.getPlayer(args[1]) != null) {
                // Player target = Bukkit.getPlayer(args[1]);
                if (RoomWorlds.isRoomWorld(player.getLocation().getWorld().getName())) {
                  RoomWorld roomWorld = RoomWorlds
                      .getRoomWorldString(player.getLocation().getWorld().getName());
                  if (roomWorld.getOwnerUUID().equals(player.getUniqueId())) {
                    try {
                      roomWorld.clearMembers(true);
                      player.sendMessage("You cleared the members");
                    } catch (Exception exception) {
                      player.sendMessage("Unknown error");
                    }
                  }
                }
              } else {
                player.sendMessage("Player not found, or offline.");
              }

            }
          }
          case "locationdebug" ->{
            player.sendMessage(LocationSerializer.getSerializedPresetLocation(player.getLocation()));
          }
        }
        // check houses for player if online or else load offlinehouseworld hashmap for
        // player
      } else {
        if (Rooms.isFloodgateLoaded()&&FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) {
          new BRKMainMenu().makemenu(player);
        } else {
          new MainMenu(Rooms.getPlayerMenuUtility(player)).open();
        }
      }

    }

    return true;
  }

}
