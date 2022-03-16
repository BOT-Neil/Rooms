package tel.endho.rooms.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import tel.endho.rooms.Rooms;

import java.sql.SQLException;

public class JoinListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) throws SQLException {
        Rooms.mysql.loadRoomWorlds(event.getPlayer());
    }
}
