package com.keenant.tabbed.item;

import com.keenant.tabbed.util.Skin;
import com.keenant.tabbed.util.Skins;
import lombok.Getter;
import lombok.ToString;

@ToString
public class TextTabItem implements TabItem {
    @Getter private final String text;
    @Getter private final int ping;
    @Getter private final Skin skin;

    public TextTabItem(String text) {
        this(text, 1000);
    }

    public TextTabItem(String text, int ping) {
        this(text, ping, Skins.getDefaultSkin());
    }

    public TextTabItem(String text, int ping, Skin skin) {
        this.text = text;
        this.ping = ping;
        this.skin = skin;
    }
}
