package com.keenant.tabbed.tablist;

import com.google.common.base.Preconditions;
import com.keenant.tabbed.Tabbed;
import com.keenant.tabbed.item.BlankTabItem;
import com.keenant.tabbed.TabItem;
import lombok.*;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@ToString
public class TableTabList extends SimpleTabList {
    @Getter private final int columns;
    @Getter private final int rows;

    public TableTabList(Tabbed tabbed, Player player, int columns, int minColumnWidth, int maxColumnWidth) {
        super(tabbed, player, -1, minColumnWidth, maxColumnWidth);
        this.columns = columns;
        this.rows = getMinRows(columns);
    }

    @Override
    public int getMaxItems() {
        return this.columns * this.rows;
    }

    @Override
    public TabItem get(int index) {
        TabItem item = super.get(index);
        if (item instanceof BlankTabItem)
            return null;
        return item;
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

    /**
     * Checks if the table has an item that is not a BlankTabItem at the given cell.
     * @param cell
     * @return True if the item is not a BlankTabItem.
     */
    public boolean contains(TableCell cell) {
        validateCell(cell);
        return super.contains(getIndex(cell));
    }

    /**
     * Checks if the table has an item that is not a BlankTabItem at the given column and row.
     * @param column
     * @param row
     * @return True if the item is not a BlankTabItem.
     */
    public boolean contains(int column, int row) {
        return contains(getIndex(column, row));
    }

    /**
     * Gets the item at the given cell.
     * @param cell
     * @return The item or null if it is empty (BlankTabItem).
     */
    @Nullable
    public TabItem get(TableCell cell) {
        validateCell(cell);
        return get(getIndex(cell));
    }

    /**
     * Set a single item at the given cell.
     * @param cell
     * @param item
     * @return The previous item
     */
    public TabItem set(TableCell cell, TabItem item) {
        validateCell(cell);

        TabItem previousItem = get(cell);

        updateTable(cell, item);

        return previousItem;
    }

    /**
     * Set a single item at the given column and row.
     * @param column
     * @param row
     * @param item
     * @return The tab item provided.
     */
    public TabItem set(int column, int row, TabItem item) {
        return set(new TableCell(column, row), item);
    }

    /**
     * Set a bunch of items.
     * @param items
     */
    public void set(Map<TableCell,TabItem> items) {
        for (Entry<TableCell,TabItem> entry : items.entrySet())
            validateCell(entry.getKey());
        updateTable(items);
    }

    public boolean set(TableBox box, List<TabItem> items, TableCorner start) {
        return set(box.getTopLeft().getColumn(), box.getTopLeft().getRow(), box.getBottomRight().getColumn(), box.getBottomRight().getRow(), items, start);
    }

    /**
     * Fills a box with a list of tab items.
     * @param col1 x1
     * @param row1 y1
     * @param col2 x2
     * @param row2 y2
     * @param items The items to fill the box with.
     * @param startCorner Where to begin filling the box.
     * @return True if all the items fit, false if otherwise.
     */
    public boolean set(int col1, int row1, int col2, int row2, List<TabItem> items, TableCorner startCorner) {
        validateCell(col1, row1);
        validateCell(col2, row2);

        Map<TableCell,TabItem> map = new HashMap<>();
        Iterator<TabItem> iterator = items.iterator();

        for (int col = col1; col <= col2; col++) {
            for (int row = row1; row <= row2; row++) {
                if (iterator.hasNext()) {
                    map.put(new TableCell(col, row), iterator.next());
                }
            }
        }
        set(map);

        return !iterator.hasNext();
    }

    private void updateTable(TableCell cell, TabItem newItem) {
        update(getIndex(cell), newItem);
    }

    private void updateTable(Map<TableCell,TabItem> items) {
        Map<Integer,TabItem> map = new HashMap<>();
        for (Entry<TableCell,TabItem> entry : items.entrySet())
            map.put(getIndex(entry.getKey()), entry.getValue());
        update(map);
    }

    private void reset() {
        Map<TableCell,TabItem> items = new HashMap<>();
        for (int row = 0; row < this.columns; row++) {
            for (int column = 0; column < this.rows; column++) {
                TableCell cell = new TableCell(row, column);
                TabItem item = new BlankTabItem();
                items.put(cell, item);
            }
        }
        updateTable(items);
    }

    private int getIndex(TableCell cell) {
        return getIndex(cell.getColumn(), cell.getRow());
    }

    private int getIndex(int column, int row) {
        return row + this.rows * column;
    }

    private void validateCell(TableCell cell) {
        validateCell(cell.getColumn(), cell.getRow());
    }

    private void validateCell(int column, int row) {
        Preconditions.checkArgument(row >= 0 && row < this.rows, "row not in allowed range");
        Preconditions.checkArgument(column >= 0 && column < this.columns, "column not in allowed range");
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

    /**
     * Represents a cell in the table.
     */
    @Data
    @AllArgsConstructor
    @EqualsAndHashCode
    public class TableCell {
        private int column;
        private int row;

        @Override
        public String toString() {
            return column + "," + row;
        }
    }

    /**
     * Represents a corner of the table.
     */
    public enum TableCorner {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT;
    }

    @Data
    @AllArgsConstructor
    @EqualsAndHashCode
    public class TableBox {
        private TableCell topLeft;
        private TableCell bottomRight;
    }
}
