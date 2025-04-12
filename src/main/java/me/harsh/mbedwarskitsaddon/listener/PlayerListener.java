package me.harsh.mbedwarskitsaddon.listener;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.event.arena.RoundStartEvent;
import de.marcely.bedwars.api.event.player.PlayerIngameRespawnEvent;
import de.marcely.bedwars.api.player.PlayerDataAPI;
import java.util.Set;
import me.harsh.mbedwarskitsaddon.MBedwarsKitsPlugin;
import me.harsh.mbedwarskitsaddon.config.KitConfig;
import me.harsh.mbedwarskitsaddon.kits.Kit;
import me.harsh.mbedwarskitsaddon.kits.KitManager;
import me.harsh.mbedwarskitsaddon.utils.KitsUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class PlayerListener implements Listener {

  @EventHandler
  public void onStart(RoundStartEvent event) {
    // No kits for blocked arenas haha
    final Arena arena = event.getArena();
    if (KitConfig.BLOCKED_ARENAS.contains(arena.getName()))
      return;

    // Give kits
    for (Player player : event.getArena().getPlayers()) {
      giveKitSafely(player, arena, KitConfig.GIVE_KIT_DELAY);
    }
  }

  @EventHandler
  public void onRespawn(PlayerIngameRespawnEvent event){
    // No kits for blocked arenas haha
    final Arena arena = event.getArena();

    if (KitConfig.BLOCKED_ARENAS.contains(arena.getName()) || !KitConfig.GIVE_KIT_ON_RESPAWN)
      return;

    giveKitSafely(event.getPlayer(), arena, 1);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onJoin(PlayerJoinEvent event){
    Bukkit.getScheduler().runTaskLater(MBedwarsKitsPlugin.getInstance(), () -> {
      PlayerDataAPI.get().getProperties(event.getPlayer().getUniqueId(), playerProperties -> {
        // Load current kit into cache.
        final String kitId = playerProperties.get(KitsUtil.KIT_CURRENT_PATH).orElse("None");
        KitManager.getInstance().setKit(event.getPlayer().getUniqueId(), kitId);
      });
    }, 20L* 5);
  }


  public void giveKitSafely(Player player, Arena arena, int delay){
    if (!player.isOnline())
      return;
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
      // Gives the armour + dye it to team's colour if it's leather.
      giveArmour(kit.getArmour(), player, arena.getPlayerTeam(player));
      KitsUtil.tell(player, KitConfig.getMessagesMap().get("Kit_given"), kit);
    }, delay * 20L);
  }

  public void giveArmour(Set<ItemStack> armour, Player player, Team team){
    final PlayerInventory inventory = player.getInventory();
    if (inventory == null)
      return;

    for (ItemStack item : armour) {
      if (item.getType().name().startsWith("LEATHER")){
        final LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(team.getBukkitColor());
        item.setItemMeta(meta);
      }
      switch (KitsUtil.getArmourType(item)){
        case HELMET:
          inventory.setHelmet(item);
          break;
        case CHESTPLATE:
          inventory.setChestplate(item);
          break;
        case LEGGINGS:
          inventory.setLeggings(item);
          break;
        case BOOTS:
          inventory.setBoots(item);
          break;
      }
    }
  }
}
