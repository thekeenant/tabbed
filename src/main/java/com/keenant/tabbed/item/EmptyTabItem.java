package com.keenant.tabbed.item;

import com.keenant.tabbed.util.Skins;

public class EmptyTabItem extends TextTabItem {
    public EmptyTabItem() {
        // 16 empty characters, makes the columns wide by default
        super("                ", 1000, Skins.getDefaultSkin());
    }
}
