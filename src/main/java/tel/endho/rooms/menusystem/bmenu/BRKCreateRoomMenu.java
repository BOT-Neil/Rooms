package tel.endho.rooms.menusystem.bmenu;

import java.util.Map;

import org.bukkit.entity.Player;
import org.geysermc.cumulus.SimpleForm;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import tel.endho.rooms.Rooms;
import tel.endho.rooms.util.Preset;

public class BRKCreateRoomMenu {
  public void makemenu(Player player) {
    Map<Integer, Preset> presetMap = Rooms.roomWorldManager.getPresetMap();
    FloodgateApi flapi = FloodgateApi.getInstance();
    SimpleForm simpleForm = SimpleForm.builder()
        .title("Create a Room")
        .content("")
        .build();
    for (int i = 0; i < presetMap.size(); i++) {
      simpleForm.getButtons().add(ButtonComponent.of(presetMap.get(i).getName()));
    }
    simpleForm.setResponseHandler(responseData -> {
      SimpleFormResponse response = simpleForm.parseResponse(responseData);
      if (!response.isCorrect()) {
        // player closed the form or returned invalid info (see FormResponse)
        return;
      }

      // short version of getClickedButtonId == 0
      if (!player.hasPermission("rooms.create")) {
        return;
      }

      int id = response.getClickedButtonId();
      Rooms.roomWorldManager.createWorld(presetMap.get(id), player);
    });
    FloodgatePlayer flapiPlayer = flapi.getPlayer(player.getUniqueId());
    flapiPlayer.sendForm(simpleForm); // or #sendForm(formBuilder)

  }
}
