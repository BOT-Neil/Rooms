package tel.endho.rooms.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import tel.endho.rooms.Rooms;

import java.sql.SQLException;

public class JoinListener implements Listener {
  @EventHandler
  public void onJoin(PlayerJoinEvent event) throws SQLException {
    Rooms.mysql.loadRoomWorlds(event.getPlayer());
  }
}
