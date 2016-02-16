package com.keenant.tabbed.util;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.keenant.tabbed.TabbedPlugin;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.entity.Player;

import java.util.Collection;

@ToString
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

    @Override
    public boolean equals(Object object) {
        if (object == this)
            return true;
        else if (object instanceof Skin) {
            Skin other = (Skin) object;
            boolean sign = this.property.getSignature().equals(other.getProperty().getSignature());
            boolean value = this.property.getValue().equals(other.getProperty().getValue());
            return sign && value;
        }
        return false;
    }
}
