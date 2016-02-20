package com.keenant.tabbed;

import com.keenant.tabbed.util.Skin;

public interface TabItem {
    /**
     * @return The text of the tab item (any length, recommended less than ~18).
     */
    String getText();

    /**
     * @return The ping of the tab item.
     */
    int getPing();

    /**
     * @return The skin/avatar of the tab item.
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
}
