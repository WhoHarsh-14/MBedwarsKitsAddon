package me.harsh.mbedwarskitsaddon.menu;

import de.marcely.bedwars.api.message.Message;
import de.marcely.bedwars.tools.Helper;
import de.marcely.bedwars.tools.NMSHelper;
import de.marcely.bedwars.tools.gui.GUIItem;
import de.marcely.bedwars.tools.gui.type.ChestGUI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import me.harsh.mbedwarskitsaddon.utils.KitsUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KitCreateMenu extends ChestGUI {
  public KitCreateMenu(){
    super(6, KitsUtil.colorize("&a&lNEW KIT's ITEMS"));
  }


  @Override
  public void open(Player player) {
  }

  private GUIItem createItem(Player player, String materialName, Runnable onUse, Message name, Message lore) {
    ItemStack is = NMSHelper.get().hideAttributes(Helper.get().parseItemStack(materialName));

    final ItemMeta im = is.getItemMeta();
    final List<String> loreList = new ArrayList<>();

    loreList.add("");
    loreList.addAll(Arrays.stream(lore.done(player).split("\\\\n"))
        .map(l -> ChatColor.GRAY + l)
        .collect(Collectors.toList()));

    im.setDisplayName(KitsUtil.colorize(name.done(player, false)));
    im.setLore(loreList);
    is.setItemMeta(im);

    return new GUIItem(is, (g0, g1, g2) -> {
      onUse.run();
    });
  }

  private GUIItem getFillerGlass(){
    return createItem(null, "BLACK_STAINED_GLASS_PANE", () -> {}, Message.build(""), Message.build(""));
  }

}
