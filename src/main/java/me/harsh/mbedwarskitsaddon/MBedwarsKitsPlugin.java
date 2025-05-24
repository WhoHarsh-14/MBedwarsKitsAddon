package me.harsh.mbedwarskitsaddon;

import de.marcely.bedwars.api.AddonAPI;
import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.GameAPI;
import java.util.Arrays;
import lombok.Getter;
import me.harsh.mbedwarskitsaddon.config.KitConfig;
import me.harsh.mbedwarskitsaddon.kits.KitManager;
import me.harsh.mbedwarskitsaddon.placeholders.KitPlaceholders;
import me.harsh.mbedwarskitsaddon.special.SpecialKitItem;
import me.harsh.mbedwarskitsaddon.utils.KitsUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public final class MBedwarsKitsPlugin extends JavaPlugin {

  public static final int MIN_MBEDWARS_API_VER = 200;
  public static final String MIN_MBEDWARS_VER_NAME = "5.5";

  @Getter
  private static MBedwarsKitsPlugin instance;
  @Getter
  private static MBedwarsKitsAddon addon;
  @Getter
  private KitConfig kitConfig;

  @Override
  public void onEnable() {
    instance = this;

    if (!checkMBedwars())
      return;
    if (!registerAddon())
      return;

    addon.registerCommands();
    addon.registerEvents();
    GameAPI.get().registerLobbyItemHandler(new SpecialKitItem());
    saveDefaultConfig();
    kitConfig = new KitConfig(this);
    kitConfig.loadConfiguration();


    final PluginDescriptionFile pdf = this.getDescription();

    KitsUtil.log(Arrays.asList(
        "------------------------------",
        pdf.getName() + " For MBedwars",
        "By: " + pdf.getAuthors(),
        "Version: " + pdf.getVersion(),
        "------------------------------"
        )
    , false);

    loadKitsAddon();
  }

  public void loadKitsAddon() {
    if (!KitConfig.ENABLED)
      addon.unregister();

    this.getServer().getScheduler().runTaskLater(this, () -> {
      KitManager.getInstance().loadKits();
      if (AddonAPI.get().getByName("Cosmetics") != null && AddonAPI.get().getByName("Cosmetics").isRegistered()){
        KitsUtil.KIT_COINS_HOOK = true;
        KitsUtil.log(" Coins found! Hooking....", false);
        Bukkit.getScheduler().runTaskLater(this, () -> {KitManager.getInstance().saveCoins();}, 20);
      }

      if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
        new KitPlaceholders().register();
      }
    }, 40);

  }


  @Override
  public void onDisable() {
    KitManager.getInstance().saveKits();
  }

  private boolean checkMBedwars() {
    try {
      final Class<?> apiClass = Class.forName("de.marcely.bedwars.api.BedwarsAPI");
      final int apiVersion = (int) apiClass.getMethod("getAPIVersion").invoke(null);

      if (apiVersion < MIN_MBEDWARS_API_VER)
        throw new IllegalStateException();
    } catch (Exception e) {
      getLogger().warning("Sorry, your installed version of MBedwars is not supported. Please install at least v" + MIN_MBEDWARS_VER_NAME);
      Bukkit.getPluginManager().disablePlugin(this);

      return false;
    }

    return true;
  }

  private boolean registerAddon() {
    addon = new MBedwarsKitsAddon(this);

    if (!addon.register()) {
      getLogger().warning("It seems like this addon has already been loaded. Please delete duplicates and try again.");
      Bukkit.getPluginManager().disablePlugin(this);

      return false;
    }

    return true;
  }
}
