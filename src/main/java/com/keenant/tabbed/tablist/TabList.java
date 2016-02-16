package com.keenant.tabbed.tablist;

import lombok.Getter;
import org.bukkit.entity.Player;

public abstract class TabList {
    @Getter protected final Player player;

    public TabList(Player player) {
        this.player = player;
    }

    public abstract <T extends TabList> T enable();

    public abstract <T extends TabList> T disable();
}
