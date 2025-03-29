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

  public KitCreateMenu(Kit kit){
    super(6, KitsUtil.colorize("&a&lNEW KIT's ITEMS"));
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

  }

  @EventHandler
  public void onClick(InventoryClickEvent event){
    if (!(event.getWhoClicked() instanceof Player))
      return;
    final Player player = (Player) event.getWhoClicked();
    final Inventory inventory = event.getClickedInventory();
    if (!inventory.equals(player.getInventory()))
      return;
    if (getPlayers().contains(player)){
      // Player is in our AddKitMenu
      final ItemStack item = event.getCurrentItem();
      if (item == null || item.getType() == Material.AIR || !isNotArmour(item.getType()))
        return;
      for (int i = 0; i < 9; i++) {
        if (item.equals(inventory.getItem(i))){
          // Add item to hotbar
          setItem(createItem(player, item.getType().name(), () -> {},
              Message.build(item.getItemMeta().getDisplayName()),
              Message.build(item.getItemMeta().getLore())), i);
          return;
        }
      }
      final Random random = new Random();
      int i = random.nextInt(20,53);
      for (int j = 0; j < 5; j++) {
        if (!isInterfaring(i))
          break;
        i = random.nextInt(20,53);
      }
      setItem(createItem(player, item.getType().name(), () -> {},
          Message.build(item.getItemMeta().getDisplayName()),
          Message.build(item.getItemMeta().getLore())), i);
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

  private GUIItem getFillerGlass(){
    return createItem(null, "BLACK_STAINED_GLASS_PANE", () -> {}, Message.build(""), Message.build(""));
  }
  private GUIItem getRedGlass(){
    return createItem(null, "RED_STAINED_GLASS_PANE", () -> {}, Message.build(""), Message.build(""));
  }
  private GUIItem getArmourHelmet(Player player){
    return createItem(player, "LEATHER_HELMET", () -> {
      final ItemStack item = player.getItemOnCursor();
      setArmourOnKit(item, player, ArmourType.HELMET);
    }, Message.build("Put helmet here"), Message.build());
  }
  private GUIItem getArmourChestplate(Player player){
    return createItem(player, "LEATHER_CHESTPLATE", () -> {
      setArmourOnKit(player.getItemOnCursor(), player, ArmourType.CHESTPLATE);
    }, Message.build("Put chestplate here"), Message.build());
  }
  private GUIItem getArmourLeggings(Player player){
    return createItem(player, "LEATHER_LEGGINGS", () -> {
      setArmourOnKit(player.getItemOnCursor(), player, ArmourType.LEGGINGS);
    }, Message.build("Put leggings here"), Message.build());
  }
  private GUIItem getArmourBoots(Player player){
    return createItem(player, "LEATHER_BOOTS", () -> {
      setArmourOnKit(player.getItemOnCursor(), player, ArmourType.BOOTS);
    }, Message.build("Put boots here"), Message.build());
  }

  private void setArmourOnKit(ItemStack armourPiece, Player player, ArmourType type){
    if (armourPiece == null || !(typeCheck(armourPiece, type)))
      return;
    kit.addArmour(armourPiece);
    setItem(createItem(player,armourPiece.getType().name(), () -> {}
        , Message.build(armourPiece.getItemMeta().getDisplayName()), Message.build(armourPiece.getItemMeta().getLore())
    ), 18);
  }

  private boolean isInterfaring(int bound){
    final List<Integer> fixedIndex = Arrays.asList(18, 19,27,28,36,37,45,46);
    return fixedIndex.contains(bound);
  }

  private boolean isNotArmour(Material mat){
    return mat.name().endsWith("HELMET") || mat.name().endsWith("CHESTPLATE") || mat.name().endsWith("LEGGINGS") || mat.name().endsWith("BOOTS");
  }
  private boolean typeCheck(ItemStack itemStack, ArmourType type){
    if (type == ArmourType.HELMET && !(itemStack.getType().name().endsWith("HELMET")))
      return false;
    if (type == ArmourType.CHESTPLATE && !(itemStack.getType().name().endsWith("CHESTPLATE")))
      return false;
    if (type == ArmourType.LEGGINGS && !(itemStack.getType().name().endsWith("LEGGINGS")))
      return false;
    if (type == ArmourType.BOOTS && !(itemStack.getType().name().endsWith("BOOTS")))
      return false;
    return true;
  }

}
