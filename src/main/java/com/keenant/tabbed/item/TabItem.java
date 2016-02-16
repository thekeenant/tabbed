package com.keenant.tabbed.item;

import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.keenant.tabbed.TabList;

import java.util.UUID;

public interface TabItem {
    String getText();

    int getPing();

    WrappedSignedProperty getSkin();
}
