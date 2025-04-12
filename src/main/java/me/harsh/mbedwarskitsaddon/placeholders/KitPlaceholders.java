package me.harsh.mbedwarskitsaddon.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.harsh.mbedwarskitsaddon.kits.Kit;
import me.harsh.mbedwarskitsaddon.kits.KitManager;
import me.harsh.mbedwarskitsaddon.utils.KitsUtil;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class KitPlaceholders extends PlaceholderExpansion {
  @Override
  public @NotNull String getIdentifier() {
    return "mbw-kit";
  }

  @Override
  public @NotNull String getAuthor() {
    return "Harshu";
  }

  @Override
  public @NotNull String getVersion() {
    return "1.0.0";
  }

  @Override
  public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
    switch (params) {
      case "current":
        final Kit kit = KitManager.getInstance().getLoadedKits().get(KitManager.getInstance().getPlayerCurrentKits().get(player.getUniqueId()));
        if (kit == null)
          return "None";
        return KitsUtil.colorize(kit.getName());
    }

    return "";
  }
}
