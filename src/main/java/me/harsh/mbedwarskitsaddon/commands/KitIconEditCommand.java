package me.harsh.mbedwarskitsaddon.commands;

import java.util.ArrayList;
import java.util.List;
import me.harsh.mbedwarskitsaddon.config.KitConfig;
import me.harsh.mbedwarskitsaddon.kits.Kit;
import me.harsh.mbedwarskitsaddon.kits.KitManager;
import me.harsh.mbedwarskitsaddon.utils.KitsUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KitIconEditCommand extends SubCommand {
  public KitIconEditCommand() {
    super("editicon");
  }

  @Override
  public void onCommand(Player player, String[] args) {
    if (!player.hasPermission(KitsUtil.KIT_ADMIN_PERM))
      return;

    // /kit editicon <id> [lore]
    if (args.length < 2) {
      KitsUtil.tell(player, KitConfig.getMessagesMap().get("Command_invalid"));
      return;
    }
    final String id = args[0];
    if (!KitManager.getInstance().getLoadedKits().containsKey(id)){
      KitsUtil.tell(player, KitConfig.getMessagesMap().get("Kit_does_not_exists"), new Kit(id, null, null, null, null));
      return;
    }
    final Kit kit = KitManager.getInstance().getLoadedKits().get(id);
    final ItemStack icon = kit.getIcon();
    final ItemMeta meta = icon.getItemMeta();
    final List<String> lore = new ArrayList<>();
    for (int i = 1; i < args.length; i++) {
      lore.add(KitsUtil.colorize(args[i]));
    }
    meta.setLore(lore);
    icon.setItemMeta(meta);
    KitsUtil.tell(player, KitConfig.getMessagesMap().get("Kit_icon_edited"), kit);
  }

}
