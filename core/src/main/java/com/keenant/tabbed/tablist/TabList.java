package com.keenant.tabbed.tablist;

import org.bukkit.entity.Player;

/**
 * The highest level of a tab list.
 */
public interface TabList {
    Player getPlayer();

    /**
     * Enables the tab list, starts any necessary listeners/schedules.
     * @return The tab list.
     */
    TabList enable();

    /**
     * Disables the tab list: stops existing listeners/schedules.
     * @return The tab list.
     */
    TabList disable();
}
