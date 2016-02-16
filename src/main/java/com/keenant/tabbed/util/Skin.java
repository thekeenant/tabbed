package com.keenant.tabbed.util;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.google.common.base.Preconditions;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.Collection;

public class Skin {
    @Getter private final WrappedSignedProperty property;

    public Skin(String value, String signature) {
        this(new WrappedSignedProperty("textures", value, signature));
    }

    public Skin(WrappedSignedProperty property) {
        Preconditions.checkArgument(property.getName().equals("textures"));
        this.property = property;
    }

    public Skin(Player player) {
        WrappedSignedProperty property = Skins.getDefaultSkin().getProperty();
        Collection<WrappedSignedProperty> properties = WrappedGameProfile.fromPlayer(player).getProperties().get("textures");
        if (properties != null && properties.size() > 0)
            property = properties.iterator().next();
        this.property = property;
    }
}
