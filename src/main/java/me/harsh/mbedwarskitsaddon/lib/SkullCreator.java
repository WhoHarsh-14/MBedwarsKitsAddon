package me.harsh.mbedwarskitsaddon.lib;

// Copyright (c) 2017 deanveloper (see LICENSE.md for more info)

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * A library for the Bukkit API to create player skulls
 * from names, base64 strings, and texture URLs.
 * <p>
 * Does not use any NMS code, and should work across all versions.
 *
 * @author deanveloper on 12/28/2016.
 */
public class SkullCreator {

  private SkullCreator() {}

  private static boolean warningPosted = false;

  // some reflection stuff to be used when setting a skull's profile
  private static Field blockProfileField;
  private static Method metaSetProfileMethod;
  private static Field metaProfileField;

  /**
   * Creates a player skull, should work in both legacy and new Bukkit APIs.
   */
  public static ItemStack createSkull() {
    checkLegacy();

    try {
      return new ItemStack(Material.valueOf("PLAYER_HEAD"));
    } catch (IllegalArgumentException e) {
      return new ItemStack(Material.valueOf("SKULL_ITEM"), 1, (byte) 3);
    }
  }

  /**
   * Creates a player skull item with the skin based on a player's name.
   *
   * @param name The Player's name.
   * @return The head of the Player.
   * @deprecated names don't make for good identifiers.
   */
  public static ItemStack itemFromName(String name) {
    return itemWithName(createSkull(), name);
  }

  /**
   * Creates a player skull item with the skin based on a player's UUID.
   *
   * @param id The Player's UUID.
   * @return The head of the Player.
   */
  public static ItemStack itemFromUuid(UUID id) {
    return itemWithUuid(createSkull(), id);
  }


  /**
   * Modifies a skull to use the skin of the player with a given name.
   *
   * @param item The item to apply the name to. Must be a player skull.
   * @param name The Player's name.
   * @return The head of the Player.
   * @deprecated names don't make for good identifiers.
   */
  @Deprecated
  public static ItemStack itemWithName(ItemStack item, String name) {
    notNull(item, "item");
    notNull(name, "name");

    SkullMeta meta = (SkullMeta) item.getItemMeta();
    meta.setOwner(name);
    item.setItemMeta(meta);

    return item;
  }

  /**
   * Modifies a skull to use the skin of the player with a given UUID.
   *
   * @param item The item to apply the name to. Must be a player skull.
   * @param id   The Player's UUID.
   * @return The head of the Player.
   */
  public static ItemStack itemWithUuid(ItemStack item, UUID id) {
    notNull(item, "item");
    notNull(id, "id");

    SkullMeta meta = (SkullMeta) item.getItemMeta();
    meta.setOwner(Bukkit.getOfflinePlayer(id).getName());
    item.setItemMeta(meta);

    return item;
  }

  /**
   * Sets the block to a skull with the given name.
   *
   * @param block The block to set.
   * @param name  The player to set it to.
   * @deprecated names don't make for good identifiers.
   */
  @Deprecated
  public static void blockWithName(Block block, String name) {
    notNull(block, "block");
    notNull(name, "name");

    Skull state = (Skull) block.getState();
    state.setOwner(name);
    state.update(false, false);
  }

  /**
   * Sets the block to a skull with the given UUID.
   *
   * @param block The block to set.
   * @param id    The player to set it to.
   */
  public static void blockWithUuid(Block block, UUID id) {
    notNull(block, "block");
    notNull(id, "id");

    setToSkull(block);
    Skull state = (Skull) block.getState();
    state.setOwner(Bukkit.getOfflinePlayer(id).getName());
    state.update(false, false);
  }


  private static void setToSkull(Block block) {
    checkLegacy();

    try {
      block.setType(Material.valueOf("PLAYER_HEAD"), false);
    } catch (IllegalArgumentException e) {
      block.setType(Material.valueOf("SKULL"), false);
      Skull state = (Skull) block.getState();
      state.setSkullType(SkullType.PLAYER);
      state.update(false, false);
    }
  }

  private static void notNull(Object o, String name) {
    if (o == null) {
      throw new NullPointerException(name + " should not be null!");
    }
  }


  // suppress warning since PLAYER_HEAD doesn't exist in 1.12.2,
  // but we expect this and catch the error at runtime.
  @SuppressWarnings("JavaReflectionMemberAccess")
  private static void checkLegacy() {
    try {
      // if both of these succeed, then we are running
      // in a legacy api, but on a modern (1.13+) server.
      Material.class.getDeclaredField("PLAYER_HEAD");
      Material.valueOf("SKULL");

      if (!warningPosted) {
        Bukkit.getLogger().warning("SKULLCREATOR API - Using the legacy bukkit API with 1.13+ bukkit versions is not supported!");
        warningPosted = true;
      }
    } catch (NoSuchFieldException | IllegalArgumentException ignored) {}
  }
}