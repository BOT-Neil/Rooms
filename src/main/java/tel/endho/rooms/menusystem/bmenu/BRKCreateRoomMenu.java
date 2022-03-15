package tel.endho.rooms.menusystem.bmenu;

import org.bukkit.entity.Player;
import org.geysermc.cumulus.SimpleForm;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import tel.endho.rooms.Rooms;

public class BRKCreateRoomMenu {
    public void makemenu(Player player){
        FloodgateApi flapi = FloodgateApi.getInstance();
        SimpleForm simpleForm= SimpleForm.builder()
                .title("Create a Room")
                .content("Content")
                .button("Create a normal world")
                .button("Create a world in the end")
                .build();
        simpleForm.setResponseHandler(responseData -> {
            SimpleFormResponse response = simpleForm.parseResponse(responseData);
            if (!response.isCorrect()) {
                // player closed the form or returned invalid info (see FormResponse)
                return;
            }

            // short version of getClickedButtonId == 0
            if (response.getClickedButtonId()==0) {
                if(player.hasPermission("rooms.create")){
                    Rooms.roomWorldManager.createWorld("normal",player);
                }
                return;
            }
            if (response.getClickedButtonId()==1) {
                if(player.hasPermission("rooms.create")){
                    Rooms.roomWorldManager.createWorld("the_end",player);
                }
                return;
            }
        });
        FloodgatePlayer flapiPlayer = flapi.getPlayer(player.getUniqueId());
        flapiPlayer.sendForm(simpleForm); // or #sendForm(formBuilder)

    }
}
