package com.keenant.tabbed.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Reflection util.
 */
public class Reflection {
    public static Object getHandle(Player player) {
        try {
            Class craftPlayer = Class.forName(Bukkit.getServer().getClass().getPackage().getName() + ".entity.CraftPlayer");
            return craftPlayer.cast(player).getClass().getMethod("getHandle").invoke(player);
        } catch (Exception e) {
            throw new RuntimeException("couldn't get CraftPlayer", e);
        }
    }
}
