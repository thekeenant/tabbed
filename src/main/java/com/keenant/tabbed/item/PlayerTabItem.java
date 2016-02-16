package com.keenant.tabbed.item;

import com.keenant.tabbed.util.Reflection;
import com.keenant.tabbed.util.Skin;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.entity.Player;

@ToString
public class PlayerTabItem implements TabItem {
    @Getter private final Player player;
    @Getter private final PlayerTextProvider textProvider;
    @Getter private final PlayerSkinProvider skinProvider;

    public PlayerTabItem(Player player, PlayerTextProvider textProvider, PlayerSkinProvider skinProvider) {
        this.player = player;
        this.textProvider = textProvider;
        this.skinProvider = skinProvider;
    }

    public PlayerTabItem(Player player, PlayerTextProvider textProvider) {
        this(player, textProvider, SKIN_PROVIDER);
    }

    public PlayerTabItem(Player player) {
        this(player, LIST_NAME_PROVIDER);
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
    public Skin getSkin() {
        return this.skinProvider.getSkin(this.player);
    }

    private static PlayerTextProvider NAME_PROVIDER = new PlayerTextProvider() {
        @Override
        public String getText(Player player) {
            return player.getName();
        }
    };

    private static PlayerTextProvider DISPLAY_NAME_PROVIDER = new PlayerTextProvider() {
        @Override
        public String getText(Player player) {
            return player.getDisplayName();
        }
    };

    private static PlayerTextProvider LIST_NAME_PROVIDER = new PlayerTextProvider() {
        @Override
        public String getText(Player player) {
            return player.getPlayerListName();
        }
    };

    private static PlayerSkinProvider SKIN_PROVIDER = new PlayerSkinProvider() {
        @Override
        public Skin getSkin(Player player) {
            return new Skin(player);
        }
    };

    public interface PlayerTextProvider {
        String getText(Player player);
    }

    public interface PlayerSkinProvider {
        Skin getSkin(Player player);
    }
}
