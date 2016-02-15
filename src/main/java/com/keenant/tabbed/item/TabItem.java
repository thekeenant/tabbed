package com.keenant.tabbed.item;

import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.keenant.tabbed.TabList;

public interface TabItem {
    TabList getTabList();

    String getText();

    int getPing();

    WrappedSignedProperty getSkin();
}
