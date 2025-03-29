package me.harsh.mbedwarskitsaddon.menu;

import de.marcely.bedwars.api.message.Message;
import de.marcely.bedwars.api.player.PlayerDataAPI;
import de.marcely.bedwars.tools.Helper;
import de.marcely.bedwars.tools.NMSHelper;
import de.marcely.bedwars.tools.gui.GUIItem;
import de.marcely.bedwars.tools.gui.type.ChestGUI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import me.clip.placeholderapi.PlaceholderAPI;
import me.harsh.mbedwarskitsaddon.config.KitConfig;
import me.harsh.mbedwarskitsaddon.kits.Kit;
import me.harsh.mbedwarskitsaddon.kits.KitManager;
import me.harsh.mbedwarskitsaddon.utils.KitsUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KitMenu extends ChestGUI {

  public KitMenu(){
    super(6, KitsUtil.colorize(KitConfig.KIT_MENU_TITLE));
  }

  @Override
  public void open(Player player) {
    final String path = "kits.selected";
    setItem(createItem(player, "WHITE_STAINED_GLASS_PANE", "", () -> {
      PlayerDataAPI.get().getProperties(player, playerProperties -> {

      });
    }, Message.build("None"), Message.build("%kit_current_bool%")), 0);
    for (Kit kit : KitManager.getInstance().getLoadedKits().values()) {
      addItem(createItem(player, kit.getIcon().getType().name(), KitsUtil.getKitPerm(kit), () -> {
        // Set the selected kit to the clicked one.
        selectKit(kit, player, path);
      }, Message.build(kit.getName()), Message.build("")));
    }
  }

  private GUIItem createItem(Player player, String materialName, String permission, Runnable onUse, Message name, Message lore) {
    ItemStack is = NMSHelper.get().hideAttributes(Helper.get().parseItemStack(materialName));

    final ItemMeta im = is.getItemMeta();
    final List<String> loreList = new ArrayList<>();

    loreList.add("");
    loreList.addAll(Arrays.stream(lore.done(player).split("\\\\n"))
        .map(l -> ChatColor.GRAY + l)
        .collect(Collectors.toList()));

    im.setDisplayName(KitsUtil.colorize(name.done(player, false)));
    im.setLore(loreList);
    is.setItemMeta(im);

    return new GUIItem(is, (g0, g1, g2) -> {
      if (KitConfig.PER_KIT_PERM && !player.hasPermission(permission)) {
        KitsUtil.tell(player, KitConfig.getMessagesMap().get("Kit_No_permission"));
        return;
      }
      onUse.run();
    });
  }

  private void selectKit(Kit kit, Player player, String path){
    PlayerDataAPI.get().getProperties(player, playerProperties -> {
      final Optional<String> currentKit = playerProperties.get(path);

      if (!currentKit.isPresent()){
        playerProperties.set(path, kit.getIcon());
        KitsUtil.tell(player, KitConfig.getMessagesMap().get("Kit_selected"), kit);
        return;
      }

      if (currentKit.get().equalsIgnoreCase(kit.getId())){
        KitsUtil.tell(player, KitConfig.getMessagesMap().get("Kit_already_selected"), kit);
        return;
      }
      playerProperties.replace(path, kit.getId());
      KitsUtil.tell(player, KitConfig.getMessagesMap().get("Kit_selected"), kit);
    });
  }

}
