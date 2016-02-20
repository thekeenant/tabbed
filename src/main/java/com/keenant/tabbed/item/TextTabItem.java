package com.keenant.tabbed.item;

import com.keenant.tabbed.util.Skin;
import com.keenant.tabbed.util.Skins;
import lombok.Getter;
import lombok.ToString;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.Objects;

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

    public TextTabItem(@Nonnull String text, @Nonnegative int ping) {
        this(text, ping, Skins.DEFAULT_SKIN);
    }

    public TextTabItem(@Nonnull String text, @Nonnegative int ping, @Nonnull Skin skin) {
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

    public void setPing(@Nonnegative int ping) {
        this.newPing = ping;
    }

    public void setSkin(@Nonnull Skin skin) {
        this.newSkin = skin;
    }

    @Override
    public boolean updateText() {
        boolean update = !Objects.equals(this.text, this.newText);
        this.text = this.newText;
        return update;
    }

    @Override
    public boolean updatePing() {
        boolean update = this.ping != this.newPing;
        this.ping = this.newPing;
        return update;
    }

    @Override
    public boolean updateSkin() {
        boolean update = !Objects.equals(this.skin, this.newSkin);
        this.skin = this.newSkin;
        return update;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TextTabItem))
            return false;
        TextTabItem other = (TextTabItem) object;
        return this.text.equals(other.getText()) && this.skin.equals(other.getSkin()) && this.ping == other.getPing();
    }
}
