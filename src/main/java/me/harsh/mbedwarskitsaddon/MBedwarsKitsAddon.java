package me.harsh.mbedwarskitsaddon;

import de.marcely.bedwars.api.BedwarsAddon;
import org.bukkit.plugin.PluginManager;

public class MBedwarsKitsAddon extends BedwarsAddon {

  private final MBedwarsKitsPlugin plugin;

  public MBedwarsKitsAddon(MBedwarsKitsPlugin plugin) {
    super(plugin);
    this.plugin = plugin;
  }


  public void registerEvents() {
    final MBedwarsKitsPlugin plugin = MBedwarsKitsPlugin.getInstance();
    final PluginManager manager = plugin.getServer().getPluginManager();

    // REGISTER EVENTS.
  }

  public void registerCommands() {
    // REGISTER COMMANDS.
//    new CommandGroup("mbedwarskits");
  }




  @Override
  public String getName() {
    return plugin.getName();
  }
}
