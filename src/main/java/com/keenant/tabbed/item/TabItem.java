package com.keenant.tabbed.item;

import com.keenant.tabbed.util.Skin;

public interface TabItem {
    String getText();

    int getPing();

    Skin getSkin();

    boolean updateText();

    boolean updatePing();

    boolean updateSkin();
}
