package tel.endho.rooms.menusystem.bmenu;

import com.grinderwolf.swm.api.exceptions.CorruptedWorldException;
import com.grinderwolf.swm.api.exceptions.NewerFormatException;
import com.grinderwolf.swm.api.exceptions.UnknownWorldException;
import com.grinderwolf.swm.api.exceptions.WorldInUseException;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.CustomForm;
import org.geysermc.cumulus.response.CustomFormResponse;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import tel.endho.rooms.RoomWorld;
import tel.endho.rooms.RoomWorlds;
import tel.endho.rooms.Rooms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BRKLoadRooms {
    public void makemenu(Player player){
        System.out.println("loadroomsmenu: ");
        FloodgateApi flapi = FloodgateApi.getInstance();
        ArrayList<RoomWorld>playerhouses= new ArrayList<>(RoomWorlds.getRoomWorldsPlayer(player).values());
        playerhouses.sort(Comparator.comparingInt(RoomWorld::getRowid));
        List<String> stockList = new ArrayList<String>();
        AtomicInteger index = new AtomicInteger();
        for(int i = 0;i < playerhouses.size();i++){
            stockList.add(i+". "+playerhouses.get(i).getEnviroment());
            index.set(i);
        }
        String[] stockArr = new String[playerhouses.size()];
        stockArr = stockList.toArray(stockArr);
        System.out.println(stockArr);
        CustomForm customForm= CustomForm.builder()
                .title("Your Rooms")
                .dropdown("List",stockArr)
                .build();
        customForm.setResponseHandler(responseData -> {
            CustomFormResponse response = customForm.parseResponse(responseData);
            if (!response.isCorrect()) {
                // player closed the form or returned invalid info (see FormResponse)
                return;
            }

            // short version of getClickedButtonId == 0
            int dropclick0 = response.getDropdown(0);
            try {
                Rooms.roomWorldManager.TpOrLoadHouseWorld(player,playerhouses.get(dropclick0).getWorldUUID());
            } catch (CorruptedWorldException | NewerFormatException | WorldInUseException | UnknownWorldException | IOException e) {
                e.printStackTrace();
            }
            System.out.println(response.getResponses().toString());
        });
        FloodgatePlayer flapiPlayer = flapi.getPlayer(player.getUniqueId());
        flapiPlayer.sendForm(customForm); // or #sendForm(formBuilder)

    }
}
