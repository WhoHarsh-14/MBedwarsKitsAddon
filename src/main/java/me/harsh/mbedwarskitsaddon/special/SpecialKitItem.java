package me.harsh.mbedwarskitsaddon.special;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.game.lobby.LobbyItem;
import de.marcely.bedwars.api.game.lobby.LobbyItemHandler;

import me.harsh.mbedwarskitsaddon.MBedwarsKitsPlugin;
import me.harsh.mbedwarskitsaddon.config.KitConfig;
import me.harsh.mbedwarskitsaddon.menu.KitMenu;
import org.bukkit.entity.Player;


public class SpecialKitItem extends LobbyItemHandler {
  public SpecialKitItem() {
    super("kit:menu", MBedwarsKitsPlugin.getInstance());
  }

  @Override
  public void handleUse(Player player, Arena arena, LobbyItem lobbyItem) {
    new KitMenu().open(player);
  }

  @Override
  public boolean isVisible(Player player, Arena arena, LobbyItem lobbyItem) {
    return !KitConfig.BLOCKED_ARENAS.contains(arena.getName());
  }
}
