package me.harsh.mbedwarskitsaddon.placeholders;

import de.marcely.bedwars.api.player.PlayerDataAPI;
import java.util.concurrent.atomic.AtomicReference;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
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
        final AtomicReference<String> atom = new AtomicReference<>();
        PlayerDataAPI.get().getProperties(player, playerProperties -> {
          atom.set(playerProperties.get(KitsUtil.KIT_CURRENT_PATH).orElse("Loading..."));
        });
        return atom.get();
    }

    return "";
  }
}
