package me.harsh.mbedwarskitsaddon.commands;

import me.harsh.mbedwarskitsaddon.MBedwarsKitsPlugin;
import me.harsh.mbedwarskitsaddon.config.KitConfig;
import me.harsh.mbedwarskitsaddon.utils.KitsUtil;
import org.bukkit.entity.Player;

public class KitReloadCommand extends SubCommand {
  public KitReloadCommand() {
    super("reload");
  }

  @Override
  public void onCommand(Player player, String[] args) {
    if (!player.hasPermission(KitsUtil.KIT_ADMIN_PERM))
      return;
    // /kit reload
    if (args.length != 0) {
      KitsUtil.tell(player, KitConfig.getMessagesMap().get("Command_invalid"));
      return;
    }

    // Reload
    MBedwarsKitsPlugin.getInstance().getKitConfig().loadConfiguration();
    System.out.println("Reloaded");

  }

}
