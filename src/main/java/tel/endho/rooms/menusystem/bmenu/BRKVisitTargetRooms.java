package tel.endho.rooms.menusystem.bmenu;

import com.grinderwolf.swm.api.exceptions.CorruptedWorldException;
import com.grinderwolf.swm.api.exceptions.NewerFormatException;
import com.grinderwolf.swm.api.exceptions.UnknownWorldException;
import com.grinderwolf.swm.api.exceptions.WorldInUseException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.CustomForm;
import org.geysermc.cumulus.response.CustomFormResponse;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import tel.endho.rooms.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BRKVisitTargetRooms {
  public void makemenu(Player player, String target) throws SQLException {

    FloodgateApi flapi = FloodgateApi.getInstance();
    ArrayList<RoomWorld> playerhouses = new ArrayList<>();
    if (Bukkit.getPlayer(target) == null) {
      playerhouses.addAll(RoomWorlds.getRoomWorldsPlayer(target).values());
    } else {
      playerhouses.addAll(RoomWorlds.getRoomWorldsPlayer(Bukkit.getPlayer(target)).values());
    }
    playerhouses.sort(Comparator.comparingInt(RoomWorld::getRowid));
    List<String> stockList = new ArrayList<String>();
    AtomicInteger index = new AtomicInteger();
    for (int i = 0; i < playerhouses.size(); i++) {
      int n = i;
      n++;
      stockList.add(n + ". " + playerhouses.get(i).getPreset());
      index.set(i);

    }
    String[] stockArr = new String[playerhouses.size()];
    stockArr = stockList.toArray(stockArr);
    Rooms.debug(stockArr.toString());
    CustomForm customForm = CustomForm.builder()
        .title("View another players rooms.")
        .dropdown("List", stockArr)
        .build();
    customForm.setResponseHandler(responseData -> {
      CustomFormResponse response = customForm.parseResponse(responseData);
      if (!response.isCorrect()) {
        // player closed the form or returned invalid info (see FormResponse)
        return;
      }

      int dropclick0 = response.getDropdown(0);
      try {
        Rooms.roomWorldManager.TpOrLoadHouseWorld(player, playerhouses.get(dropclick0).getWorldUUID());
        return;
      } catch (CorruptedWorldException | NewerFormatException | WorldInUseException | UnknownWorldException
          | IOException e) {
        e.printStackTrace();
      }
      Rooms.debug(response.getResponses().toString());
    });
    FloodgatePlayer flapiPlayer = flapi.getPlayer(player.getUniqueId());
    flapiPlayer.sendForm(customForm); // or #sendForm(formBuilder)

  }
}
