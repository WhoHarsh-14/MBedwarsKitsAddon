package me.harsh.mbedwarskitsaddon.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import me.harsh.mbedwarskitsaddon.MBedwarsKitsPlugin;
import me.harsh.mbedwarskitsaddon.kits.KitManager;
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
  public static int GIVE_KIT_DELAY;
  public static boolean GIVE_KIT_ON_RESPAWN;
  public static List<String> BLOCKED_ARENAS;

  // COINS HOOK
  public static int KIT_DEFAULT_COINS = 0;
  public static Map<String,Integer> KIT_PER_COINS = new HashMap<>();

  // MENU
  public static String KIT_MENU_TITLE;
  public static String KIT_MENU_PLAYER_NAME;
  public static List<String> KIT_MENU_PLAYER_DESCRIPTION;
  public static String KIT_MENU_BACK_NAME;
  public static List<String> KIT_MENU_BACK_DESCRIPTION;
  public static String KIT_MENU_NEXT_NAME;
  public static List<String> KIT_MENU_NEXT_DESCRIPTION;



  public KitConfig(MBedwarsKitsPlugin plugin) {
    config = plugin.getConfig();
  }

  public void loadConfiguration() {

    COMMAND_ALIASES = getConfig().getStringList("Command_alias");


    //              FEATURES
    final ConfigurationSection features = config.getConfigurationSection("Features");
    if (features != null){
      ENABLED = features.getBoolean("Enabled");
      GAME_SERVER = features.getBoolean("Game_server");
      PER_KIT_PERM = features.getBoolean("Per_kit_perm");
      GIVE_KIT_DELAY = features.getInt("Give_kit_delay");
      GIVE_KIT_ON_RESPAWN = features.getBoolean("Give_kit_on_respawn");
      BLOCKED_ARENAS = features.getStringList("Blocked_arenas");
    }

    //             COINS HOOK
    if (GAME_SERVER){
      final ConfigurationSection coins = config.getConfigurationSection("Kits");
      if (coins != null){
        KIT_DEFAULT_COINS = coins.getInt("Default");
        for (String key : coins.getKeys(false)) {
          if (KitManager.getInstance().getLoadedKits().containsKey(key)){
            // Key = Kit
            KIT_PER_COINS.put(key, config.getInt(key));
          }
        }
      }
    }


    //                MENU
    final ConfigurationSection menu = config.getConfigurationSection("Menu");
    if (menu != null){
      KIT_MENU_TITLE = menu.getString("Title");
      KIT_MENU_PLAYER_NAME = menu.getString("Player_head.Name");
      KIT_MENU_PLAYER_DESCRIPTION = menu.getStringList("Player_head.Description");
      KIT_MENU_BACK_NAME = menu.getString("Previous_page.Name");
      KIT_MENU_BACK_DESCRIPTION = menu.getStringList("Previous_page.Description");
      KIT_MENU_NEXT_NAME = menu.getString("Next_page.Name");
      KIT_MENU_NEXT_DESCRIPTION = menu.getStringList("Next_page.Description");
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
