package me.harsh.mbedwarskitsaddon.menu;

import de.marcely.bedwars.api.message.Message;
import de.marcely.bedwars.api.player.PlayerProperties;
import de.marcely.bedwars.tools.Helper;
import de.marcely.bedwars.tools.NMSHelper;
import de.marcely.bedwars.tools.gui.GUIItem;
import de.marcely.bedwars.tools.gui.type.ChestGUI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import me.harsh.mbedwarskitsaddon.config.KitConfig;
import me.harsh.mbedwarskitsaddon.kits.Kit;
import me.harsh.mbedwarskitsaddon.kits.KitManager;
import me.harsh.mbedwarskitsaddon.utils.KitsUtil;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KitMenu extends ChestGUI {

  private final PlayerProperties playerProperties;

  public KitMenu(PlayerProperties playerProperties) {
    super(6, KitsUtil.colorize(KitConfig.KIT_MENU_TITLE));
    this.playerProperties = playerProperties;
  }


  public void draw(Player player){
    // Get the player props

    createItem(player, "WHITE_STAINED_GLASS_PANE", "", () -> {
      playerProperties.set(KitsUtil.KIT_CURRENT_PATH, "None");
    }, Message.build("None"), Message.build(""), guiItem -> {
      setItem(guiItem, 0);
    });

    for (Kit kit : KitManager.getInstance().getLoadedKits().values()) {
      createItem(player, kit.getIcon().getType().name(), KitsUtil.getKitPerm(kit), () -> {
        if (!player.hasPermission(kit.getPermission())){
          KitsUtil.tell(player, KitConfig.getMessagesMap().get("Kit_No_permission"), kit);
          return;
        }
        selectKit(kit, player, KitsUtil.KIT_CURRENT_PATH);
      }, Message.build(kit.getName()), Message.build(""), this::addItem);
    }
  }



  private void createItem(Player player, String materialName, String permission, Runnable onUse, Message name, Message lore, Consumer<GUIItem> callback) {
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
    final GUIItem item = new GUIItem(is, (g0, g1, g2) -> {
      if (KitConfig.PER_KIT_PERM && !player.hasPermission(permission)) {
        KitsUtil.tell(player, KitConfig.getMessagesMap().get("Kit_No_permission"));
        return;
      }
      onUse.run();
    });
    final String currentId = playerProperties.get(KitsUtil.KIT_CURRENT_PATH).orElse("None");
    final Kit kit = KitManager.getInstance().getLoadedKits().get(currentId);
    if (kit == null)
      return;
    if (kit.getIcon().equals(item.getItem())) {
      item.getItem().addEnchantment(Enchantment.ARROW_INFINITE, 1);
      final ItemMeta meta = item.getItem().getItemMeta();
      meta.setDisplayName(kit.getName());
      meta.setLore(Collections.singletonList(KitsUtil.colorize("&aSelected")));
      meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
      item.getItem().setItemMeta(meta);
    }

    callback.accept(item);
  }

  private void selectKit(Kit kit, Player player, String path) {
    final Optional<String> currentKit = playerProperties.get(path);

    if (!currentKit.isPresent()) {
      playerProperties.set(path, kit.getIcon());
      KitsUtil.tell(player, KitConfig.getMessagesMap().get("Kit_selected"), kit);
      return;
    }

    if (currentKit.get().equalsIgnoreCase(kit.getId())) {
      KitsUtil.tell(player, KitConfig.getMessagesMap().get("Kit_already_selected"), kit);
      return;
    }
    playerProperties.replace(path, kit.getId());
    KitsUtil.tell(player, KitConfig.getMessagesMap().get("Kit_selected"), kit);
  }


//  private void updateGUIItem(String currentId, ItemStack original) {
//    final Kit kit = KitManager.getInstance().getLoadedKits().get(currentId);
//    if (kit == null)
//      return;
//    if (kit.getIcon().equals(original)) {
//      original.addEnchantment(Enchantment.ARROW_INFINITE, 1);
//      final ItemMeta meta = original.getItemMeta();
//      meta.setDisplayName(kit.getName());
//      meta.setLore(Collections.singletonList(KitsUtil.colorize("&aSelected")));
//      meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
//      original.setItemMeta(meta);
//    }
//  }

}
