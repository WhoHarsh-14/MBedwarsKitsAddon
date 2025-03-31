package me.harsh.mbedwarskitsaddon.listener;

import de.marcely.bedwars.api.event.arena.RoundStartEvent;
import de.marcely.bedwars.api.player.PlayerDataAPI;
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
  public void onStart(RoundStartEvent event) {
    // Give kits
    for (Player player : event.getArena().getPlayers()) {
      if (!player.isOnline())
        continue;
      PlayerDataAPI.get().getProperties(player.getUniqueId(), playerProperties -> {
        final String key = playerProperties.get(KitsUtil.KIT_CURRENT_PATH).orElse("None");
        if (key.equalsIgnoreCase("None")) {
          System.out.println("Key is None");
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
          System.out.println(kit.getArmour().toString());
          System.out.println(kit.getItems().values());
          kit.getItems().forEach((integer, itemStack) -> {
            System.out.println("Adding Item at index " + integer + " with the item: " + itemStack.getType().name());
            if (integer == -1)
              inventory.addItem(itemStack);
            else inventory.setItem(integer, itemStack);
          });
          for (ItemStack itemStack : kit.getArmour()) {
            System.out.println("Setting armour : " + itemStack.getType().name());
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
      });
    }
  }
}
