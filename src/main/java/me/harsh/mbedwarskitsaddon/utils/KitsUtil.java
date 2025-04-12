package me.harsh.mbedwarskitsaddon.utils;

import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;
import me.clip.placeholderapi.PlaceholderAPI;
import me.harsh.mbedwarskitsaddon.MBedwarsKitsPlugin;
import me.harsh.mbedwarskitsaddon.kits.Kit;
import me.harsh.mbedwarskitsaddon.kits.KitManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@UtilityClass
public class KitsUtil {

  public String KIT_CURRENT_PATH = "kits_selected";
  public String KIT_ADMIN_PERM = "kitsaddon.admin";

  public void log(String message, boolean warn){
    if (warn)
      MBedwarsKitsPlugin.getInstance().getServer().getLogger().warning(
          colorize(message)
      );
    MBedwarsKitsPlugin.getInstance().getServer().getLogger().info(
        colorize(message)
    );
  }

  public void log(List<String> messages, boolean warn){
    messages.forEach(s -> log(s,warn));
  }


  public void tell(Player player, String message){
    player.sendMessage(colorize(PlaceholderAPI.setPlaceholders(player, message)));
  }
  public void tell(Player player, String message, Kit kit){
    player.sendMessage(colorize(PlaceholderAPI.setPlaceholders(player, message
        .replace("%kit_name%", kit.getName() == null ? "": kit.getName())
        .replace("%kit_id%", kit.getId())
    )));
  }
  public List<String> colorizeList(List<String> str){
    final List<String> result = new ArrayList<>();
    str.forEach(s -> result.add(colorize(s)));
    return result;
  }
  public void tell(Player player, List<String> message, Kit kit){
    message.forEach(s -> tell(player, s, kit));
  }
  public void tell(Player player, List<String> message){
    message.forEach(s -> tell(player, s));
  }

  public String colorize(String message){
    return ChatColor.translateAlternateColorCodes('&', message);
  }

  public String getKitPerm(Kit kit){
    return "kitsaddon." + kit.getId();
  }

  public boolean typeCheck(ItemStack itemStack, ArmourType type){
    if (type == ArmourType.HELMET && !(itemStack.getType().name().endsWith("HELMET")))
      return false;
    if (type == ArmourType.CHESTPLATE && !(itemStack.getType().name().endsWith("CHESTPLATE")))
      return false;
    if (type == ArmourType.LEGGINGS && !(itemStack.getType().name().endsWith("LEGGINGS")))
      return false;
    return type != ArmourType.BOOTS || itemStack.getType().name().endsWith("BOOTS");
  }

  public boolean isArmour(ItemStack item){
    final String name = item.getType().name();
    return name.endsWith("HELMET") || name.endsWith("CHESTPLATE") || name.endsWith("LEGGINGS") || name.endsWith("BOOTS");
  }

  public String getFormattedMaterialName(ItemStack item) {
    if (item == null) return "null";
    String name = item.getType().toString();

    name = name.replace('_', ' ');

    String[] words = name.split(" ");
    StringBuilder result = new StringBuilder();

    for (int i = 0; i < words.length; i++) {
      if (!words[i].isEmpty()) {
        result.append(words[i].substring(0, 1).toUpperCase())
            .append(words[i].substring(1).toLowerCase());

        if (i < words.length - 1) {
          result.append(" ");
        }
      }
    }

    return result.toString();
  }

  public int getTotalPageNo() {
    final int kitNo = KitManager.getInstance().getLoadedKits().size();
    final int pageFloor = Math.floorDiv(kitNo, 27);
    final double pageTemp = (kitNo / 27);
    int pages = 1;
    if (pageTemp == pageFloor && (pageTemp != 0 || pageFloor != 0)) {
      pages = pageFloor;
    } else if (pageTemp > pageFloor) {
      pages = pageFloor + 1;
    }
    return pages;
  }
  public ArmourType getArmourType(ItemStack item){
    if (item.getType().name().endsWith("HELMET"))
      return ArmourType.HELMET;
    if (item.getType().name().endsWith("CHESTPLATE"))
      return ArmourType.CHESTPLATE;
    if (item.getType().name().endsWith("LEGGINGS"))
      return ArmourType.LEGGINGS;
    if (item.getType().name().endsWith("BOOTS"))
      return ArmourType.BOOTS;

    return null;
  }
  public int getIndexFromType(ItemStack item){
    final ArmourType type = getArmourType(item);
    switch (type){
      case HELMET:
        return 18;
      case CHESTPLATE:
        return 27;
      case LEGGINGS:
        return 36;
      case BOOTS:
        return 45;
      default:
        return -1;
    }
  }

}
