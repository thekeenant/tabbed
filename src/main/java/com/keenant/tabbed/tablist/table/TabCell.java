package com.keenant.tabbed.tablist.table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode
public class TabCell {
    private int column;
    private int row;

    @Override
    public String toString() {
        return column + "," + row;
    }
}
