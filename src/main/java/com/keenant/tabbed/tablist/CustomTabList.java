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
import com.google.common.collect.ImmutableMap;
import com.keenant.tabbed.Tabbed;
import com.keenant.tabbed.TabbedPlugin;
import com.keenant.tabbed.item.TabItem;
import com.keenant.tabbed.util.Packets;
import lombok.ToString;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.Map.Entry;

@ToString(exclude = "tabbed")
public class CustomTabList extends TitledTabList {
    private final Tabbed tabbed;
    private final HashMap<Integer,TabItem> items;
    private final PacketListener packetListener;
    private final int maxItems;

    public CustomTabList(Tabbed tabbed, Player player, int maxItems) {
        super(player);
        this.tabbed = tabbed;
        this.maxItems = maxItems;

        this.items = new HashMap<>();
        this.packetListener = createPacketListener();
    }

    public CustomTabList(Tabbed tabbed, Player player) {
        // client maximum is 4x20 (4 columns, 20 rows)
        this(tabbed, player, 4 * 20);
    }

    public int getMaxItems() {
        return maxItems;
    }

    @Override
    public CustomTabList enable() {
        super.enable();
        registerListener();
        return this;
    }

    @Override
    public CustomTabList disable() {
        super.disable();
        unregisterListener();
        return this;
    }

    public void add(TabItem item) {
        add(getNextIndex(), item);
    }

    public void add(int index, TabItem item) {
        TabbedPlugin.log("adding @ " + index);
        update(index, item);
    }

    public void remove(int index) {
        update(index, null);
        this.items.remove(index);
    }

    public void remove(TabItem item) {
        Iterator<Entry<Integer,TabItem>> iterator = this.items.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<Integer,TabItem> entry = iterator.next();
            if (entry.getValue().equals(item))
                remove(entry.getKey());
        }
    }

    public boolean contains(int index) {
        return this.items.containsKey(index);
    }

    public TabItem get(int index) {
        return this.items.get(index);
    }

    protected void clear() {
        this.items.clear();
    }

    protected boolean put(int index, TabItem item) {
        if (index < 0 || index >= getMaxItems())
            return false;
        if (item == null)
            return true;
        this.items.put(index, item);
        return true;
    }

    protected Map<Integer,TabItem> putAll(Map<Integer,TabItem> items) {
        HashMap<Integer,TabItem> result = new HashMap<>(items.size());
        for (Entry<Integer,TabItem> entry : items.entrySet())
            if (put(entry.getKey(), entry.getValue()))
                result.put(entry.getKey(), entry.getValue());
        return result;
    }

    protected void update(int index, TabItem newItem) {
        update(ImmutableMap.of(index, newItem));
    }

    protected void update(Map<Integer,TabItem> items) {
        Map<Integer,TabItem> send = putAll(items);
        Packets.send(this.player, getUpdate(send));
    }

    private List<PacketContainer> getUpdate(int index, TabItem newItem) {
        TabItem oldItem = get(index);

        if (newItem == null && oldItem != null) {
            TabbedPlugin.log("Removing @ " + index);
            return Collections.singletonList(Packets.getPacket(PlayerInfoAction.REMOVE_PLAYER, getPlayerInfoData(index, oldItem)));
        }

        List<PacketContainer> packets = new ArrayList<>(2);

        boolean skinChanged = oldItem == null || !oldItem.getSkin().equals(newItem.getSkin());
        boolean textChanged = oldItem == null || !oldItem.getText().equals(newItem.getText());
        boolean pingChanged = oldItem == null || oldItem.getPing() != newItem.getPing();

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

    private List<PacketContainer> getUpdate(Map<Integer,TabItem> items) {
        List<PacketContainer> all = new ArrayList<>(items.size()  * 2);

        for (Entry<Integer,TabItem> entry : items.entrySet())
            all.addAll(getUpdate(entry.getKey(), entry.getValue()));

        List<PlayerInfoData> removePlayer = new ArrayList<>();
        List<PlayerInfoData> addPlayer = new ArrayList<>();
        List<PlayerInfoData> displayPlayer = new ArrayList<>();
        List<PlayerInfoData> pingChanged = new ArrayList<>();

        for (PacketContainer packet : all) {
            if (packet.getPlayerInfoAction().read(0) == PlayerInfoAction.REMOVE_PLAYER)
                removePlayer.addAll(packet.getPlayerInfoDataLists().read(0));
            if (packet.getPlayerInfoAction().read(0) == PlayerInfoAction.ADD_PLAYER)
                addPlayer.addAll(packet.getPlayerInfoDataLists().read(0));
            if (packet.getPlayerInfoAction().read(0) == PlayerInfoAction.UPDATE_DISPLAY_NAME)
                displayPlayer.addAll(packet.getPlayerInfoDataLists().read(0));
            if (packet.getPlayerInfoAction().read(0) == PlayerInfoAction.UPDATE_LATENCY)
                pingChanged.addAll(packet.getPlayerInfoDataLists().read(0));
        }

        List<PacketContainer> result = new ArrayList<>(4);

        if (removePlayer.size() > 0 || addPlayer.size() > 0) {
            result.add(Packets.getPacket(PlayerInfoAction.REMOVE_PLAYER, removePlayer));
            result.add(Packets.getPacket(PlayerInfoAction.ADD_PLAYER, addPlayer));
        }
        if (displayPlayer.size() > 0)
            result.add(Packets.getPacket(PlayerInfoAction.UPDATE_DISPLAY_NAME, displayPlayer));
        if (pingChanged.size() > 0)
            result.add(Packets.getPacket(PlayerInfoAction.UPDATE_LATENCY, pingChanged));

        return result;
    }

    private PlayerInfoData getPlayerInfoData(int index, TabItem item) {
        WrappedGameProfile profile = getGameProfile(index, item.getSkin());
        return getPlayerInfoData(profile, item.getPing(), item.getText());
    }

    private PlayerInfoData getPlayerInfoData(WrappedGameProfile profile, int ping, String displayName) {
        return new PlayerInfoData(profile, ping, NativeGameMode.NOT_SET, WrappedChatComponent.fromText(displayName));
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

    private int getNextIndex() {
        for (int index = 0; index < getMaxItems(); index++) {
            if (!contains(index))
                return index;
        }
        // tablist is full
        return -1;
    }
}
