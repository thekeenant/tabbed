package com.keenant.tabbed;

import com.keenant.tabbed.item.TextTabItem;
import com.keenant.tabbed.tablist.CustomTabList;
import com.keenant.tabbed.util.Skins;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;
import java.util.UUID;

public class TabbedPlugin extends JavaPlugin implements Listener {
    private Tabbed tabbed;
    @Override
    public void onEnable() {
        log("Enabled Tabbed " + getDescription().getVersion());
        getServer().getPluginManager().registerEvents(this, this);
        tabbed = new Tabbed(this);
    }

    public static void log(String msg) {
        System.out.println(msg);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        CustomTabList tablist = tabbed.newCustomTabList(event.getPlayer(), 40);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        CustomTabList tablist = (CustomTabList) tabbed.getTabList(event.getPlayer());

        for (int i = 0; i < 40; i++)
            tablist.add(i, new TextTabItem(UUID.randomUUID().toString().substring(0, 6), 50, Skins.getColoredSkins().get(new Random().nextInt(14))));
    }
}
