package me.harsh.mbedwarskitsaddon.menu;

import de.marcely.bedwars.api.cosmetics.CosmeticsAPI;
import de.marcely.bedwars.api.cosmetics.currency.Currency;
import de.marcely.bedwars.api.cosmetics.currency.CurrencyProvider;
import de.marcely.bedwars.api.message.Message;
import de.marcely.bedwars.tools.Helper;
import de.marcely.bedwars.tools.NMSHelper;
import de.marcely.bedwars.tools.gui.GUIItem;
import de.marcely.bedwars.tools.gui.type.ChestGUI;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import me.harsh.mbedwarskitsaddon.MBedwarsKitsPlugin;
import me.harsh.mbedwarskitsaddon.config.KitConfig;
import me.harsh.mbedwarskitsaddon.kits.Kit;
import me.harsh.mbedwarskitsaddon.kits.KitManager;
import me.harsh.mbedwarskitsaddon.lib.SkullCreator;
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
    super(6, KitsUtil.colorize(
        KitConfig.KIT_MENU_TITLE
            .replace("%page_total%", ""+KitsUtil.getTotalPageNo())
            .replace("%page_current%", ""+1)
    ));
  }

  // For every page
  // BLACK
  // KITS (4 Rows = 27kits + 1None)
  // BLACK (Info bar)
  public void drawPage(Player player, int pageNo) {
    clear();

    if (pageNo > KitsUtil.getTotalPageNo())
      return;

    final List<Integer> borders = Arrays.asList(0,1,2,3,4,5,6,7,8,9,17,18,26,27,35,36,44,46,47,48,50,51,52);
    for (int i = 0; i < 54; i++) {
      if (!borders.contains(i))
        continue;
      setItem(getBlackFiller(player), i);
    }

    // BACK
    createItem(player, "SIGN", "", () -> {
          if (pageNo == 1)
            return;
          drawPage(player, pageNo - 1);
          setTitle(KitsUtil.colorize(
              KitConfig.KIT_MENU_TITLE
                  .replace("%page_total%", ""+KitsUtil.getTotalPageNo())
                  .replace("%page_current%", ""+ (pageNo-1))
          ));
        },
        Message.build(KitsUtil.colorize(KitConfig.KIT_MENU_BACK_NAME)), Message.build(KitsUtil.colorizeList(KitConfig.KIT_MENU_BACK_DESCRIPTION)),
        guiItem -> {

          if (pageNo == 1) {
            setItem(getBlackFiller(player), 45);
            return;
          }
          setItem(guiItem, 45);
        });
    // Next
    createItem(player, "SIGN", "", () -> {
          if (pageNo == KitsUtil.getTotalPageNo())
            return;
          drawPage(player, pageNo + 1);
          setTitle(KitsUtil.colorize(
              KitConfig.KIT_MENU_TITLE
                  .replace("%page_total%", ""+KitsUtil.getTotalPageNo())
                  .replace("%page_current%", ""+ (pageNo+1))
          ));
        },
        Message.build(KitsUtil.colorize(KitConfig.KIT_MENU_NEXT_NAME)), Message.build(KitsUtil.colorizeList(KitConfig.KIT_MENU_NEXT_DESCRIPTION)),
        guiItem -> {

          if (pageNo == KitsUtil.getTotalPageNo()){
            setItem(getBlackFiller(player), 53);
            return;
          }
          setItem(guiItem, 53);

        });
    // HEAD
    setItem(getPlayerHead(player), 49);


    createItem(player, "WHITE_STAINED_GLASS_PANE", "", () -> {
      if (manager.getPlayerCurrentKits().containsKey(player.getUniqueId()))
        manager.getPlayerCurrentKits().replace(player.getUniqueId(), "None");
      else manager.getPlayerCurrentKits().put(player.getUniqueId(), "None");

      clear();
      drawPage(player, pageNo);
    }, Message.build("None"), Message.build(""), guiItem -> {
      setItem(guiItem, 10);
    });

    final Kit[] kits = KitManager.getInstance().getLoadedKits().values().toArray(new Kit[0]);

    if (kits.length == 0)
      return;
    for (int i = 27 * (pageNo - 1); i < (27 * pageNo); i++) {
      if (i >= kits.length)
        break;

      final Kit kit = kits[i];
      if (kit == null)
        continue;

      createItem(player, kit.getIcon().getType().name(), KitsUtil.getKitPerm(kit), () -> {
        if (!player.hasPermission(kit.getPermission())) {
          if (KitsUtil.KIT_COINS_HOOK){
            Currency currency = CosmeticsAPI.get().getCurrencyById(KitConfig.COINS_ID);
            if (currency == null && !(CosmeticsAPI.get().getCurrencies().isEmpty())){
              // If not empty it should find any.
              currency = CosmeticsAPI.get().getCurrencies().stream().findAny().get();
            }
            if (currency == null || currency.getProvider() == null)
              return;

            final CurrencyProvider provider = currency.getProvider();
            currency.getProvider().getAmount(player, bigDecimal -> {
              if (bigDecimal.compareTo(BigDecimal.valueOf(kit.getPrice())) >= 0){
                // Player has enough coins.
                provider.setAmount(player, bigDecimal.subtract(BigDecimal.valueOf(kit.getPrice())));
                player.addAttachment(MBedwarsKitsPlugin.getInstance()).setPermission(kit.getPermission(), true);
                KitsUtil.tell(player, KitConfig.getMessagesMap().get("Kit_purchased"), kit);
                clear();
                drawPage(player, pageNo);
                return;
              }
              KitsUtil.tell(player, KitConfig.getMessagesMap().get("Kit_coins_not_enough"), kit);
            });
            return;
          }
          KitsUtil.tell(player, KitConfig.getMessagesMap().get("Kit_No_permission"), kit);
          return;
        }
        selectKit(kit, player);
        clear();
        drawPage(player, pageNo);
      }, Message.build(kit.getName()), Message.build(
          kit.getIcon().getItemMeta().getLore() == null ? Collections.singletonList("") : kit.getIcon().getItemMeta().getLore()
      ), guiItem -> {
        if (!player.hasPermission(kit.getPermission()) && KitsUtil.KIT_COINS_HOOK) {
          final ItemMeta meta = guiItem.getItem().getItemMeta();
          final List<String> lore = meta.getLore();
          lore.add("");
          lore.add("&6Price: " + kit.getPrice());
          guiItem.getItem().setItemMeta(meta);
        }
        System.out.println("Adding GUi Item");
        addItem(guiItem);
      });

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
    if (kit == null) {
      if (item.getItem().getType() == Helper.get().getMaterialByName("WHITE_STAINED_GLASS_PANE")) {
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
      meta.setDisplayName(KitsUtil.colorize(kit.getName()));
      meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
      meta.setLore(customLore);
      item.getItem().setItemMeta(meta);
    }

    callback.accept(item);
  }

  private GUIItem createItemDev(Player player, String materialName, Runnable onUse, Message name, Message lore) {
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
      onUse.run();
    });
  }

  private GUIItem createSkull(Player player, ItemStack is, Runnable onUse, Message name, Message lore) {
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
      onUse.run();
    });
  }

  private GUIItem getBlackFiller(Player player) {
    return createItemDev(player, "BLACK_STAINED_GLASS_PANE", () -> {
        },
        Message.build(" "), Message.build(" "));
  }

  private GUIItem getPlayerHead(Player player) {
    final ItemStack skull = SkullCreator.itemFromUuid(player.getUniqueId());
    return createSkull(player, skull, () -> {
    }, Message.build(KitsUtil.colorize(KitConfig.KIT_MENU_PLAYER_NAME)), Message.build(KitsUtil.colorizeList(KitConfig.KIT_MENU_PLAYER_DESCRIPTION)));
  }


  private void selectKit(Kit kit, Player player) {
    final Map<UUID, String> playerKitMap = KitManager.getInstance().getPlayerCurrentKits();

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



//  !- OLD METHOD WITHOUT PAGES -!
//  public void draw(Player player) {
//    createItem(player, "WHITE_STAINED_GLASS_PANE", "", () -> {
//      if (manager.getPlayerCurrentKits().containsKey(player.getUniqueId()))
//        manager.getPlayerCurrentKits().replace(player.getUniqueId(), "None");
//      else manager.getPlayerCurrentKits().put(player.getUniqueId(), "None");
//
//      clear();
//      draw(player);
//    }, Message.build("None"), Message.build(""), guiItem -> {
//      setItem(guiItem, 0);
//    });
//
//    for (Kit kit : KitManager.getInstance().getLoadedKits().values()) {
//      createItem(player, kit.getIcon().getType().name(), KitsUtil.getKitPerm(kit), () -> {
//        if (!player.hasPermission(kit.getPermission())) {
//          KitsUtil.tell(player, KitConfig.getMessagesMap().get("Kit_No_permission"), kit);
//          return;
//        }
//        selectKit(kit, player);
//        clear();
//        draw(player);
//      }, Message.build(kit.getName()), Message.build(
//          kit.getIcon().getItemMeta().getLore() == null ? Collections.singletonList("") : kit.getIcon().getItemMeta().getLore()
//      ), this::addItem);
//    }
//  }

}
