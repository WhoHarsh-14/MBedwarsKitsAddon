package me.harsh.mbedwarskitsaddon.utils;

import java.util.List;
import lombok.experimental.UtilityClass;
import me.clip.placeholderapi.PlaceholderAPI;
import me.harsh.mbedwarskitsaddon.MBedwarsKitsPlugin;
import me.harsh.mbedwarskitsaddon.kits.Kit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@UtilityClass
public class KitsUtil {

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
        .replace("%kit_name%", kit.getName())
        .replace("%kit_id%", kit.getId())
    )));
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

}
