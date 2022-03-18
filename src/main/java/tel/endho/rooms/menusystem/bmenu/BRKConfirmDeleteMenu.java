package tel.endho.rooms.menusystem.bmenu;

import com.grinderwolf.swm.api.exceptions.UnknownWorldException;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.SimpleForm;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import tel.endho.rooms.RoomWorld;
import tel.endho.rooms.RoomWorlds;
import tel.endho.rooms.Rooms;

import java.io.IOException;
import java.util.UUID;

public class BRKConfirmDeleteMenu {
  public void makemenu(Player player) {
    FloodgateApi flapi = FloodgateApi.getInstance();
    SimpleForm simpleForm = SimpleForm.builder()
        .title("Delete this room?")
        .content("Content")
        .button("Yes - Delete it")
        .button("No - Stop!")
        .build();
    simpleForm.setResponseHandler(responseData -> {
      SimpleFormResponse response = simpleForm.parseResponse(responseData);
      if (!response.isCorrect()) {
        // player closed the form or returned invalid info (see FormResponse)
        return;
      }

      // short version of getClickedButtonId == 0
      if (RoomWorlds.isRoomWorld(UUID.fromString(player.getLocation().getWorld().getName()))
          || player.hasPermission("rooms.admin")) {
        RoomWorld roomWorld = RoomWorlds.getRoomWorldUUID(UUID.fromString(player.getLocation().getWorld().getName()));
        if (response.getClickedButtonId() == 0) {
          try {
            Rooms.roomWorldManager.deleteRoomWorld(roomWorld);
          } catch (UnknownWorldException | IOException e) {
            e.printStackTrace();
          }
          return;
        }
        if (response.getClickedButtonId() == 1) {
          return;
        }
      }

    });
    FloodgatePlayer flapiPlayer = flapi.getPlayer(player.getUniqueId());
    flapiPlayer.sendForm(simpleForm); // or #sendForm(formBuilder)

  }
}
