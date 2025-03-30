package me.harsh.mbedwarskitsaddon.menu;

import de.marcely.bedwars.api.message.Message;
import de.marcely.bedwars.tools.Helper;
import de.marcely.bedwars.tools.NMSHelper;
import de.marcely.bedwars.tools.gui.GUIItem;
import de.marcely.bedwars.tools.gui.type.ChestGUI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import me.harsh.mbedwarskitsaddon.kits.Kit;
import me.harsh.mbedwarskitsaddon.kits.KitManager;
import me.harsh.mbedwarskitsaddon.utils.ArmourType;
import me.harsh.mbedwarskitsaddon.utils.KitsUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KitCreateMenu extends ChestGUI implements Listener {

  private final Kit kit;

  public KitCreateMenu(Kit kit) {
    super(6, KitsUtil.colorize(kit.getName()));
    this.kit = kit;

  }


  @Override
  public void open(Player player) {
    // ABOVE BLACK -> Hotbar
    // SIDE FROM RED -> Armour
    for (int i = 9; i <= 17; i++) {
      setItem(getFillerGlass(), i);
    }
    setItem(getRedGlass(), 19);
    setItem(getRedGlass(), 28);
    setItem(getRedGlass(), 37);
    setItem(getRedGlass(), 46);
    setItem(getArmourHelmet(player), 18);
    setItem(getArmourChestplate(player), 27);
    setItem(getArmourLeggings(player), 36);
    setItem(getArmourBoots(player), 45);

    setItem(createItem(player, "LIME_WOOL", () -> {
      // Load everything from our inv -> kit.
      for (int i = 0; i < 9; i++) {
        final GUIItem item = getItem(i);
        if (item == null)
          continue;
        kit.getItems().put(i, item.getItem());
      }
      for (int i = 20; i < 54; i++) {
        if (isInterfaring(i))
          continue;
        final GUIItem item = getItem(i);
        if (item == null)
          continue;
        kit.getItems().put(-1, item.getItem());
      }
      if (getItem(18) != null && !(getItem(18).equals(getArmourHelmet(player))))
        kit.getArmour().add(getItem(18).getItem());
      if (getItem(27) != null && !(getItem(27).equals(getArmourChestplate(player))))
        kit.getArmour().add(getItem(27).getItem());
      if (getItem(36) != null && !(getItem(36).equals(getArmourLeggings(player))))
        kit.getArmour().add(getItem(36).getItem());
      if (getItem(45) != null && !(getItem(45).equals(getArmourHelmet(player))))
        kit.getArmour().add(getItem(45).getItem());

      KitManager.getInstance().addKit(kit);
      // Close after everything is complete.
      player.closeInventory();
    }, Message.build("&a&lConfirm"), Message.build("&7If the setup is complete", "&7Click this button.")), 53);

  }

  @EventHandler
  public void onClick(InventoryClickEvent event) {
    if (!(event.getWhoClicked() instanceof Player))
      return;
    final Player player = (Player) event.getWhoClicked();
    final Inventory inventory = event.getClickedInventory();
    if (!inventory.equals(player.getInventory()))
      return;
    if (getPlayers().contains(player)) {
      // Player is in our AddKitMenu
      final ItemStack item = event.getCurrentItem();
      if (item == null || item.getType() == Material.AIR || !isNotArmour(item.getType()))
        return;
      for (int i = 0; i < 9; i++) {
        if (item.equals(inventory.getItem(i))) {
          // Add item to hotbar
          setItem(createItem(player, item.getType().name(), () -> {
              },
              Message.build(item.getItemMeta().getDisplayName()),
              Message.build(item.getItemMeta().getLore())), i);
          return;
        }
      }

      final int index = getIndexFromItemStack(item, inventory);
      final GUIItem guiItem = createItem(player, item.getType().name(), () -> {
          },
          Message.build(item.getItemMeta().getDisplayName()),
          Message.build(item.getItemMeta().getLore()));
      if (index == -1)
        addItem(guiItem);
      else setItem(guiItem, index);
    }
  }


  private GUIItem createItem(Player player, String materialName, Runnable onUse, Message name, Message lore) {
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

  private GUIItem getFillerGlass() {
    return createItem(null, "BLACK_STAINED_GLASS_PANE", () -> {
    }, Message.build(""), Message.build(""));
  }

  private GUIItem getRedGlass() {
    return createItem(null, "RED_STAINED_GLASS_PANE", () -> {
    }, Message.build(""), Message.build(""));
  }

  private GUIItem getArmourHelmet(Player player) {
    return createItem(player, "LEATHER_HELMET", () -> {
      final ItemStack item = player.getItemOnCursor();
      setArmourOnKit(item, player, ArmourType.HELMET);
    }, Message.build("Put helmet here"), Message.build());
  }

  private GUIItem getArmourChestplate(Player player) {
    return createItem(player, "LEATHER_CHESTPLATE", () -> {
      setArmourOnKit(player.getItemOnCursor(), player, ArmourType.CHESTPLATE);
    }, Message.build("Put chestplate here"), Message.build());
  }

  private GUIItem getArmourLeggings(Player player) {
    return createItem(player, "LEATHER_LEGGINGS", () -> {
      setArmourOnKit(player.getItemOnCursor(), player, ArmourType.LEGGINGS);
    }, Message.build("Put leggings here"), Message.build());
  }

  private GUIItem getArmourBoots(Player player) {
    return createItem(player, "LEATHER_BOOTS", () -> {
      setArmourOnKit(player.getItemOnCursor(), player, ArmourType.BOOTS);
    }, Message.build("Put boots here"), Message.build());
  }

  private void setArmourOnKit(ItemStack armourPiece, Player player, ArmourType type) {
    if (armourPiece == null || !(KitsUtil.typeCheck(armourPiece, type)))
      return;
    kit.addArmour(armourPiece);
    setItem(createItem(player, armourPiece.getType().name(), () -> {
        }
        , Message.build(armourPiece.getItemMeta().getDisplayName()), Message.build(armourPiece.getItemMeta().getLore())
    ), 18);
  }

  private boolean isInterfaring(int bound) {
    final List<Integer> fixedIndex = Arrays.asList(18, 19, 27, 28, 36, 37, 45, 46);
    return fixedIndex.contains(bound);
  }

  private boolean isNotArmour(Material mat) {
    return mat.name().endsWith("HELMET") || mat.name().endsWith("CHESTPLATE") || mat.name().endsWith("LEGGINGS") || mat.name().endsWith("BOOTS");
  }

  private int getIndexFromItemStack(ItemStack item, Inventory inventory) {
    for (int i = 0; i <= 35; i++) {
      final ItemStack x = inventory.getItem(i);
      if (x == null || x.getType() == Material.AIR)
        continue;
      if (x.equals(item))
        return i;
    }
    return -1;
  }


}
