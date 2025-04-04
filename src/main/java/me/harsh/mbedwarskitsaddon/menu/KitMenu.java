package me.harsh.mbedwarskitsaddon.menu;

import de.marcely.bedwars.api.message.Message;
import de.marcely.bedwars.tools.Helper;
import de.marcely.bedwars.tools.NMSHelper;
import de.marcely.bedwars.tools.gui.GUIItem;
import de.marcely.bedwars.tools.gui.type.ChestGUI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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

  private final KitManager manager = KitManager.getInstance();

  public KitMenu() {
    super(6, KitsUtil.colorize(KitConfig.KIT_MENU_TITLE));
  }


  public void draw(Player player){
    createItem(player, "WHITE_STAINED_GLASS_PANE", "", () -> {
      if (manager.getPlayerCurrentKits().containsKey(player.getUniqueId()))
        manager.getPlayerCurrentKits().replace(player.getUniqueId(), "None");
      else manager.getPlayerCurrentKits().put(player.getUniqueId(), "None");

      clear();
      draw(player);
    }, Message.build("None"), Message.build(""), guiItem -> {
      setItem(guiItem, 0);
    });

    for (Kit kit : KitManager.getInstance().getLoadedKits().values()) {
      createItem(player, kit.getIcon().getType().name(), KitsUtil.getKitPerm(kit), () -> {
        if (!player.hasPermission(kit.getPermission())){
          KitsUtil.tell(player, KitConfig.getMessagesMap().get("Kit_No_permission"), kit);
          return;
        }
        selectKit(kit, player);
        clear();
        draw(player);
      }, Message.build(kit.getName()), Message.build(
          kit.getIcon().getItemMeta().getLore() == null ? Collections.singletonList("") : kit.getIcon().getItemMeta().getLore()
      ), this::addItem);
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
    final String currentId = KitManager.getInstance().getPlayerCurrentKits().get(player.getUniqueId());
    final Kit kit = KitManager.getInstance().getLoadedKits().get(currentId);
    if (kit == null){
      if (item.getItem().getType() == Helper.get().getMaterialByName("WHITE_STAINED_GLASS_PANE")){
        item.getItem().addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
        final ItemMeta meta = item.getItem().getItemMeta();
        meta.setLore(Collections.singletonList(KitsUtil.colorize("&aSelected")));
        item.getItem().setItemMeta(meta);
      }
      callback.accept(item);
      return;
    }

    if (kit.getIcon().getItemMeta().getDisplayName().equalsIgnoreCase(item.getItem().getItemMeta().getDisplayName())) {
      if (item.getItem().getType() == Helper.get().getMaterialByName("BOW"))
        item.getItem().addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
      else item.getItem().addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
      final ItemMeta meta = item.getItem().getItemMeta();
      List<String> customLore = kit.getIcon().getItemMeta().getLore();
      if (customLore == null)
        customLore = new ArrayList<>();
      customLore.add("");
      customLore.add(KitsUtil.colorize("&aSelected"));
      meta.setDisplayName(kit.getName());
      meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
      meta.setLore(customLore);
      item.getItem().setItemMeta(meta);
    }

    callback.accept(item);
  }

  private void selectKit(Kit kit, Player player) {
    final Map<UUID,String> playerKitMap = KitManager.getInstance().getPlayerCurrentKits();

    if (!playerKitMap.containsKey(player.getUniqueId())) {
      playerKitMap.put(player.getUniqueId(), kit.getId());
      KitsUtil.tell(player, KitConfig.getMessagesMap().get("Kit_selected"), kit);
      return;
    }

    if (playerKitMap.get(player.getUniqueId()).equalsIgnoreCase(kit.getId())) {
      KitsUtil.tell(player, KitConfig.getMessagesMap().get("Kit_already_selected"), kit);
      return;
    }
    playerKitMap.replace(player.getUniqueId(), kit.getId());
    KitsUtil.tell(player, KitConfig.getMessagesMap().get("Kit_selected"), kit);
  }


}
