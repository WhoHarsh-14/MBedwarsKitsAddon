package me.harsh.mbedwarskitsaddon.commands;

import de.marcely.bedwars.api.player.PlayerDataAPI;
import me.harsh.mbedwarskitsaddon.config.KitConfig;
import me.harsh.mbedwarskitsaddon.kits.KitManager;
import me.harsh.mbedwarskitsaddon.menu.KitMenu;
import me.harsh.mbedwarskitsaddon.utils.KitsUtil;
import org.bukkit.entity.Player;

public class KitMenuCommand extends SubCommand {
  public KitMenuCommand() {
    super("menu");
  }

  @Override
  public void onCommand(Player player, String[] args) {
//    if (!player.hasPermission(KitsUtil.KIT_ADMIN_PERM))
//      return;

    // /kit menu
    if (args.length != 0) {
      KitsUtil.tell(player, KitConfig.getMessagesMap().get("Command_invalid"));
      return;
    }

    // Open the menu
    final KitMenu menu = new KitMenu();
    menu.draw(player);
    menu.open(player);
  }

}
