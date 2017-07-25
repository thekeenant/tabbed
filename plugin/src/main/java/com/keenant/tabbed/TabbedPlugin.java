package com.keenant.tabbed;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class TabbedPlugin extends JavaPlugin {
  @Override
  public void onLoad() {
    getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Tabbed library is ready to be used...");
  }

  @Override
  public void onDisable() {
    getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Tabbed library is disabled...");
  }
}
