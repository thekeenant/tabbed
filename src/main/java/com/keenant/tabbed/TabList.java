package com.keenant.tabbed;

import com.google.common.base.Preconditions;
import com.keenant.tabbed.item.TabItem;
import lombok.Getter;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class TabList {
    @Getter private final Player player;
    @Getter private String header;
    @Getter private String footer;

    @Getter private final Map<TabCell,TabItem> items;
    @Getter private final int columns;
    @Getter private final int rows;

    public TabList(@Nonnull Player player, int columns, int rows) {
        this.player = player;
        this.items = new HashMap<>();
        this.columns = columns;
        this.rows = rows;
    }

    // Custom tab items

    public void set(int column, int row, TabItem item) {
        validateCell(column, row);
        this.items.put(new TabCell(column, row), item);
    }

    private void validateCell(int column, int row) {
        Preconditions.checkArgument(row >= 0 && row <= this.rows, "row not in allowed range");
        Preconditions.checkArgument(column >= 0 && column <= this.columns, "column not in allowed range");
    }


    // Header / Footer Stuff

    public void setHeaderFooter(String header, String footer) {
        setHeader(header);
        setFooter(footer);
    }

    public void hideHeaderFooter(String header, String footer) {
        hideHeader();
        hideFooter();
    }

    public void setHeader(String header) {
        this.header = header;
        // todo: send update packet
    }

    public void hideHeader() {
        setHeader(null);
    }

    public void setFooter(String footer) {
        this.footer = footer;
        // todo: send update packet
    }

    public void hideFooter() {
        setFooter(null);
    }
}
