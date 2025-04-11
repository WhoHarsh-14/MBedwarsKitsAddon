package me.harsh.mbedwarskitsaddon.commands;

import me.harsh.mbedwarskitsaddon.config.KitConfig;
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
    menu.drawPage(player, 1);
    menu.open(player);
  }

}
