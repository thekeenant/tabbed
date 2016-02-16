package com.keenant.tabbed.item;

import com.keenant.tabbed.util.Skins;
import lombok.ToString;

@ToString
public class BlankTabItem extends TextTabItem {
    public BlankTabItem() {
        super("", 1000, Skins.getDefaultSkin());
    }
}
