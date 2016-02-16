package com.keenant.tabbed.tablist.table;

import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.google.common.base.Preconditions;
import com.keenant.tabbed.Tabbed;
import com.keenant.tabbed.item.EmptyTabItem;
import com.keenant.tabbed.item.TabItem;
import com.keenant.tabbed.tablist.CustomTabList;
import com.keenant.tabbed.util.Packets;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

@ToString
public class TableTabList extends CustomTabList {
    @Getter private final int columns;
    @Getter private final int rows;

    public TableTabList(Tabbed tabbed, Player player, int columns) {
        super(tabbed, player);
        this.columns = columns;
        this.rows = getMinRows(columns);
    }

    @Override
    public int getMaxItems() {
        return this.columns * this.rows;
    }

    @Override
    public TableTabList enable() {
        super.enable();
        reset();
        return this;
    }

    @Override
    public TableTabList disable() {
        super.disable();
        return this;
    }

    public boolean contains(TabCell cell) {
        return contains(getIndex(cell));
    }

    public TabItem get(TabCell cell) {
        return get(getIndex(cell));
    }

    public TabItem set(int column, int row, TabItem item) {
        return set(new TabCell(column, row), item);
    }

    public TabItem set(TabCell cell, TabItem item) {
        validateCell(cell);

        TabItem previousItem = this.get(cell);

        updateTable(cell, item);

        return previousItem;
    }

    public void set(Map<TabCell,TabItem> items) {
        for (Entry<TabCell,TabItem> entry : items.entrySet())
            validateCell(entry.getKey());
        updateTable(items);
    }

    protected void updateTable(TabCell cell, TabItem newItem) {
        this.update(getIndex(cell), newItem);
    }

    protected void updateTable(Map<TabCell,TabItem> items) {

    }

    private void reset() {
        PlayerInfoData[] data = new PlayerInfoData[this.columns * this.rows];
        for (int x = 0; x < this.columns; x++) {
            for (int y = 0; y < this.rows; y++) {
                TabCell cell = new TabCell(x, y);
                TabItem item = new EmptyTabItem(true);
                int index = getIndex(cell);

                if (this.contains(cell)) {
                    PlayerInfoData remove = getPlayerInfoData(index, this.get(cell));
                    Packets.send(this.player, PlayerInfoAction.REMOVE_PLAYER, remove);
                }

                data[index] = getPlayerInfoData(index, item);
            }
        }
        this.clear();
        Packets.send(this.player, PlayerInfoAction.ADD_PLAYER, Arrays.asList(data));
    }

    private static int getMinRows(int columns) {
        if (columns == 1)
            return 1;
        else if (columns == 2)
            return 11;
        else if (columns == 3)
            return 14;
        else if (columns == 4)
            return 20;
        else
            throw new RuntimeException("invalid column count " + columns);
    }

    private int getIndex(TabCell cell) {
        return cell.getRow() + this.rows * cell.getColumn();
    }

    private void validateCell(TabCell cell) {
        Preconditions.checkArgument(cell.getRow() >= 0 && cell.getRow() < this.rows, "row not in allowed range");
        Preconditions.checkArgument(cell.getColumn() >= 0 && cell.getColumn() < this.columns, "column not in allowed range");
    }
}
