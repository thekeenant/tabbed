package com.keenant.tabbed.tablist.table;

import com.keenant.tabbed.Tabbed;
import com.keenant.tabbed.item.TabItem;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class BatchTableTabList extends TableTabList {
    @Getter private final Map<TabCell,TabItem> batch; // items to be displayed after update() called.

    public BatchTableTabList(Tabbed tabbed, Player player, int columns) {
        super(tabbed, player, columns);
        this.batch = new HashMap<>();
    }

    @Override
    public BatchTableTabList enable() {
        super.enable();
        return this;
    }

    @Override
    public BatchTableTabList disable() {
        super.disable();
        return this;
    }

    public void update() {
        this.updateTable(this.batch);
        this.batch.clear();
    }

    @Override
    protected void updateTable(TabCell cell, TabItem newItem) {
        this.batch.put(cell, newItem);
    }

    @Override
    protected void updateTable(Map<TabCell,TabItem> items) {
        this.batch.putAll(items);
    }
}
