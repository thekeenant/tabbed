package com.keenant.tabbed;

import com.google.common.base.Preconditions;
import com.keenant.tabbed.tablist.DefaultTabList;
import com.keenant.tabbed.tablist.SimpleTabList;
import com.keenant.tabbed.tablist.TabList;
import com.keenant.tabbed.tablist.TitledTabList;
import com.keenant.tabbed.tablist.table.TableTabList;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;

public class Tabbed implements Listener {
    @Getter private final Plugin plugin;
    private final HashMap<Player,TabList> tabLists;

    public Tabbed(Plugin plugin) {
        this.plugin = plugin;
        this.tabLists = new HashMap<>();
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        TabList tabList = tabLists.remove(event.getPlayer());
        if (tabList != null)
            tabList.disable();
    }

    @Nullable
    public TabList getTabList(Player player) {
        return this.tabLists.get(player);
    }

    @Nullable
    public TabList destroyTabList(Player player) {
        TabList tabList = getTabList(player);
        if (tabList == null)
            return null;
        return tabList.disable();
    }

    @Nullable
    public TabList destroyTabList(@Nonnull TabList tabList) {
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
