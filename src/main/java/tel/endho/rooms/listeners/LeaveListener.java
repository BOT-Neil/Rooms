package tel.endho.rooms.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import tel.endho.rooms.RoomWorld;
import tel.endho.rooms.RoomWorlds;

import java.sql.SQLException;
import java.util.ArrayList;

public class LeaveListener implements Listener {
  @EventHandler
  public void onLeave(PlayerQuitEvent event) throws SQLException {
    // HouseMain.mysql.loadHouseWorlds(event.getPlayer());
    ArrayList<RoomWorld> playerhouses = new ArrayList<>(RoomWorlds.getRoomWorldsPlayer(event.getPlayer()).values());
    playerhouses.forEach(roomWorld -> {
      if (!roomWorld.isLoaded()) {
        RoomWorlds.getRoomWolrds().remove(roomWorld.getWorldUUID(), roomWorld);
      }
    });
  }
}
