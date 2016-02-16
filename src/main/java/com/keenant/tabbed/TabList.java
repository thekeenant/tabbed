package com.keenant.tabbed;

import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.google.common.base.Preconditions;
import com.keenant.tabbed.item.EmptyTabItem;
import com.keenant.tabbed.item.TabItem;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.Map.Entry;

@ToString
public class TabList {
    @Getter private final Player player;
    @Getter private String header;
    @Getter private String footer;

    @Getter private final Map<TabCell,TabItem> items; // items currently displayed to the client
    @Getter private final int columns;
    @Getter private final int rows;

    public TabList(@Nonnull Player player, int columns) {
        this.player = player;
        this.items = new HashMap<>();
        this.columns = columns;
        this.rows = getMinRows(columns);
        reset();
    }

    public void set(int column, int row, TabItem item) {
        set(new TabCell(column, row), item);
    }

    public void set(TabCell cell, TabItem item) {
        validateCell(cell);
        update(cell, item);
    }

    public void batchSet(Map<TabCell,TabItem> items) {
        List<PacketContainer> packets = new ArrayList<>();
        for (Entry<TabCell,TabItem> entry : items.entrySet()) {
            validateCell(entry.getKey());
            packets.addAll(getUpdate(entry.getKey(), entry.getValue()));
            this.items.put(entry.getKey(), entry.getValue());
        }
        send(packets);
    }

    private void update(TabCell cell, TabItem newItem) {
        send(getUpdate(cell, newItem));
        this.items.put(cell, newItem);
    }

    private List<PacketContainer> getUpdate(TabCell cell, TabItem newItem) {
        TabItem oldItem = this.items.get(cell);

        boolean skinChanged = !oldItem.getSkin().equals(newItem.getSkin());
        boolean textChanged = !oldItem.getText().equals(newItem.getText());

        List<PacketContainer> packets = new ArrayList<>();

        if (skinChanged) {
            packets.add(getPacket(PlayerInfoAction.REMOVE_PLAYER, getPlayerInfoData(cell, oldItem)));
            packets.add(getPacket(PlayerInfoAction.ADD_PLAYER, getPlayerInfoData(cell, newItem)));
        }
        else if (textChanged) {
            packets.add(getPacket(PlayerInfoAction.UPDATE_DISPLAY_NAME, getPlayerInfoData(cell, newItem)));
        }

        return packets;
    }

    private void reset() {
        PlayerInfoData[] data = new PlayerInfoData[this.columns * this.rows];
        for (int x = 0; x < this.columns; x++) {
            for (int y = 0; y < this.rows; y++) {
                TabCell cell = new TabCell(x, y);
                TabItem item = new EmptyTabItem();

                if (this.items.containsKey(cell)) {
                    PlayerInfoData remove = getPlayerInfoData(cell, this.items.get(cell));
                    send(getPacket(PlayerInfoAction.REMOVE_PLAYER, remove));
                }

                int index = getIndex(cell);
                data[index] = getPlayerInfoData(cell, item);
                this.items.put(cell, item);
            }
        }
        send(getPacket(PlayerInfoAction.ADD_PLAYER, Arrays.asList(data)));
    }

    private PacketContainer getPacket(PlayerInfoAction action, PlayerInfoData data) {
        return getPacket(action, Collections.singletonList(data));
    }

    private PacketContainer getPacket(PlayerInfoAction action, List<PlayerInfoData> data) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(Server.PLAYER_INFO);
        packet.getPlayerInfoAction().write(0, action);
        packet.getPlayerInfoDataLists().write(0, data);
        return packet;
    }

    private void send(PacketContainer packet) {
        send(Collections.singletonList(packet));
    }

    private void send(List<PacketContainer> packets) {
        try {
            for (PacketContainer packet : packets) {
                ProtocolLibrary.getProtocolManager().sendServerPacket(this.player, packet);
            }
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private static int getMinRows(int columns) {
        if (columns == 1)
            return 1;
        else if (columns == 2)
            return 11;
        else if (columns == 3)
            return 14;
        else
            throw new RuntimeException("invalid column count " + columns);
    }

    public PlayerInfoData getPlayerInfoData(TabCell cell, TabItem item) {
        WrappedGameProfile profile = getGameProfile(cell, item.getSkin());
        return getPlayerInfoData(profile, item.getPing(), item.getText());
    }

    private PlayerInfoData getPlayerInfoData(WrappedGameProfile profile, int ping, String displayName) {
        return new PlayerInfoData(profile, ping, NativeGameMode.NOT_SET, WrappedChatComponent.fromText(displayName));
    }

    private WrappedGameProfile getGameProfile(TabCell cell, WrappedSignedProperty skin) {
        String index = getStringIndex(cell);
        WrappedGameProfile profile = new WrappedGameProfile(UUID.nameUUIDFromBytes(index.getBytes()), "$" + index);
        profile.getProperties().put("textures", skin);
        return profile;
    }

    private String getStringIndex(TabCell cell) {
        return String.format("%03d", getIndex(cell));
    }

    private int getIndex(TabCell cell) {
        return cell.getRow() + this.rows * cell.getColumn();
    }

    private void validateCell(TabCell cell) {
        Preconditions.checkArgument(cell.getRow() >= 0 && cell.getRow() < this.rows, "row not in allowed range");
        Preconditions.checkArgument(cell.getColumn() >= 0 && cell.getColumn() < this.columns, "column not in allowed range");
    }

    public void setHeaderFooter(String header, String footer) {
        setHeader(header);
        setFooter(footer);
    }

    public void resetHeaderFooter() {
        resetHeader();
        resetFooter();
    }

    public void setHeader(String header) {
        this.header = header;
        updateHeaderFooter();
    }

    public void resetHeader() {
        setHeader(null);
    }

    public void setFooter(String footer) {
        this.footer = footer;
        updateHeaderFooter();
    }

    public void resetFooter() {
        setFooter(null);
    }

    private void updateHeaderFooter() {
        PacketContainer packet = new PacketContainer(Server.PLAYER_LIST_HEADER_FOOTER);
        packet.getChatComponents().write(0, WrappedChatComponent.fromText(this.header == null ? "" : this.header));
        packet.getChatComponents().write(1, WrappedChatComponent.fromText(this.footer == null ? "" : this.footer));
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(this.player, packet);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
