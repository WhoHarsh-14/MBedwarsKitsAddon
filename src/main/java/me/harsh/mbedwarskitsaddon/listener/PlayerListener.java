package me.harsh.mbedwarskitsaddon.listener;

import de.marcely.bedwars.api.arena.ArenaStatus;
import de.marcely.bedwars.api.event.arena.ArenaStatusChangeEvent;
import de.marcely.bedwars.api.player.PlayerDataAPI;
import de.marcely.bedwars.api.remote.RemotePlayer;
import me.harsh.mbedwarskitsaddon.MBedwarsKitsPlugin;
import me.harsh.mbedwarskitsaddon.config.KitConfig;
import me.harsh.mbedwarskitsaddon.kits.Kit;
import me.harsh.mbedwarskitsaddon.kits.KitManager;
import me.harsh.mbedwarskitsaddon.utils.KitsUtil;
import org.bukkit.Bukkit;
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
          final String key = playerProperties.get(KitsUtil.KIT_CURRENT_PATH).orElse("None");

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
                case LEGGINGS:
                  inventory.setLeggings(itemStack);
                case BOOTS:
                  inventory.setBoots(itemStack);
              }
            }
            KitsUtil.tell(player, KitConfig.getMessagesMap().get("Kit_given"), kit);
          }, KitConfig.GIVE_KIT_DELAY * 20L);
//          inventory.setArmorContents(kit.getArmour().toArray(new ItemStack[0]));
        });
      }
    }
  }
}
