package com.keenant.tabbed;

import com.google.common.base.Preconditions;
import com.keenant.tabbed.tablist.*;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class Tabbed implements Listener {
    private static Map<Plugin,Tabbed> instances = new HashMap<>();
    @Getter @Setter static Level logLevel = Level.WARNING;

    @Getter private final Plugin plugin;
    private final Map<Player,TabList> tabLists;

    public Tabbed(Plugin plugin) {
        this.plugin = plugin;
        this.tabLists = new HashMap<>();
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
        instances.put(plugin, this);
    }

    public static void log(Level level, String message) {
        if (level.intValue() >= logLevel.intValue())
            System.out.println("[" + level.getName() + "] " + message);
    }

    /**
     * Gets an instance of Tabbed from a plugin.
     * @param plugin
     * @return
     */
    public static Tabbed getTabbed(Plugin plugin) {
        return instances.get(plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        destroyTabList(event.getPlayer());
    }

    /**
     * Get the current tab list of the player.
     * @param player
     * @return The tab list, or null if it wasn't present.
     */
    public TabList getTabList(Player player) {
        return this.tabLists.get(player);
    }

    /**
     * Disables the tab list of a player.
     * @param player
     * @return The tab list removed (or null if it wasn't present).
     */
    public TabList destroyTabList(Player player) {
        TabList tabList = getTabList(player);
        if (tabList == null)
            return null;
        this.tabLists.remove(player);
        return tabList.disable();
    }

    /**
     * Disables a tab list.
     * @param tabList
     * @return The tab list removed.
     */
    public TabList destroyTabList(TabList tabList) {
        return destroyTabList(tabList.getPlayer());
    }

    /**
     * Creates a new TitledTabList with the given parameters.
     * @param player
     * @return
     */
    public TitledTabList newTitledTabList(Player player) {
        return put(player, new TitledTabList(player).enable());
    }

    /**
     * Creates a new DefaultTabList.
     * @param player
     * @return
     */
    public DefaultTabList newDefaultTabList(Player player) {
        return put(player, new DefaultTabList(this, player, -1).enable());
    }

    /**
     * Creates a new CustomTabList with the given parameters.
     * @param player
     * @return
     */
    public SimpleTabList newSimpleTabList(Player player) {
        return newSimpleTabList(player, SimpleTabList.MAXIMUM_ITEMS);
    }

    /**
     * Creates a new CustomTabList with the given parameters.
     * @param player
     * @param maxItems
     * @return
     */
    public SimpleTabList newSimpleTabList(Player player, int maxItems) {
        return newSimpleTabList(player, maxItems, -1);
    }

    /**
     * Creates a new CustomTabList with the given parameters.
     * @param player
     * @param maxItems
     * @param minColumnWidth
     * @return
     */
    public SimpleTabList newSimpleTabList(Player player, int maxItems, int minColumnWidth) {
        return newSimpleTabList(player, maxItems, minColumnWidth, -1);
    }

    /**
     * Creates a new CustomTabList with the given parameters.
     * @param player
     * @param maxItems
     * @param minColumnWidth
     * @param maxColumnWidth
     * @return
     */
    public SimpleTabList newSimpleTabList(Player player, int maxItems, int minColumnWidth, int maxColumnWidth) {
        return put(player, new SimpleTabList(this, player, maxItems, minColumnWidth, maxColumnWidth).enable());
    }

    /**
     * Creates a new TableTabList with the given parameters.
     * @param player
     * @return
     */
    public TableTabList newTableTabList(Player player) {
        return newTableTabList(player, 4);
    }

    /**
     * Creates a new TableTabList with the given parameters.
     * @param player
     * @param columns
     * @return
     */
    public TableTabList newTableTabList(Player player, int columns) {
        return newTableTabList(player, columns, -1);
    }

    /**
     * Creates a new TableTabList with the given parameters.
     * @param player
     * @param columns
     * @param minColumnWidth
     * @return
     */
    public TableTabList newTableTabList(Player player, int columns, int minColumnWidth) {
        return newTableTabList(player, columns, minColumnWidth, -1);
    }

    /**
     * Creates a new TableTabList with the given parameters.
     * @param player
     * @param columns
     * @param minColumnWidth
     * @param maxColumnWidth
     * @return
     */
    public TableTabList newTableTabList(Player player, int columns, int minColumnWidth, int maxColumnWidth) {
        return put(player, new TableTabList(this, player, columns, minColumnWidth, maxColumnWidth).enable());
    }

    private <T extends TabList> T put(Player player, T tabList) {
        Preconditions.checkArgument(!this.tabLists.containsKey(player), "player '" + player.getName() + "' already has a tablist");
        this.tabLists.put(player, tabList);
        return tabList;
    }
}
