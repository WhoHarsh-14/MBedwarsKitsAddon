package me.harsh.mbedwarskitsaddon.commands;

import me.harsh.mbedwarskitsaddon.config.KitConfig;
import me.harsh.mbedwarskitsaddon.kits.KitManager;
import me.harsh.mbedwarskitsaddon.utils.KitsUtil;
import org.bukkit.entity.Player;

public class KitUnLoadCommand extends SubCommand {
  public KitUnLoadCommand() {
    super("save");
  }

  @Override
  public void onCommand(Player player, String[] args) {
    if (!player.hasPermission(KitsUtil.KIT_ADMIN_PERM))
      return;
    // /kit save
    if (args.length != 0) {
      KitsUtil.tell(player, KitConfig.getMessagesMap().get("Command_invalid"));
      return;
    }
    // Actually save it.
    KitManager.getInstance().loadKitsIntoConfig();
    KitsUtil.tell(player, KitConfig.getMessagesMap().get("Kit_saved"));
  }

}
