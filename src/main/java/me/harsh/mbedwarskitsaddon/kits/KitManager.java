package me.harsh.mbedwarskitsaddon.kits;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.tools.Helper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Getter;
import me.harsh.mbedwarskitsaddon.config.KitConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

@Getter
public class KitManager {

  @Getter
  private static final KitManager instance = new KitManager();
  private final Map<String, Kit> loadedKits = new HashMap<>();
  private final String DIRECTORY = "plugins/MBedwars/add-ons/MBedwarsKitsAddon/kits.yml";

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
        final ItemStack icon = Helper.get().parseItemStack(playerProperties.get("kits_" + id + "_icon").orElse(""));

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
          final ItemStack item = Helper.get().parseItemStack(playerProperties.get(itemKey).orElse(""));
          final int index = playerProperties.getInt(itemKey + "_index").orElse(-1);

          if (item != null && index >= 0) {
            itemStacks.put(index, item);
          } else if (item != null && index == (-1)) {
            itemStacks.put(-1, item);
          }
        }

        List<String> armorKeys = playerProperties.getStoredKeys()
            .stream()
            .filter(s -> s.startsWith("kits_" + id + "_armour_"))
            .collect(Collectors.toList());

        for (String armorKey : armorKeys) {
          final ItemStack armor = Helper.get().parseItemStack(playerProperties.get(armorKey).orElse(""));
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
        final String path = "kits_" + value.getId().toLowerCase().replace(" ", "_") + "_";
        // Kit is present already.
        if (playerProperties.get(path + "name")
            .orElse("")
            .equalsIgnoreCase(value.getName()))
          continue;

        // Saves the new kit.
        playerProperties.set(path + "name", value.getName());
        playerProperties.set(path + "icon", Helper.get().composeItemStack(value.getIcon()));

        value.getItems().forEach((integer, itemStack) -> {
          playerProperties.set(path + "items_" +
                  (itemStack.getItemMeta().hasDisplayName() ? itemStack.getItemMeta().getDisplayName().toLowerCase().replace(" ", "_") : itemStack.getType().name().toLowerCase().replace(" ", "_"))
              , Helper.get().composeItemStack(itemStack));
          playerProperties.set(path + "items_" +
              (itemStack.getItemMeta().hasDisplayName() ? itemStack.getItemMeta().getDisplayName().toLowerCase().replace(" ", "_") : itemStack.getType().name().toLowerCase().replace(" ", "_"))
              + "_index", integer);
        });

        value.getArmour().forEach(itemStack -> playerProperties.set(path + "armour_" +
                (itemStack.getItemMeta().hasDisplayName() ? itemStack.getItemMeta().getDisplayName().toLowerCase().replace(" ", "_") : itemStack.getType().name().toLowerCase().replace(" ", "_"))
            , Helper.get().composeItemStack(itemStack)));


      }
    });
  }

  public void loadKitsFromConfig() {
    final File kitsFile = new File(DIRECTORY);
    if (!kitsFile.exists()) return;

    final YamlConfiguration config = YamlConfiguration.loadConfiguration(kitsFile);
    final ConfigurationSection kitsSection = config.getConfigurationSection("Kits");
    if (kitsSection == null) return;

    for (final String kitId : kitsSection.getKeys(false)) {
      try {
        final ConfigurationSection kitSection = kitsSection.getConfigurationSection(kitId);
        if (kitSection == null) continue;

        final String name = kitSection.getString("Name");
        final String iconData = kitSection.getString("Icon");
        final ItemStack icon = Helper.get().parseItemStack(iconData);

        final Map<Integer, ItemStack> items = new HashMap<>();
        final List<Map<?, ?>> itemsMap = kitSection.getMapList("Items");

        for (Map<?, ?> map : itemsMap) {
          // int:str
          for (int i = 0; i <= 8; i++) {
            final String key = (String) map.get(i);
            if (key == null || !map.containsKey(i))
              continue;
            final ItemStack item = Helper.get().parseItemStack(key);
            if (item == null)
              continue;
            items.put(i, item);
          }

          // Remaining.
          for (int i = 50; i < 101; i++) {
            if (!map.containsKey(i))
              continue;
            final String key = (String) map.get(i);
            final ItemStack itemStack = Helper.get().parseItemStack(key);
            if (itemStack == null)
              continue;
            items.put(-1, itemStack);
          }

        }

        final Set<ItemStack> armour = new HashSet<>();
        final ConfigurationSection armourSection = kitSection.getConfigurationSection("Armour");
        if (armourSection != null) {
          for (final String armourKey : armourSection.getKeys(false)) {
            final String armourData = armourSection.getString(armourKey);
            final ItemStack armourPiece = Helper.get().parseItemStack(armourData);
            if (armourPiece != null) {
              armour.add(armourPiece);
            }
          }
        }

        final Kit kit = new Kit(kitId, name, icon, items, armour);
        addKit(kit);
      } catch (Exception e) {
//        System.out.println("Failed to load kit: " + kitId + " Error: " + e.getMessage());
        e.printStackTrace();
      }
    }
  }

  public void loadKitsIntoConfig() {
    final File kitsFile = new File(DIRECTORY);
    if (kitsFile.exists()) kitsFile.delete();
    if (!kitsFile.getParentFile().exists()) kitsFile.getParentFile().mkdirs();

    final YamlConfiguration config = new YamlConfiguration();

    for (final Kit kit : getLoadedKits().values()) {
      final String basePath = "Kits." + kit.getId() + ".";

      config.set(basePath + "Name", kit.getName());
      config.set(basePath + "Icon", Helper.get().composeItemStack(kit.getIcon()));

      final List<Map<Integer, String>> indexItemMapList = new ArrayList<>();
      kit.getItems().forEach((slot, item) -> {
        final Map<Integer, String> map = new HashMap<>();
        if (slot == -1){
          // Getting a random number above 50 (Add)
          final Random random = new Random();
          final int i = random.nextInt(50, 100);
          map.put(i, Helper.get().composeItemStack(item));
        }else map.put(slot, Helper.get().composeItemStack(item));

        indexItemMapList.add(map);
//        config.set(itemPath, Helper.get().composeItemStack(item));
//        config.set(itemPath + ".slot", slot);
      });
      config.set(basePath + "Items", indexItemMapList);

      kit.getArmour().forEach(armourPiece -> {
        final String armourName = armourPiece.getItemMeta().hasDisplayName() ?
            armourPiece.getItemMeta().getDisplayName() :
            armourPiece.getType().name();
        final String armourPath = basePath + "Armour." + armourName;

        config.set(armourPath, Helper.get().composeItemStack(armourPiece));
      });
    }

    try {
      config.save(kitsFile);
    } catch (IOException e) {
//      System.out.println("Failed to save kits: " + e.getMessage());
    }
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