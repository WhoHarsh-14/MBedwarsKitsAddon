package me.harsh.mbedwarskitsaddon.kits;

import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import me.harsh.mbedwarskitsaddon.config.KitConfig;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
public class Kit {
  private final String id;
  private String name;
  private String permission;
  private ItemStack icon;
  private Map<Integer, ItemStack> items;
  // Armour in extra set because kits can be edited. Makes easier to track them this way.
  private Set<ItemStack> armour;
  private int price;

  public Kit(String id, String name, ItemStack icon, Map<Integer,ItemStack> items, Set<ItemStack> armour){
    this.id = id;
    this.items = items;
    this.icon = icon;
    this.name = name;
    this.armour = armour;
    this.permission = "kitsaddon." + id;
    this.price = KitConfig.KIT_DEFAULT_COINS;
  }

  public Kit(String id, String name, ItemStack icon, Map<Integer,ItemStack> items, Set<ItemStack> armour, int price){
    this.id = id;
    this.items = items;
    this.icon = icon;
    this.name = name;
    this.armour = armour;
    this.price = price;
    this.permission = "kitsaddon." + id;
  }


  public void addItem(int index, ItemStack itemStack){
    this.items.put(index, itemStack);
  }
  public void addArmour(ItemStack armour){
    this.armour.add(armour);
  }

  @Override
  public boolean equals(Object obj) {
    final Kit kit = (Kit) obj;
    return kit.getId().equalsIgnoreCase(this.id);
  }

}
