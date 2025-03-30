package me.harsh.mbedwarskitsaddon.commands;

import de.marcely.bedwars.tools.Helper;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import me.harsh.mbedwarskitsaddon.MBedwarsKitsPlugin;
import me.harsh.mbedwarskitsaddon.config.KitConfig;
import me.harsh.mbedwarskitsaddon.kits.Kit;
import me.harsh.mbedwarskitsaddon.menu.KitCreateMenu;
import me.harsh.mbedwarskitsaddon.utils.KitsUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KitCreateCommand extends SubCommand {
  public KitCreateCommand() {
    super("create");
  }

  @Override
  public void onCommand(Player player, String[] args) {
    // /kit create <id> <name> <icon>
    if (args.length != 3) {
      KitsUtil.tell(player, KitConfig.getMessagesMap().get("Command_invalid"));
      return;
    }
    final String id = args[0];
    final String name = args[1];
    final Material mat = Helper.get().getMaterialByName(args[2]);
    if (mat == null){
      KitsUtil.tell(player, KitConfig.getMessagesMap().get("Command_invalid_material"));
      return;
    }
    final ItemStack icon = new ItemStack(mat);
    final ItemMeta iconMeta = icon.getItemMeta();
    iconMeta.setDisplayName(KitsUtil.colorize(name));
    icon.setItemMeta(iconMeta);
    final Kit dummyKit = new Kit(id, name, icon, new HashMap<>(), new HashSet<>());
    // Open the kit creation menu to parse the items, armour
    new KitCreateMenu(dummyKit).open(player);
    MBedwarsKitsPlugin.getInstance().getServer().getPluginManager().registerEvents(new KitCreateMenu(dummyKit), MBedwarsKitsPlugin.getInstance());
  }

  @Override
  public Map<Integer, String> getTab() {
    final Map<Integer,String> tab = new HashMap<>();
    tab.put(0, "<id>");
    tab.put(1, "<name>");
    tab.put(2, "<material>");
    return tab;
  }
}
