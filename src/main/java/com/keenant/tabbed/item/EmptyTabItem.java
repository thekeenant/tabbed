package com.keenant.tabbed.item;

import com.keenant.tabbed.util.Skins;

public class EmptyTabItem extends TextTabItem {
    public EmptyTabItem(boolean wide) {
        // 16 empty characters, makes the column normal width
        super(wide ? "                " : "", 1000, Skins.getDefaultSkin());
    }
}
