package me.harsh.mbedwarskitsaddon.commands;

import java.util.HashMap;
import java.util.Map;
import me.harsh.mbedwarskitsaddon.config.KitConfig;
import me.harsh.mbedwarskitsaddon.kits.Kit;
import me.harsh.mbedwarskitsaddon.kits.KitManager;
import me.harsh.mbedwarskitsaddon.utils.KitsUtil;
import org.bukkit.entity.Player;

public class KitRemoveCommand extends SubCommand {
  public KitRemoveCommand() {
    super("remove");
  }

  @Override
  public void onCommand(Player player, String[] args) {
    if (!player.hasPermission(KitsUtil.KIT_ADMIN_PERM))
      return;
    // /kit remove <id>
    if (args.length != 1) {
      KitsUtil.tell(player, KitConfig.getMessagesMap().get("Command_invalid"));
      return;
    }
    final String id = args[0];
    if (!KitManager.getInstance().getLoadedKits().containsKey(id)) {
      KitsUtil.tell(player, KitConfig.getMessagesMap().get("Kit_does_not_exists"));
      return;
    }
    // Manages our remove.
    KitManager.getInstance().removeKit(id);
    KitsUtil.tell(player, KitConfig.getMessagesMap().get("Kit_removed"), new Kit(id, null, null,null, null));
  }

  @Override
  public Map<Integer, String> getTab() {
    final Map<Integer,String> tab = new HashMap<>();
    tab.put(0, "<id>");
    return tab;
  }
}
