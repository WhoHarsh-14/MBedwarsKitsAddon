package me.harsh.mbedwarskitsaddon.commands;

import de.marcely.bedwars.tools.Helper;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import me.harsh.mbedwarskitsaddon.config.KitConfig;
import me.harsh.mbedwarskitsaddon.kits.Kit;
import me.harsh.mbedwarskitsaddon.kits.KitManager;
import me.harsh.mbedwarskitsaddon.utils.KitsUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KitIconCommand extends SubCommand {
  public KitIconCommand() {
    super("icon");
  }

  @Override
  public void onCommand(Player player, String[] args) {
    if (!player.hasPermission(KitsUtil.KIT_ADMIN_PERM))
      return;

    // /kit icon <id> description/item [lore1,lore2,lore3]/[Material]/hand
    if (args.length < 3) {
      KitsUtil.tell(player, KitConfig.getMessagesMap().get("Command_invalid"));
      return;
    }
    final String id = args[0];
    if (!KitManager.getInstance().getLoadedKits().containsKey(id)) {
      KitsUtil.tell(player, KitConfig.getMessagesMap().get("Kit_does_not_exists"), new Kit(id, null, null, null, null));
      return;
    }
    final Kit kit = KitManager.getInstance().getLoadedKits().get(id);

    // Description command
    if (args[1].equalsIgnoreCase("description")) {
      final StringBuilder builder = new StringBuilder();
      for (int i = 2; i < args.length; i++) {
        builder
            .append(KitsUtil.colorize(args[i]))
            .append(" ");
      }
      final String newString = builder.toString();
      final List<String> lore = Arrays.stream(newString
                  .split(","))
                  .collect(Collectors.toList());
      final ItemStack icon = kit.getIcon();
      final ItemMeta meta = icon.getItemMeta();
      for (int i = 1; i < args.length; i++) {
        lore.add(KitsUtil.colorize(args[i]));
      }
      meta.setLore(lore);
      icon.setItemMeta(meta);
    }

    if (args[1].equalsIgnoreCase("item")) {
      final String item = args[2];
      if (item == null)
        return;
      if (item.equalsIgnoreCase("hand")) {
        // Set it to the item from hand.
        final ItemStack handItem = player.getItemInHand();
        if (handItem == null || handItem.isSimilar(kit.getIcon()))
          return;
        // Copies the itemStack without any checks/safety.
        if (args[3].equalsIgnoreCase("-ignoreChecks")){
          kit.setIcon(handItem);
          KitsUtil.tell(player, KitConfig.getMessagesMap().get("Kit_icon_edited"), kit);
          return;
        }
        final ItemStack kitIcon = kit.getIcon();
        kitIcon.setType(handItem.getType());
        final ItemMeta meta = kitIcon.getItemMeta();
        meta.setDisplayName(
            handItem.getItemMeta().hasDisplayName() ? handItem.getItemMeta().getDisplayName() : kit.getName()
        );
        meta.setLore(
            handItem.getItemMeta().hasLore() ? handItem.getItemMeta().getLore() : kit.getIcon().getItemMeta().getLore()
        );
        kitIcon.setItemMeta(meta);
        KitsUtil.tell(player, KitConfig.getMessagesMap().get("Kit_icon_edited"), kit);
        return;
      }
      final Material mat = Helper.get().getMaterialByName(item);
      if (mat == null) {
        KitsUtil.tell(player, KitConfig.getMessagesMap().get("Command_invalid_material"), kit);
        return;
      }
      kit.getIcon().setType(mat);
    }

    KitsUtil.tell(player, KitConfig.getMessagesMap().get("Kit_icon_edited"), kit);
  }

}
