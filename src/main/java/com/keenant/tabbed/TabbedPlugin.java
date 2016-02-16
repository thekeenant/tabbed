package com.keenant.tabbed;

import com.keenant.tabbed.tablist.DefaultTabList;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

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
        DefaultTabList tablist = new DefaultTabList(tabbed, event.getPlayer(), -1);
        tablist.enable();
    }

    @EventHandler
    public void onAsyncChat(final AsyncPlayerChatEvent event) {
        getServer().getScheduler().runTask(this, new Runnable() {
            @Override
            public void run() {
                event.getPlayer().setPlayerListName(event.getMessage());
            }
        });
    }
}
