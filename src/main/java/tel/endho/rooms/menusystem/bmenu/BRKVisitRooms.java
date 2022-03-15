package tel.endho.rooms.menusystem.bmenu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.CustomForm;
import org.geysermc.cumulus.response.CustomFormResponse;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import tel.endho.rooms.RoomWorld;
import tel.endho.rooms.RoomWorlds;
import tel.endho.rooms.Rooms;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BRKVisitRooms {
    public void makemenu(Player player) throws SQLException {

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
                .title("View another players rooms.")
                .input("Visit", "Trashulius")
                .build();
        customForm.setResponseHandler(responseData -> {
            CustomFormResponse response = customForm.parseResponse(responseData);
            if (!response.isCorrect()) {
                // player closed the form or returned invalid info (see FormResponse)
                return;
            }

            // short version of getClickedButtonId == 0
            String target = response.getInput(0);
            System.out.println("target: " +target);
            if(target!=null&&Bukkit.getPlayer(target)==null){
                if(RoomWorlds.getRoomWorldsPlayer(target).isEmpty()){
                    try {
                        Rooms.mysql.loadOthersRoomWorlds(player,target,null);
                        return;
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }else{
                    try {
                        new BRKVisitTargetRooms().makemenu(player,target);
                        return;
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }else{
                try {
                    new BRKVisitTargetRooms().makemenu(player,target);
                    return;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            /*int dropclick0 = response.getDropdown(0);
            int dropclick1 = response.getDropdown(1);
            try {
                Rooms.housemanager.TpOrLoadHouseWorld(player,playerhouses.get(dropclick0).getWorldUUID());
            } catch (CorruptedWorldException | NewerFormatException | WorldInUseException | UnknownWorldException | IOException e) {
                e.printStackTrace();
            }
            System.out.println(response.getResponses().toString());*/
        });
        FloodgatePlayer flapiPlayer = flapi.getPlayer(player.getUniqueId());
        flapiPlayer.sendForm(customForm); // or #sendForm(formBuilder)

    }
}
