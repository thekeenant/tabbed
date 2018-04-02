package com.keenant.tabbed.tablist;

import com.google.common.base.Preconditions;
import com.keenant.tabbed.Tabbed;
import com.keenant.tabbed.item.BlankTabItem;
import com.keenant.tabbed.item.TabItem;
import lombok.*;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;

/**
 * An implementation of SimpleTabList that behaves like an HTML/CSS table.
 * It has columns and rows where (0,0) is the top left and (columns - 1, rows - 1)
 * is the top right.
 *
 * It supports some fancy operations like filling a portion of the table
 * in any direction.
 */
@ToString
public class TableTabList extends SimpleTabList {
    @Getter private final int columns;
    @Getter private final int rows;
    @Getter private final TableBox box;

    public TableTabList(Tabbed tabbed, Player player, int columns, int minColumnWidth, int maxColumnWidth) {
        super(tabbed, player, -1, minColumnWidth, maxColumnWidth);
        this.columns = columns;
        this.rows = getMinRows(columns);
        this.box = new TableBox(new TableCell(0, 0), new TableCell(this.columns - 1, this.rows - 1));
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
    public TabItem remove(int index) {
        TabItem prev = get(index);
        set(index, new BlankTabItem());
        return prev;
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
    public TabItem get(TableCell cell) {
        validateCell(cell);
        return get(getIndex(cell));
    }

    /**
     * Gets the box of the entire tab list.
     * @return
     */
    public TableBox getBox() {
        return this.box.clone();
    }

    /**
     * Set a single item at the given cell.
     * @param cell
     * @param item
     * @return The previous item
     */
    public TabItem set(TableCell cell, TabItem item) {
        return set(cell.getColumn(), cell.getRow(), item);
    }

    /**
     * Set a single item at the given column and row.
     * @param column
     * @param row
     * @param item
     * @return The tab item provided.
     */
    public TabItem set(int column, int row, TabItem item) {
        return super.set(getIndex(column, row), item);
    }

    /**
     * Set a bunch of items.
     * @param items
     */
    public void setTable(Map<TableCell,TabItem> items) {
        for (Entry<TableCell,TabItem> entry : items.entrySet())
            validateCell(entry.getKey());

        // new items
        Map<Integer,TabItem> indexItems = new HashMap<>(items.size());
        for (Entry<TableCell,TabItem> entry : items.entrySet())
            indexItems.put(getIndex(entry.getKey()), entry.getValue());

        super.set(indexItems);
    }

    /**
     * Remove an item by column/row.
     * @param column
     * @param row
     */
    public void remove(int column, int row) {
        remove(getIndex(column, row));
    }

    /**
     * Remove an item by table cell.
     * @param cell
     */
    public void remove(TableCell cell) {
        remove(cell.getColumn(), cell.getRow());
    }

    /**
     * Fill a box from the top left horizontally.
     * @param box
     * @param items
     * @return
     */
    public boolean fill(TableBox box, List<TabItem> items) {
        return fill(box, items, TableCorner.TOP_LEFT);
    }

    /**
     * Fill a box from a specific corner horizontally.
     * @param box
     * @param items
     * @return
     */
    public boolean fill(TableBox box, List<TabItem> items, TableCorner corner) {
        return fill(box, items, corner, FillDirection.HORIZONTAL);
    }

    /**
     * Fills a box with a list of tab items.
     * @param box
     * @param items The items to fill the box with.
     * @param startCorner Where to begin filling the box.
     * @param direction The direction to fill the box.
     * @return
     */
    public boolean fill(TableBox box, List<TabItem> items, TableCorner startCorner, FillDirection direction) {
        return fill(box.getTopLeft().getColumn(), box.getTopLeft().getRow(), box.getBottomRight().getColumn(), box.getBottomRight().getRow(), items, startCorner, direction);
    }

    /**
     * Fills a box with a list of tab items.
     * @param col1 x1 Must be less than or equal to x2
     * @param row1 y1 Must be less than or equal to y2
     * @param col2 x2
     * @param row2 y2
     * @param items The items to fill the box with.
     * @param startCorner Where to begin filling the box.
     * @param direction The direction to fill the box.
     * @return True if all the items fit, false if otherwise.
     */
    public boolean fill(int col1, int row1, int col2, int row2, List<TabItem> items, TableCorner startCorner, FillDirection direction) {
        validateCell(col1, row1);
        validateCell(col2, row2);
        Preconditions.checkNotNull(items, "items can't be null");
        Preconditions.checkNotNull(startCorner, "startCorner can't be null");
        Preconditions.checkNotNull(direction, "direction can't be null");

        Map<Integer,TabItem> map = new HashMap<>();
        Iterator<TabItem> iterator = items.iterator();

        boolean reverseCol = false;
        boolean reverseRow = false;

        if (startCorner == TableCorner.TOP_RIGHT || startCorner == TableCorner.BOTTOM_RIGHT)
            reverseCol = true;
        if (startCorner == TableCorner.BOTTOM_LEFT || startCorner == TableCorner.BOTTOM_RIGHT)
            reverseRow = true;

        if (direction == FillDirection.HORIZONTAL) {
            for (int row = row1; row <= row2; row++) {
                for (int col = col1; col <= col2; col++) {
                    int fixedCol = reverseCol ? col2 - (col - col1) : col;
                    int fixedRow = reverseRow ? row2 - (row - row1) : row;

                    if (iterator.hasNext())
                        map.put(getIndex(fixedCol, fixedRow), iterator.next());
                }
            }
        }
        else if (direction == FillDirection.VERTICAL) {
            for (int col = col1; col <= col2; col++) {
                for (int row = row1; row <= row2; row++) {
                    int fixedRow = reverseRow ? row2 - (row - row1) : row;
                    int fixedCol = reverseCol ? col2 - (col - col1) : col;

                    if (iterator.hasNext())
                        map.put(getIndex(fixedCol, fixedRow), iterator.next());
                }
            }
        }

        Tabbed.log(Level.INFO, "Filling " + col1 + "," + row1 + "->" + col2 + "," + row2 + " with " + map.size() + " items");
        set(map);
        return !iterator.hasNext();
    }

    private void reset() {
        Map<Integer,TabItem> newItems = new HashMap<>();
        for (int row = 0; row < this.columns; row++) {
            for (int column = 0; column < this.rows; column++) {
                TabItem item = new BlankTabItem();
                newItems.put(getIndex(row, column), item);
            }
        }
        set(newItems);
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
    public static class TableCell {
        private int column;
        private int row;

        public TableCell add(int columns, int rows) {
            this.column += columns;
            this.row += rows;
            return this;
        }

        public TableCell clone() {
            return new TableCell(this.column, this.row);
        }

        @Override
        public String toString() {
            return column + "," + row;
        }
    }

    /**
     * Represents an area of the table.
     */
    @ToString
    @EqualsAndHashCode
    public static class TableBox {
        @Getter private final List<TableCell> cells;

        public TableBox(TableCell topLeft, TableCell bottomRight) {
            int width = bottomRight.getColumn() - topLeft.getColumn();

            Preconditions.checkArgument(topLeft.getColumn() <= bottomRight.getColumn(), "col1 must be less than or equal to col2");
            Preconditions.checkArgument(topLeft.getRow() <= bottomRight.getRow(), "row1 must be less than or equal to row2");

            this.cells = new ArrayList<>(4);
            this.cells.add(topLeft.clone());
            this.cells.add(topLeft.clone().add(width, 0));
            this.cells.add(bottomRight.clone());
            this.cells.add(bottomRight.clone().add(-width, 0));
        }

        /**
         * Get a corner of this box.
         * @param corner
         * @return
         */
        public TableCell get(TableCorner corner) {
            return this.cells.get(corner.ordinal());
        }

        /**
         * Top left.
         * @return
         */
        public TableCell getTopLeft() {
            return get(TableCorner.TOP_LEFT);
        }

        /**
         * Top right.
         * @return
         */
        public TableCell getTopRight() {
            return get(TableCorner.TOP_RIGHT);
        }

        /**
         * Bottom right.
         * @return
         */
        public TableCell getBottomRight() {
            return get(TableCorner.BOTTOM_RIGHT);
        }

        /**
         * Bottom left.
         * @return
         */
        public TableCell getBottomLeft() {
            return get(TableCorner.BOTTOM_LEFT);
        }

        /**
         * Get the width of this box.
         * @return
         */
        public int getWidth() {
            return getTopRight().getColumn() - getTopLeft().getColumn();
        }

        /**
         * Get the height of this box.
         * @return
         */
        public int getHeight() {
            return getTopLeft().getRow() - getBottomLeft().getRow();
        }

        /**
         * Get the size of this box.
         * @return
         */
        public int getSize() {
            return getWidth() * getHeight();
        }

        public TableBox clone() {
            return new TableBox(this.getTopLeft().clone(), this.getBottomRight().clone());
        }
    }

    /**
     * Represents a corner of the table.
     */
    public enum TableCorner {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_RIGHT,
        BOTTOM_LEFT
    }

    /**
     * Represents a direction in which to fill an area.
     */
    public enum FillDirection {
        HORIZONTAL,
        VERTICAL,
    }
}
