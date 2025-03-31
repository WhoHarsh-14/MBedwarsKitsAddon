package me.harsh.mbedwarskitsaddon.commands;

import me.harsh.mbedwarskitsaddon.config.KitConfig;
import me.harsh.mbedwarskitsaddon.kits.KitManager;
import me.harsh.mbedwarskitsaddon.utils.KitsUtil;
import org.bukkit.entity.Player;

public class KitLoadCommand extends SubCommand {
  public KitLoadCommand() {
    super("load");
  }

  @Override
  public void onCommand(Player player, String[] args) {
    if (!player.hasPermission(KitsUtil.KIT_ADMIN_PERM))
      return;
    // /kit load
    if (args.length != 0) {
      KitsUtil.tell(player, KitConfig.getMessagesMap().get("Command_invalid"));
      return;
    }
    // Load it
    KitManager.getInstance().loadKitsFromConfig();
    KitsUtil.tell(player, KitConfig.getMessagesMap().get("Kit_loaded"));
  }

}
