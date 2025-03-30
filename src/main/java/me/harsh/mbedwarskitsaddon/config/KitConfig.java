package me.harsh.mbedwarskitsaddon.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import me.harsh.mbedwarskitsaddon.MBedwarsKitsPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class KitConfig {

  @Getter
  private static FileConfiguration config;
  @Getter
  private static Map<String, List<String>> messagesMap = new HashMap<>();

  // CONSTs
  public static List<String> COMMAND_ALIASES;

  // FEATURES
  public static boolean ENABLED;
  public static boolean GAME_SERVER;
  public static boolean PER_KIT_PERM;
  public static List<String> BLOCKED_ARENAS;

  // MENU
  public static String KIT_MENU_TITLE;



  public KitConfig(MBedwarsKitsPlugin plugin) {
    config = plugin.getConfig();
    plugin.saveDefaultConfig();
  }

  public void loadConfiguration() {

    COMMAND_ALIASES = getConfig().getStringList("Command_alias");


    //              FEATURES
    final ConfigurationSection features = config.getConfigurationSection("Features");
    if (features != null){
      ENABLED = features.getBoolean("Enabled");
      GAME_SERVER = features.getBoolean("Game_server");
      PER_KIT_PERM = features.getBoolean("Per_kit_perm");
      BLOCKED_ARENAS = features.getStringList("Blocked_arenas");
    }

    //                MENU
    final ConfigurationSection menu = config.getConfigurationSection("Menu");
    if (menu != null){
      KIT_MENU_TITLE = menu.getString("Title");
    }



    //                  MESSAGES
    final ConfigurationSection messages = config.getConfigurationSection("Messages");
    if (messages != null) {
      for (String key : messages.getKeys(false)) {
        messagesMap.put(key, messages.getStringList(key));
      }
    }

  }



}
