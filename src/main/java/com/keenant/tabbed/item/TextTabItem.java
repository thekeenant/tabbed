package com.keenant.tabbed.item;

import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.keenant.tabbed.util.Skins;
import lombok.Getter;

public class TextTabItem implements TabItem {
    @Getter private String text;
    @Getter private int ping;
    @Getter private WrappedSignedProperty skin;

    public TextTabItem(String text) {
        this(text, 1000);
    }

    public TextTabItem(String text, int ping) {
        this(text, ping, Skins.getDefaultSkin());
    }

    public TextTabItem(String text, int ping, WrappedSignedProperty skin) {
        this.text = text;
        this.ping = ping;
        this.skin = skin;
    }
}
