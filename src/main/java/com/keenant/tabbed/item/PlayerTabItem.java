package com.keenant.tabbed.item;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.keenant.tabbed.TabList;
import com.keenant.tabbed.util.Reflection;
import com.keenant.tabbed.util.Skins;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;

public class PlayerTabItem implements TabItem {
    @Getter private final Player player;
    @Getter private final WrappedGameProfile profile;

    public PlayerTabItem(Player player) {
        this.player = player;
        this.profile = WrappedGameProfile.fromPlayer(player);
    }

    @Override
    public String getText() {
        return this.player.getPlayerListName();
    }

    @Override
    public int getPing() {
        try {
            Object craftPlayer = Reflection.getHandle(this.player);
            return craftPlayer.getClass().getDeclaredField("ping").getInt(craftPlayer);
        } catch (Exception e) {
            throw new RuntimeException("couldn't get player ping", e);
        }
    }

    @Override
    public WrappedSignedProperty getSkin() {
        Collection<WrappedSignedProperty> properties = this.profile.getProperties().get("textures");
        if (properties != null && properties.size() > 0)
            return properties.iterator().next();
        return Skins.getDefaultSkin();
    }
}
