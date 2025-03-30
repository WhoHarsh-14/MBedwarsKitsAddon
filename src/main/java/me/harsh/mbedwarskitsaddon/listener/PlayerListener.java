package me.harsh.mbedwarskitsaddon.listener;

import de.marcely.bedwars.api.arena.ArenaStatus;
import de.marcely.bedwars.api.event.arena.ArenaStatusChangeEvent;
import de.marcely.bedwars.api.player.PlayerDataAPI;
import de.marcely.bedwars.api.remote.RemotePlayer;
import me.harsh.mbedwarskitsaddon.config.KitConfig;
import me.harsh.mbedwarskitsaddon.kits.Kit;
import me.harsh.mbedwarskitsaddon.kits.KitManager;
import me.harsh.mbedwarskitsaddon.utils.KitsUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PlayerListener implements Listener {

  @EventHandler
  public void onStart(ArenaStatusChangeEvent event) {
    if (event.getNewStatus() == ArenaStatus.RUNNING) {
      // Give kits
      for (RemotePlayer remotePlayer : event.getArena().asRemote().getRemotePlayers()) {
        if (!remotePlayer.isPlaying())
          continue;
        PlayerDataAPI.get().getProperties(remotePlayer.getUniqueId(), playerProperties -> {
          final String key = playerProperties.get("kits.current").orElse("None");

          if (key.equalsIgnoreCase("None"))
            return;

          final Kit kit = KitManager.getInstance().getLoadedKits().get(key);
          if (kit == null){
            KitsUtil.log("&c&lSorry, KIT instance not found", true);
            return;
          }
          final Player player = remotePlayer.asBukkit();
          if (player == null)
            return;
          final PlayerInventory inventory = player.getInventory();
          inventory.clear();
          kit.getItems().forEach((integer, itemStack) -> {
            if (integer == -1)
              inventory.addItem(itemStack);
            inventory.setItem(integer, itemStack);
          });
          inventory.setArmorContents(kit.getArmour().toArray(new ItemStack[0]));
          KitsUtil.tell(player, KitConfig.getMessagesMap().get("Kit_given"), kit);
        });
      }
    }
  }
}
