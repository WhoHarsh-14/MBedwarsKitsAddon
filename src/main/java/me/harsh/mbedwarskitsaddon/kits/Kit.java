package me.harsh.mbedwarskitsaddon.kits;

import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@Getter
@Setter
public class Kit {
  private final String id;
  private String name;
  private ItemStack icon;
  private Set<ItemStack> items;

  public Kit(String id, String name, ItemStack icon, Set<ItemStack> items){
    this.id = id;
    this.items = items;
    this.icon = icon;
    this.name = name;
  }

  public void addItem(ItemStack itemStack){
    this.items.add(itemStack);
  }

  @Override
  public boolean equals(Object obj) {
    final Kit kit = (Kit) obj;
    return kit.getId().equalsIgnoreCase(this.id);
  }

}
