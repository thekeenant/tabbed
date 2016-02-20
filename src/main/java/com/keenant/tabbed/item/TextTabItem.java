package com.keenant.tabbed.item;

import com.keenant.tabbed.util.Skin;
import com.keenant.tabbed.util.Skins;
import lombok.Getter;
import lombok.ToString;

import javax.annotation.Nonnull;

/**
 * A tab item with custom text, ping and skin.
 */
@ToString
public class TextTabItem implements TabItem {
    @Getter private String text;
    @Getter private int ping;
    @Getter private Skin skin;

    private String newText;
    private int newPing;
    private Skin newSkin;

    public TextTabItem(@Nonnull String text) {
        this(text, 1000);
    }

    public TextTabItem(@Nonnull String text, int ping) {
        this(text, ping, Skins.getDefaultSkin());
    }

    public TextTabItem(@Nonnull String text, int ping, @Nonnull Skin skin) {
        this.newText = text;
        this.newPing = ping;
        this.newSkin = skin;
        updateText();
        updatePing();
        updateSkin();
    }

    public void setText(@Nonnull String text) {
        this.newText = text;
    }

    public void setPing(int ping) {
        this.newPing = ping;
    }

    public void setSkin(@Nonnull Skin skin) {
        this.newSkin = skin;
    }

    @Override
    public boolean updateText() {
        boolean update = this.text == null || this.newText == null || !this.text.equals(this.newText);
        this.text = this.newText;
        return update;
    }

    @Override
    public boolean updatePing() {
        boolean update = this.ping != this.newPing;
        this.ping = newPing;
        return update;
    }

    @Override
    public boolean updateSkin() {
        boolean update = this.skin == null || this.newSkin == null || !this.skin.equals(this.newSkin);
        this.skin = newSkin;
        return update;
    }
}
