package com.keenant.tabbed.util;

import com.comphenix.protocol.ProtocolLibrary;
import org.bukkit.entity.Player;

public class Reflection {
    public static Object getHandle(Player player) {
        try {
            Class craftPlayer = Class.forName("org.bukkit.craftbukkit." + ProtocolLibrary.getProtocolManager().getMinecraftVersion().getVersion() + ".entity.CraftPlayer");
            return craftPlayer.cast(player).getClass().getMethod("getHandle").invoke(player);
        } catch (Exception e) {
            throw new RuntimeException("couldn't get CraftPlayer", e);
        }
    }
}
