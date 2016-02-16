package com.keenant.tabbed;

import com.google.common.base.Preconditions;
import com.keenant.tabbed.tablist.CustomTabList;
import com.keenant.tabbed.tablist.TabList;
import com.keenant.tabbed.tablist.TitledTabList;
import com.keenant.tabbed.tablist.table.TableTabList;
import lombok.Getter;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;

public class Tabbed {
    @Getter private final Plugin plugin;
    private final HashMap<Player,TabList> tabLists;

    public Tabbed(Plugin plugin) {
        this.plugin = plugin;
        this.tabLists = new HashMap<>();
    }

    @Nullable
    public TabList getTabList(Player player) {
        return this.tabLists.get(player);
    }

    private <T extends TabList> T put(Player player, T tabList) {
        Preconditions.checkArgument(!this.tabLists.containsKey(player), "player '" + player.getName() + "' already has a tablist");
        this.tabLists.put(player, tabList);
        return tabList;
    }

    public TitledTabList newTitledTabList(Player player) {
        return put(player, new TitledTabList(player).enable());
    }

    public CustomTabList newCustomTabList(Player player) {
        return put(player, new CustomTabList(this, player).enable());
    }

    public TableTabList newTableTabList(Player player, int columns) {
        return put(player, new TableTabList(this, player, columns).enable());
    }
}
