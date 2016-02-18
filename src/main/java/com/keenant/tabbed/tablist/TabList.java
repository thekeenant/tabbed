package com.keenant.tabbed.tablist;

import lombok.Getter;
import org.bukkit.entity.Player;

public abstract class TabList {
    @Getter protected final Player player;

    public TabList(Player player) {
        this.player = player;
    }

    /**
     * Enables the tab list, starts any necessary listeners/schedules.
     * @param <T>
     * @return
     */
    public abstract <T extends TabList> T enable();

    /**
     * Disables the tab list: stops existing listeners/schedules.
     * @param <T>
     * @return
     */
    public abstract <T extends TabList> T disable();
}
