package me.harsh.mbedwarskitsaddon.kits;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.tools.Helper;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Getter;
import me.harsh.mbedwarskitsaddon.config.KitConfig;
import org.bukkit.Material;
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

      // Kits.<id>.name
      for (String key : nameKits) {
        final String id = key.split("\\.")[1];
        final String name = playerProperties.get(key).orElse("[Error parsing name]");
        final ItemStack icon = playerProperties.getItemStack("kits." + id + ".icon").orElse(null);

        if (loadedKits.containsKey(id))
          continue;

        // Loading Items.
        final Set<ItemStack> itemStacks = new HashSet<>();
        // kits.<id>.Items.item1/item2/....
        playerProperties.getStoredKeys()
            .stream()
            .filter(s -> s.contains("items"))
            .filter(s -> s.contains(id))
            .forEach(s -> itemStacks.add(playerProperties.getItemStack(s).orElse(null)));

        // finally load it into the map
        loadedKits.put(id, new Kit(id, name, icon, itemStacks));
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
        final String path = "kits." + value.getId() + ".";
        // Kit is present already.
        if (playerProperties.get(path + "name")
            .orElse("")
            .equalsIgnoreCase(value.getName()))
          continue;

        // Saves the new kit.
        playerProperties.set(path + "name", value.getName());
        playerProperties.set(path + "icon", value.getIcon());
        value.getItems().forEach(itemStack -> playerProperties.set(path + "items." + itemStack.getItemMeta().getDisplayName(), itemStack));


      }
    });
  }

  public void addKit(Kit kit) {
    this.getLoadedKits().put(kit.getId(), kit);
  }

  // Have to manually remove from player data
  public void removeKit(Kit kit) {
    final String path = "kits." + kit.getId() + ".";
    this.getLoadedKits().remove(kit.getId());
    BedwarsAPI.getPlayerDataAPI().getProperties(new UUID(0, 0), playerProperties -> {
      playerProperties.remove(path + "name");
      playerProperties.remove(path + "icon");
      kit.getItems().forEach(itemStack -> playerProperties.remove(path + "items." + itemStack.getItemMeta().getDisplayName()));
    });
  }


}
