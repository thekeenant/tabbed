package com.keenant.tabbed.item;

import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.keenant.tabbed.TabList;
import lombok.Getter;

public class TextTabItem implements TabItem {
    @Getter private final TabList tabList;
    @Getter private String text;
    @Getter private int ping;
    @Getter private WrappedSignedProperty skin;

    public TextTabItem(TabList tabList, String text, int ping, WrappedSignedProperty skin) {
        this.tabList = tabList;
        this.text = text;
        this.ping = ping;
        this.skin = skin;
    }
}
