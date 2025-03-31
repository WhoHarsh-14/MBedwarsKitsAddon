package me.harsh.mbedwarskitsaddon.kits;

import de.marcely.bedwars.api.BedwarsAPI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Getter;
import me.harsh.mbedwarskitsaddon.config.KitConfig;
import org.bukkit.inventory.ItemStack;

@Getter
public class KitManager {

  @Getter
  private static final KitManager instance = new KitManager();
  private final Map<String, Kit> loadedKits = new HashMap<>();

  private KitManager() {
  }

  public void loadKits() {
    // Load kits from our Dummy Player Props
    BedwarsAPI.getPlayerDataAPI().getProperties(new UUID(0, 0), playerProperties -> {
      final List<String> nameKits = playerProperties.getStoredKeys()
          .stream()
          .filter(s -> s.startsWith("kits"))
          .filter(s -> s.endsWith("name"))
          .collect(Collectors.toList());


      for (String key : nameKits) {
        final String id = key.split("_")[1];
        final String name = playerProperties.get(key).orElse("[Error parsing name]");
        final ItemStack icon = playerProperties.getItemStack("kits_" + id + "_icon").orElse(null);

        if (loadedKits.containsKey(id))
          continue;

        // Loading maps/set
        final Map<Integer, ItemStack> itemStacks = new HashMap<>();
        final Set<ItemStack> armours = new HashSet<>();

        List<String> itemKeys = playerProperties.getStoredKeys()
            .stream()
            .filter(s -> s.startsWith("kits_" + id + "_items_"))
            .filter(s -> !s.endsWith("_index"))
            .collect(Collectors.toList());


        for (String itemKey : itemKeys) {
          ItemStack item = playerProperties.getItemStack(itemKey).orElse(null);
          int index = playerProperties.getInt(itemKey + "_index").orElse(-1);

          if (item != null && index >= 0) {
            itemStacks.put(index, item);
          }
        }

        List<String> armorKeys = playerProperties.getStoredKeys()
            .stream()
            .filter(s -> s.startsWith("kits_" + id + "_armour_"))
            .collect(Collectors.toList());

        for (String armorKey : armorKeys) {
          ItemStack armor = playerProperties.getItemStack(armorKey).orElse(null);
          if (armor != null) {
            armours.add(armor);
          }
        }

        loadedKits.put(id, new Kit(id, name, icon, itemStacks, armours));
      }
    });
  }

  public void saveKits() {
    if (KitConfig.GAME_SERVER)
      return;
    BedwarsAPI.getPlayerDataAPI().getProperties(new UUID(0, 0), playerProperties -> {
      final List<String> nameKits = playerProperties.getStoredKeys()
          .stream()
          .filter(s -> s.startsWith("kits"))
          .filter(s -> s.endsWith("name"))
          .collect(Collectors.toList());

      if (nameKits.size() == this.getLoadedKits().size())
        return;

      // There is a change in kits perhaps
      for (Kit value : this.getLoadedKits().values()) {
        final String path = "kits_" + value.getId() + "_";
        // Kit is present already.
        if (playerProperties.get(path + "name")
            .orElse("")
            .equalsIgnoreCase(value.getName()))
          continue;

        // Saves the new kit.
        playerProperties.set(path + "name", value.getName());
        playerProperties.set(path + "icon", value.getIcon());
        System.out.println(value.getItems().values());
        value.getItems().forEach((integer, itemStack) -> {
          // NO DEBUG MSG HERE
          System.out.println("Saving item at index " + integer);
          System.out.println("Item is " + itemStack);
          System.out.println("Display name " + itemStack.getItemMeta().getDisplayName());

          playerProperties.set(path + "items_" +
                  (itemStack.getItemMeta().hasDisplayName() ? itemStack.getItemMeta().getDisplayName() : itemStack.getType().name())
              , itemStack);
          playerProperties.set(path + "items_" +
              (itemStack.getItemMeta().hasDisplayName() ? itemStack.getItemMeta().getDisplayName() : itemStack.getType().name())
              + "_index", integer);
        });


        value.getArmour().forEach(itemStack -> playerProperties.set(path + "armour_" +
                (itemStack.getItemMeta().hasDisplayName() ? itemStack.getItemMeta().getDisplayName() : itemStack.getType().name())
            , itemStack));


      }
    });
  }

  public void loadKitsFromConfig(){
    // TODO: Try to direct to kits.yml if exists load all the Kits from there into the player props.
  }

  public void loadKitsIntoConfig(){
    // TODO: Save all kits into a file kits.yml
  }

  public void addKit(Kit kit) {
    this.getLoadedKits().put(kit.getId(), kit);
  }


  public void removeAllKits(){
    this.getLoadedKits().values().forEach(kit -> removeKit(kit.getId()));
  }

  // Have to manually remove from player data
  public void removeKit(String kitId) {
    final Kit kit = getLoadedKits().get(kitId);
    final String path = "kits_" + kit.getId() + "_";
    this.getLoadedKits().remove(kit.getId());
    BedwarsAPI.getPlayerDataAPI().getProperties(new UUID(0, 0), playerProperties -> {
      playerProperties.remove(path + "name");
      playerProperties.remove(path + "icon");
      kit.getItems().forEach((integer, itemStack) -> {
        playerProperties.remove(path + "items_" +
            (itemStack.getItemMeta().hasDisplayName() ? itemStack.getItemMeta().getDisplayName() : itemStack.getType().name()));
        playerProperties.remove(path + "items_" +
            (itemStack.getItemMeta().hasDisplayName() ? itemStack.getItemMeta().getDisplayName() : itemStack.getType().name()) + "_index");
      });
//      kit.getItems().forEach(itemStack -> playerProperties.remove(path + "items." + itemStack.getItemMeta().getDisplayName()));
      kit.getArmour().forEach(itemStack -> playerProperties.remove(path + "armour_" +
          (itemStack.getItemMeta().hasDisplayName() ? itemStack.getItemMeta().getDisplayName() : itemStack.getType().name())));
    });
  }


}