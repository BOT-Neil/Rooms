package tel.endho.rooms.menusystem;

import org.bukkit.entity.Player;

import java.util.UUID;

/*
Companion class to all menus. This is needed to pass information across the entire
 menu system no matter how many inventories are opened or closed.

 Each player has one of these objects, and only one.
 */

public class PlayerMenuUtility {

  private Player owner;
  // store the player that will be killed so we can access him in the next menu
  private Player playerToKill;
  private Player playerGetHouseList;
  private UUID worldclicked;

  public PlayerMenuUtility(Player p) {
    this.owner = p;
  }

  public Player getOwner() {
    return owner;
  }

  public Player getPlayerToKill() {
    return playerToKill;
  }

  public void setPlayerToKill(Player playerToKill) {
    this.playerToKill = playerToKill;
  }

  public Player getPlayerGetHouseList() {
    return playerGetHouseList;
  }

  public void setPlayerGetHouseList(Player player) {
    this.playerGetHouseList = player;
  }

  public UUID getWorldclicked() {
    return worldclicked;
  }

  public void setWorldclicked(UUID uuid) {
    this.worldclicked = uuid;
  }
}
