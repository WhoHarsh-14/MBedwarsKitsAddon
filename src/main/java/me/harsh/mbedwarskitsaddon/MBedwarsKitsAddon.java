package me.harsh.mbedwarskitsaddon;

import de.marcely.bedwars.api.BedwarsAddon;
import java.util.Arrays;
import me.harsh.mbedwarskitsaddon.commands.CommandGroup;
import me.harsh.mbedwarskitsaddon.commands.KitCreateCommand;
import me.harsh.mbedwarskitsaddon.listener.PlayerListener;
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
    manager.registerEvents(new PlayerListener(), plugin);
//    manager.registerEvents(new KitCreateMenu(), plugin);
  }

  public void registerCommands() {
    plugin.getCommand("kit").setExecutor(new CommandGroup("kit", Arrays.asList(
        new KitCreateCommand()))
    );
  }




  @Override
  public String getName() {
    return plugin.getName();
  }
}
