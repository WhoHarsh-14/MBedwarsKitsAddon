package me.harsh.mbedwarskitsaddon.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class KitPlaceholders extends PlaceholderExpansion {
  @Override
  public @NotNull String getIdentifier() {
    return "mbw_kit";
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
    String[] parameters = params.split("_");

    switch (parameters[0]) {
    }

    return "";
  }
}
