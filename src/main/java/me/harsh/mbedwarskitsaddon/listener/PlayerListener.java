package me.harsh.mbedwarskitsaddon.listener;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.event.arena.RoundStartEvent;
import de.marcely.bedwars.api.player.PlayerDataAPI;
import java.util.Map;
import java.util.UUID;
import me.harsh.mbedwarskitsaddon.MBedwarsKitsPlugin;
import me.harsh.mbedwarskitsaddon.config.KitConfig;
import me.harsh.mbedwarskitsaddon.kits.Kit;
import me.harsh.mbedwarskitsaddon.kits.KitManager;
import me.harsh.mbedwarskitsaddon.utils.KitsUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PlayerListener implements Listener {

  @EventHandler
  public void onStart(RoundStartEvent event) {
    // No kits for blocked arenas haha
    final Arena arena = event.getArena();
    if (KitConfig.BLOCKED_ARENAS.contains(arena.getName()))
      return;

    // Give kits
    for (Player player : event.getArena().getPlayers()) {
      if (!player.isOnline())
        continue;
      final String key = KitManager.getInstance().getPlayerCurrentKits().get(player.getUniqueId());
      if (key.equalsIgnoreCase("None")) {
        return;
      }

      final Kit kit = KitManager.getInstance().getLoadedKits().get(key);
      if (kit == null){
        KitsUtil.log("&c&lSorry, KIT instance not found", true);
        return;
      }

      Bukkit.getScheduler().runTaskLater(MBedwarsKitsPlugin.getInstance(), () -> {
        final PlayerInventory inventory = player.getInventory();
        inventory.clear();
        kit.getItems().forEach((integer, itemStack) -> {
          if (integer == -1)
            inventory.addItem(itemStack);
          else inventory.setItem(integer, itemStack);
        });
        for (ItemStack itemStack : kit.getArmour()) {
          switch (KitsUtil.getArmourType(itemStack)){
            case HELMET:
              inventory.setHelmet(itemStack);
              break;
            case CHESTPLATE:
              inventory.setChestplate(itemStack);
              break;
            case LEGGINGS:
              inventory.setLeggings(itemStack);
              break;
            case BOOTS:
              inventory.setBoots(itemStack);
              break;
          }
        }
        KitsUtil.tell(player, KitConfig.getMessagesMap().get("Kit_given"), kit);
      }, KitConfig.GIVE_KIT_DELAY * 20L);
    }
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent event){
    PlayerDataAPI.get().getProperties(event.getPlayer(), playerProperties -> {
      // Load current kit into cache.
      final String kitId = playerProperties.get(KitsUtil.KIT_CURRENT_PATH).orElse("None");
      if (KitManager.getInstance().getLoadedKits().containsKey(kitId))
        KitManager.getInstance().getPlayerCurrentKits().put(event.getPlayer().getUniqueId(), kitId);
    });
  }

  // Player may have switched servers at this point.
  @EventHandler
  public void onLeave(PlayerQuitEvent event){
    final Map<UUID,String> kitMap = KitManager.getInstance().getPlayerCurrentKits();
    PlayerDataAPI.get().getProperties(event.getPlayer().getUniqueId(), playerProperties -> {
      // Save current kit to props.
      if (!kitMap.containsKey(event.getPlayer().getUniqueId()))
        return;
      final String key = kitMap.get(event.getPlayer().getUniqueId());
      playerProperties.set(KitsUtil.KIT_CURRENT_PATH, key);
      kitMap.remove(event.getPlayer().getUniqueId());
    });
  }
}
