package tel.endho.rooms.menusystem.bmenu;

import org.bukkit.entity.Player;
import org.geysermc.cumulus.SimpleForm;
import org.geysermc.cumulus.response.FormResponse;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.geysermc.cumulus.util.FormBuilder;
import org.geysermc.cumulus.util.FormImage;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import java.sql.SQLException;

public class BRKMainMenu {
    public void makemenu(Player player){
        FloodgateApi flapi = FloodgateApi.getInstance();
        SimpleForm simpleForm= SimpleForm.builder()
                .title("Title")
                .content("Content")
                .button("Create Room")
                .button("Your Rooms")
                .button("View a specific players rooms.")
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
                    new BRKCreateRoomMenu().makemenu(player);
                }
                return;
            }
            if (response.getClickedButtonId()==1) {
                new BRKLoadRooms().makemenu(player);
                if(player.hasPermission("rooms.load")){

                }
                return;
            }
            if(response.getClickedButtonId()==2){
                try {
                    new BRKVisitRooms().makemenu(player);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        FloodgatePlayer flapiPlayer = flapi.getPlayer(player.getUniqueId());
        flapiPlayer.sendForm(simpleForm); // or #sendForm(formBuilder)

    }
}
