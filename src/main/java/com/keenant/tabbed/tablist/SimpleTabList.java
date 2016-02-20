package com.keenant.tabbed.tablist;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.*;
import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.google.common.base.Preconditions;
import com.keenant.tabbed.item.TabItem;
import com.keenant.tabbed.Tabbed;
import com.keenant.tabbed.util.Packets;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.Map.Entry;

/**
 * A simple implementation of a custom tab list that supports batch updates.
 */
@ToString(exclude = "tabbed")
public class SimpleTabList extends TitledTabList implements CustomTabList {
    public static int MAXIMUM_ITEMS = 4 * 20; // client maximum is 4x20 (4 columns, 20 rows)

    protected final Tabbed tabbed;
    protected final Map<Integer,TabItem> items;
    private final PacketListener packetListener;
    private final int maxItems;
    private final int minColumnWidth;
    private final int maxColumnWidth;

    private int updaterId;
    private final Runnable updater;

    @Getter boolean batchEnabled;
    private final Map<Integer,TabItem> clientItems;

    public SimpleTabList(Tabbed tabbed, Player player, int maxItems, int minColumnWidth, int maxColumnWidth) {
        super(player);
        Preconditions.checkArgument(maxItems <= MAXIMUM_ITEMS, "maxItems cannot exceed client maximum of " + MAXIMUM_ITEMS);
        Preconditions.checkArgument(minColumnWidth <= maxColumnWidth || maxColumnWidth < 0, "minColumnWidth cannot be greater than maxColumnWidth");

        this.tabbed = tabbed;
        this.maxItems = maxItems < 0 ? MAXIMUM_ITEMS : maxItems;
        this.minColumnWidth = minColumnWidth;
        this.maxColumnWidth = maxColumnWidth;
        this.clientItems = new HashMap<>();
        this.updater = new Runnable() {
            @Override
            public void run() {
                update(items, items);
            }
        };

        this.items = new HashMap<>();
        this.packetListener = createPacketListener();
    }

    public int getMaxItems() {
        return maxItems;
    }

    @Override
    public SimpleTabList enable() {
        List<PacketContainer> packets = new ArrayList<>();
        for (Player target : Bukkit.getOnlinePlayers())
            packets.add(Packets.getPacket(PlayerInfoAction.REMOVE_PLAYER, getPlayerInfoData(WrappedGameProfile.fromPlayer(target), 0, null)));
        Packets.send(this.player, packets);

        super.enable();
        registerListener();
        this.updaterId = this.tabbed.getPlugin().getServer().getScheduler().scheduleSyncRepeatingTask(this.tabbed.getPlugin(), this.updater, 0, 20);
        return this;
    }

    @Override
    public SimpleTabList disable() {
        super.disable();
        unregisterListener();
        this.tabbed.getPlugin().getServer().getScheduler().cancelTask(updaterId);
        return this;
    }

    /**
     * Sends the batch update to the player and resets the batch.
     */
    public void batchUpdate() {
        if (!this.batchEnabled)
            throw new UnsupportedOperationException("cannot batch update when batch is not enabled, call setBachEnabled(true) first");
        update(this.clientItems, this.items, true);
        batchReset();
    }

    /**
     * Reset the existing batch.
     */
    public void batchReset() {
        if (!this.batchEnabled)
            throw new UnsupportedOperationException("cannot reset batch when batch is not enabled, call setBachEnabled(true) first");
        this.items.clear();
        this.items.putAll(this.clientItems);
    }

    /**
     * Enable batch processing of tab items. Modifications to the tab list
     * will not be sent to the client until {@link #batchUpdate()} is called.
     * @param batchEnabled
     */
    public void setBatchEnabled(boolean batchEnabled) {
        if (this.batchEnabled == batchEnabled)
            return;
        if (this.batchEnabled && !this.clientItems.equals(this.items))
            throw new RuntimeException("cannot disable batch before batchUpdate() called");
        this.batchEnabled = batchEnabled;
        this.clientItems.clear();

        if (this.batchEnabled)
            this.clientItems.putAll(this.items);
    }

    public void add(TabItem item) {
        set(getNextIndex(), item);
    }

    public void add(int index, TabItem item) {
        validateIndex(index);
        Map<Integer,TabItem> current = new HashMap<>();
        current.putAll(this.items);

        Map<Integer,TabItem> map = new HashMap<>();
        for (int i = index; i < getMaxItems(); i++) {
            if (!contains(i))
                break;
            TabItem move = get(i);
            map.put(i + 1, move);
        }
        map.put(index, item);
        update(current, map);
    }

    public TabItem set(int index, TabItem item) {
        Map<Integer,TabItem> items = new HashMap<>(1);
        items.put(index, item);
        return set(items).get(index);
    }

    public Map<Integer,TabItem> set(Map<Integer,TabItem> items) {
        for (Entry<Integer,TabItem> entry : items.entrySet())
            validateIndex(entry.getKey());

        Map<Integer,TabItem> oldItems = new HashMap<>();
        oldItems.putAll(this.items);
        update(oldItems, items);

        return oldItems;
    }

    public TabItem remove(int index) {
        validateIndex(index);
        TabItem removed = this.items.remove(index);
        update(index, removed, null);
        return removed;
    }

    public <T extends TabItem> T remove(T item) {
        Iterator<Entry<Integer,TabItem>> iterator = this.items.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<Integer,TabItem> entry = iterator.next();
            if (entry.getValue().equals(item))
                remove(entry.getKey());
        }
        return item;
    }

    public boolean contains(int index) {
        validateIndex(index);
        return this.items.containsKey(index);
    }

    public TabItem get(int index) {
        validateIndex(index);
        return this.items.get(index);
    }

    public void update() {
        update(this.items, this.items);
    }

    public void update(int index) {
        Map<Integer,TabItem> map = new HashMap<>();
        map.put(index, get(index));
        update(index, get(index), get(index));
    }

    public int getNextIndex() {
        for (int index = 0; index < getMaxItems(); index++) {
            if (!contains(index))
                return index;
        }
        // tablist is full
        return -1;
    }

    protected void update(int index, TabItem oldItem, TabItem newItem) {
        Map<Integer,TabItem> oldItems = new HashMap<>(1);
        oldItems.put(index, oldItem);

        Map<Integer,TabItem> newItems = new HashMap<>(1);
        newItems.put(index, newItem);

        update(oldItems, newItems);
    }

    protected void update(Map<Integer,TabItem> oldItems, Map<Integer,TabItem> items) {
        update(oldItems, items, false);
    }

    private void validateIndex(int index) {
        Preconditions.checkArgument(index > 0 || index < getMaxItems(), "index not in allowed range");
    }

    private boolean put(int index, TabItem item) {
        if (index < 0 || index >= getMaxItems())
            return false;
        if (item == null)
            return true;
        this.items.put(index, item);
        return true;
    }

    private Map<Integer,TabItem> putAll(Map<Integer,TabItem> items) {
        HashMap<Integer,TabItem> result = new HashMap<>(items.size());
        for (Entry<Integer,TabItem> entry : items.entrySet())
            if (put(entry.getKey(), entry.getValue()))
                result.put(entry.getKey(), entry.getValue());
        return result;
    }

    private void update(Map<Integer,TabItem> oldItems, Map<Integer,TabItem> items, boolean isBatch) {
        if (this.batchEnabled && !isBatch) {
            this.items.putAll(items);
            return;
        }

        Map<Integer,TabItem> newItems = putAll(items);
        Packets.send(this.player, getUpdate(oldItems, newItems));
    }

    private List<PacketContainer> getUpdate(int index, TabItem oldItem, TabItem newItem) {
        if (newItem == null && oldItem != null)
            return Collections.singletonList(Packets.getPacket(PlayerInfoAction.REMOVE_PLAYER, getPlayerInfoData(index, oldItem)));

        List<PacketContainer> packets = new ArrayList<>(2);

        boolean skinChanged = oldItem == null || !oldItem.equals(newItem) || newItem.updateSkin();
        boolean textChanged = oldItem == null || newItem.updateText();
        boolean pingChanged = oldItem == null || newItem.updatePing();

        if (skinChanged) {
            if (oldItem != null)
                packets.add(Packets.getPacket(PlayerInfoAction.REMOVE_PLAYER, getPlayerInfoData(index, oldItem)));
            packets.add(Packets.getPacket(PlayerInfoAction.ADD_PLAYER, getPlayerInfoData(index, newItem)));
        }
        else {
            if (textChanged)
                packets.add(Packets.getPacket(PlayerInfoAction.UPDATE_DISPLAY_NAME, getPlayerInfoData(index, newItem)));
            if (pingChanged)
                packets.add(Packets.getPacket(PlayerInfoAction.UPDATE_LATENCY, getPlayerInfoData(index, newItem)));
        }

        return packets;
    }

    private List<PacketContainer> getUpdate(Map<Integer,TabItem> oldItems, Map<Integer,TabItem> items) {
        List<PacketContainer> all = new ArrayList<>(items.size()  * 2);

        for (Entry<Integer,TabItem> entry : items.entrySet())
            all.addAll(getUpdate(entry.getKey(), oldItems.get(entry.getKey()), entry.getValue()));

        List<PlayerInfoData> removePlayer = new ArrayList<>();
        List<PlayerInfoData> addPlayer = new ArrayList<>();
        List<PlayerInfoData> displayChanged = new ArrayList<>();
        List<PlayerInfoData> pingChanged = new ArrayList<>();

        for (PacketContainer packet : all) {
            if (packet.getPlayerInfoAction().read(0) == PlayerInfoAction.REMOVE_PLAYER)
                removePlayer.addAll(packet.getPlayerInfoDataLists().read(0));
            if (packet.getPlayerInfoAction().read(0) == PlayerInfoAction.ADD_PLAYER)
                addPlayer.addAll(packet.getPlayerInfoDataLists().read(0));
            if (packet.getPlayerInfoAction().read(0) == PlayerInfoAction.UPDATE_DISPLAY_NAME)
                displayChanged.addAll(packet.getPlayerInfoDataLists().read(0));
            if (packet.getPlayerInfoAction().read(0) == PlayerInfoAction.UPDATE_LATENCY)
                pingChanged.addAll(packet.getPlayerInfoDataLists().read(0));
        }

        List<PacketContainer> result = new ArrayList<>(4);

        if (removePlayer.size() > 0 || addPlayer.size() > 0) {
            result.add(Packets.getPacket(PlayerInfoAction.REMOVE_PLAYER, removePlayer));
            result.add(Packets.getPacket(PlayerInfoAction.ADD_PLAYER, addPlayer));
        }
        if (displayChanged.size() > 0)
            result.add(Packets.getPacket(PlayerInfoAction.UPDATE_DISPLAY_NAME, displayChanged));
        if (pingChanged.size() > 0)
            result.add(Packets.getPacket(PlayerInfoAction.UPDATE_LATENCY, pingChanged));


        return result;
    }

    private PlayerInfoData getPlayerInfoData(int index, TabItem item) {
        WrappedGameProfile profile = getGameProfile(index, item.getSkin().getProperty());
        return getPlayerInfoData(profile, item.getPing(), item.getText());
    }

    private PlayerInfoData getPlayerInfoData(WrappedGameProfile profile, int ping, String displayName) {
        if (displayName != null) {
            // min width
            while (displayName.length() < this.minColumnWidth)
                displayName += " ";

            // max width
            if (this.maxColumnWidth > 0)
                while (displayName.length() > this.maxColumnWidth)
                    displayName = displayName.substring(0, displayName.length() - 1);
        }

        return new PlayerInfoData(profile, ping, NativeGameMode.NOT_SET, displayName == null ? null : WrappedChatComponent.fromText(displayName));
    }

    private WrappedGameProfile getGameProfile(int index, WrappedSignedProperty skin) {
        String stringIndex = getStringIndex(index);
        WrappedGameProfile profile = new WrappedGameProfile(UUID.nameUUIDFromBytes(stringIndex.getBytes()), "$" + stringIndex);
        profile.getProperties().put("textures", skin);
        return profile;
    }

    private String getStringIndex(int index) {
        return String.format("%03d", index);
    }

    private void registerListener() {
        ProtocolLibrary.getProtocolManager().addPacketListener(this.packetListener);
    }

    private void unregisterListener() {
        ProtocolLibrary.getProtocolManager().removePacketListener(this.packetListener);
    }

    private PacketAdapter createPacketListener() {
        return new PacketAdapter(this.tabbed.getPlugin(), ListenerPriority.NORMAL, PacketType.Play.Server.PLAYER_INFO) {
            public void onPacketSending(PacketEvent event) {
                if (!event.getPlayer().equals(player))
                    return;

                PacketContainer packet = event.getPacket();
                List<PlayerInfoData> playerData = packet.getPlayerInfoDataLists().read(0);
                List<PlayerInfoData> newData = new ArrayList<>();

                for (PlayerInfoData data : playerData) {
                    if (data.getProfile().getName().startsWith("$")) {
                        String corrected = data.getProfile().getName().substring(1);
                        WrappedGameProfile profile = new WrappedGameProfile(data.getProfile().getUUID(), corrected);
                        profile.getProperties().putAll(data.getProfile().getProperties());

                        PlayerInfoData replaced = new PlayerInfoData(profile, data.getPing(), data.getGameMode(), data.getDisplayName());
                        newData.add(replaced);
                    }
                }

                if (newData.size() == 0)
                    event.setCancelled(true);
                else
                    packet.getPlayerInfoDataLists().write(0, newData);
            }
        };
    }
}
