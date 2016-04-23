package com.keenant.tabbed.util;

import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;

/**
 * Some generic-ish packet utils.
 */
public class Packets {
    /**
     * Creates a PLAYER_INFO packet from the params.
     * @param action
     * @param data
     * @return
     */
    public static PacketContainer getPacket(PlayerInfoAction action, PlayerInfoData data) {
        return getPacket(action, Collections.singletonList(data));
    }
    /**
     * Creates a PLAYER_INFO packet from the params.
     * @param action
     * @param data
     * @return
     */

    public static PacketContainer getPacket(PlayerInfoAction action, List<PlayerInfoData> data) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(Server.PLAYER_INFO);
        packet.getPlayerInfoAction().write(0, action);
        packet.getPlayerInfoDataLists().write(0, data);
        return packet;
    }

    /**
     * Sends a list of ProtocolLib packets to a player.
     * @param player
     * @param packets
     * @return
     */
    public static void send(Player player, List<PacketContainer> packets) {
        try {
            for (PacketContainer packet : packets)
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet, false);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
