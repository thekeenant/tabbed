package com.keenant.tabbed;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.google.common.collect.ImmutableMap;
import com.keenant.tabbed.item.PlayerTabItem;
import com.keenant.tabbed.item.TextTabItem;
import com.keenant.tabbed.util.Skins;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class TabbedPlugin extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        log("Enabled Tabbed " + getDescription().getVersion());
        getServer().getPluginManager().registerEvents(this, this);

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Server.PLAYER_INFO) {
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                List<PlayerInfoData> playerData = packet.getPlayerInfoDataLists().read(0);
                List<PlayerInfoData> newData = new ArrayList<>();

                for (PlayerInfoData data : playerData) {
                    if (data.getProfile().getName().startsWith("$")) {
                        String corrected = data.getProfile().getName().substring(1);
                        WrappedGameProfile profile = new WrappedGameProfile(data.getProfile().getUUID(), corrected);
                        profile.getProperties().putAll(data.getProfile().getProperties());

                        PlayerInfoData replaced = new PlayerInfoData(profile, data.getPing(), data.getGameMode(), data.getDisplayName());
                        newData.add(replaced);
                    }
                }

                if (newData.size() == 0)
                    event.setCancelled(true);
                else
                    packet.getPlayerInfoDataLists().write(0, newData);
            }
        });
    }

    public static void log(String msg) {
        System.out.println(msg);
    }

    private TabList tabList;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.tabList = new TabList(event.getPlayer(), 3);

        Scoreboard sb = Bukkit.getServer().getScoreboardManager().getNewScoreboard();

        event.getPlayer().setScoreboard(sb);

        Team team = sb.registerNewTeam("test");
        team.setPrefix("PREFIX");
        team.addEntry("One,One");

        tabList.set(0, 0, new TextTabItem("0,0"));
        tabList.set(1, 1, new TextTabItem("1,1"));
        tabList.set(2, 2, new TextTabItem("2,2"));

        tabList.set(0, 1, new PlayerTabItem(event.getPlayer()));
        tabList.set(0, 2, new PlayerTabItem(event.getPlayer()));
        tabList.set(0, 3, new PlayerTabItem(event.getPlayer()));

        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                tabList.set(1, 0, new TextTabItem(UUID.randomUUID().toString().substring(0, 6), 50, Skins.getColoredSkins().get(new Random().nextInt(14))));
            }
        }, 0, 20 * 5);
    }
}
