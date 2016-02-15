package com.keenant.tabbed.item;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.keenant.tabbed.TabList;
import com.keenant.tabbed.util.Reflection;
import com.keenant.tabbed.util.Skins;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.Collection;

public class PlayerTabItem implements TabItem {
    @Getter private final TabList tabList;
    @Getter private final Player player;
    @Getter private final WrappedGameProfile profile;

    public PlayerTabItem(TabList tabList, Player player) {
        this.tabList = tabList;
        this.player = player;
        this.profile = WrappedGameProfile.fromPlayer(player);
    }

    @Override
    public String getText() {
        return this.player.getDisplayName();
    }

    @Override
    public int getPing() {
        try {
            return Reflection.getHandle(this.player).getClass().getDeclaredField("ping").getInt(this.player);
        } catch (Exception e) {
            throw new RuntimeException("couldn't get player ping", e);
        }
    }

    @Override
    public WrappedSignedProperty getSkin() {
        Collection<WrappedSignedProperty> properties = this.profile.getProperties().get("texture");

        if (properties != null && properties.size() > 0)
            return properties.iterator().next();
        return Skins.getDefaultSkin();
    }
}
