package me.harsh.mbedwarskitsaddon.commands;

import java.util.HashMap;
import java.util.Map;
import me.harsh.mbedwarskitsaddon.MBedwarsKitsPlugin;
import me.harsh.mbedwarskitsaddon.config.KitConfig;
import me.harsh.mbedwarskitsaddon.kits.Kit;
import me.harsh.mbedwarskitsaddon.kits.KitManager;
import me.harsh.mbedwarskitsaddon.menu.KitEditMenu;
import me.harsh.mbedwarskitsaddon.utils.KitsUtil;
import org.bukkit.entity.Player;

public class KitEditCommand extends SubCommand {
  public KitEditCommand() {
    super("edit");
  }

  @Override
  public void onCommand(Player player, String[] args) {
    // /kit edit <id>
    if (args.length != 1) {
      KitsUtil.tell(player, KitConfig.getMessagesMap().get("Command_invalid"));
      return;
    }
    final String id = args[0];
    if (!KitManager.getInstance().getLoadedKits().containsKey(id)){
      KitsUtil.tell(player, KitConfig.getMessagesMap().get("Kit_does_not_exists"), new Kit(id, null, null, null, null));
      return;
    }
    final KitEditMenu menu = new KitEditMenu(KitManager.getInstance().getLoadedKits().get(id));
    menu.draw(player);
    menu.open(player);
    MBedwarsKitsPlugin.getInstance().getServer().getPluginManager().registerEvents(menu, MBedwarsKitsPlugin.getInstance());
  }

  @Override
  public Map<Integer, String> getTab() {
    final Map<Integer,String> tab = new HashMap<>();
    tab.put(0, "<id>");
    return tab;
  }
}
