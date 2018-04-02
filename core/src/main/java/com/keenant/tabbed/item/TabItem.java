package com.keenant.tabbed.item;

import com.keenant.tabbed.util.Skin;

/**
 * Represents a custom tab item.
 */
public interface TabItem {
    /**
     * The text of the tab item (any length, recommended less than ~18). No calculations should be made.
     * @return
     */
    String getText();

    /**
     * The ping of the tab item. No calculations should be made.
     * @return
     */
    int getPing();

    /**
     * The skin/avatar of the tab item. No calculations should be made.
     * @return
     */
    Skin getSkin();

    /**
     * Makes any appropriate changes to the text.
     * @return If a change has been made.
     */
    boolean updateText();

    /**
     * Makes any appropriate changes to the ping.
     * @return If a change has been made.
     */
    boolean updatePing();

    /**
     * Makes any appropriate changes to the skin.
     * @return If a change has been made.
     */
    boolean updateSkin();

    /**
     * Compare to another tab item.
     * @param object
     * @return
     */
    boolean equals(Object object);
}
