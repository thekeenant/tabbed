package com.keenant.tabbed.util;

import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

/**
 * Represents the skin/avatar of a tab item.
 */
@ToString
public class Skin {
    @Getter private final WrappedSignedProperty property;
    public static final String TEXTURE_KEY = "textures";

    public Skin(String value, String signature) {
        this(new WrappedSignedProperty(TEXTURE_KEY, value, signature));
    }

    public Skin(WrappedSignedProperty property) {
        Preconditions.checkArgument(property.getName().equals(TEXTURE_KEY));
        this.property = property;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this)
            return true;
        else if (object instanceof Skin) {
            Skin other = (Skin) object;
            boolean sign = Objects.equals(this.property.getSignature(), other.getProperty().getSignature());
            boolean value = Objects.equals(this.property.getValue(), other.getProperty().getValue());
            return sign && value;
        }
        return false;
    }
}
