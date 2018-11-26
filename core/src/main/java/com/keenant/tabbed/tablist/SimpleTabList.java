package com.keenant.tabbed.tablist;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.google.common.base.Preconditions;
import com.keenant.tabbed.Tabbed;
import com.keenant.tabbed.item.TabItem;
import com.keenant.tabbed.util.Packets;
import com.keenant.tabbed.util.Skin;
import com.keenant.tabbed.util.Skins;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;

/**
 * A simple implementation of a custom tab list that supports batch updates.
 */
@ToString(exclude = "tabbed")
public class SimpleTabList extends TitledTabList implements CustomTabList {
    public static int MAXIMUM_ITEMS = 4 * 20; // client maximum is 4x20 (4 columns, 20 rows)

    protected final Tabbed tabbed;
    protected final Map<Integer,TabItem> items;
    private final int maxItems;
    private final int minColumnWidth;
    private final int maxColumnWidth;

    @Getter boolean batchEnabled;
    private final Map<Integer,TabItem> clientItems;

    private static final Map<Skin, Map<Integer, WrappedGameProfile>> PROFILE_INDEX_CACHE = new HashMap<>();

    public SimpleTabList(Tabbed tabbed, Player player, int maxItems, int minColumnWidth, int maxColumnWidth) {
        super(player);
        Preconditions.checkArgument(maxItems <= MAXIMUM_ITEMS, "maxItems cannot exceed client maximum of " + MAXIMUM_ITEMS);
        Preconditions.checkArgument(minColumnWidth <= maxColumnWidth || maxColumnWidth < 0, "minColumnWidth cannot be greater than maxColumnWidth");

        this.tabbed = tabbed;
        this.maxItems = maxItems < 0 ? MAXIMUM_ITEMS : maxItems;
        this.minColumnWidth = minColumnWidth;
        this.maxColumnWidth = maxColumnWidth;
        this.clientItems = new HashMap<>();
        this.items = new HashMap<>();
    }

    public int getMaxItems() {
        return maxItems;
    }

    @Override
    public SimpleTabList enable() {
        super.enable();
        return this;
    }

    @Override
    public SimpleTabList disable() {
        super.disable();
        return this;
    }

    /**
     * Sends the batch update to the player and resets the batch.
     */
    public void batchUpdate() {
        update(this.clientItems, this.items, true);
        this.clientItems.clear();
        this.clientItems.putAll(this.items);
    }

    /**
     * Reset the existing batch.
     */
    public void batchReset() {
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
        if (item == null) {
            this.items.remove(index);
            return true;
        }
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

    private List<PacketContainer> getUpdate(Map<Integer,TabItem> oldItems, Map<Integer,TabItem> newItems) {
        List<PlayerInfoData> removePlayer = new ArrayList<>();
        List<PlayerInfoData> addPlayer = new ArrayList<>();
        List<PlayerInfoData> displayChanged = new ArrayList<>();
        List<PlayerInfoData> pingUpdated = new ArrayList<>();

        for (Entry<Integer, TabItem> entry : newItems.entrySet()) {
            int index = entry.getKey();
            TabItem oldItem = oldItems.get(index);
            TabItem newItem = entry.getValue();

            if (newItem == null && oldItem != null) { // TabItem has been removed.
                removePlayer.add(getPlayerInfoData(index, oldItem));
                continue;
            }

            boolean skinChanged = oldItem == null || newItem.updateSkin() || !newItem.getSkin().equals(oldItem.getSkin());
            boolean textChanged = oldItem == null || newItem.updateText() || !newItem.getText().equals(oldItem.getText());
            boolean pingChanged = oldItem == null || newItem.updatePing() || oldItem.getPing() != newItem.getPing();

            if (skinChanged) {
                if (oldItem != null)
                    removePlayer.add(getPlayerInfoData(index, oldItem));
                addPlayer.add(getPlayerInfoData(index, newItem));
            } else if (pingChanged) {
                pingUpdated.add(getPlayerInfoData(index, newItem));
            }

            if (textChanged)
                displayChanged.add(getPlayerInfoData(index, newItem));
        }

        List<PacketContainer> result = new ArrayList<>(4);

        if (removePlayer.size() > 0 || addPlayer.size() > 0) {
            result.add(Packets.getPacket(PlayerInfoAction.REMOVE_PLAYER, removePlayer));
            result.add(Packets.getPacket(PlayerInfoAction.ADD_PLAYER, addPlayer));
        }
        if (displayChanged.size() > 0)
            result.add(Packets.getPacket(PlayerInfoAction.UPDATE_DISPLAY_NAME, displayChanged));
        if (pingUpdated.size() > 0)
            result.add(Packets.getPacket(PlayerInfoAction.UPDATE_LATENCY, pingUpdated));

        return result;
    }

    private PlayerInfoData getPlayerInfoData(int index, TabItem item) {
        WrappedGameProfile profile = getGameProfile(index, item);
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

        return new PlayerInfoData(profile, ping, NativeGameMode.SURVIVAL, displayName == null ? null : WrappedChatComponent.fromText(displayName));
    }

    private WrappedGameProfile getGameProfile(int index, TabItem item) {
        Skin skin = item.getSkin();
        if (!PROFILE_INDEX_CACHE.containsKey(skin)) // Cached by skins, so if you change the skins a lot, it still works while being efficient.
            PROFILE_INDEX_CACHE.put(skin, new HashMap<>());
        Map<Integer, WrappedGameProfile> indexCache = PROFILE_INDEX_CACHE.get(skin);

        if (!indexCache.containsKey(index)) { // Profile is not cached, generate and cache one.
            String name = String.format("%03d", index) + "|UpdateMC"; // Starts with 00 so they are sorted in alphabetical order and appear in the right order.
            UUID uuid = UUID.nameUUIDFromBytes(name.getBytes());

            WrappedGameProfile profile = new WrappedGameProfile(uuid, name); // Create a profile to cache by skin and index.
            profile.getProperties().put(Skin.TEXTURE_KEY, item.getSkin().getProperty());
            indexCache.put(index, profile); // Cache the profile.
        }

        return indexCache.get(index);
    }
}
