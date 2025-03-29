package me.harsh.mbedwarskitsaddon.placeholders;

import de.marcely.bedwars.api.player.PlayerDataAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
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
        PlayerDataAPI.get().getProperties(player, playerProperties -> {
          playerProperties.get("kits.selected");
        });
    }

    return "";
  }
}
