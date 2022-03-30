package com.blazingdb.calcite.util;


import com.querifylabs.blog.optimizer.DrillTable;
import com.querifylabs.blog.optimizer.DrillTranslatableTable;
import org.apache.calcite.plan.RelOptTable;

public class Utilities {

    public static final String COL_NULL_ERROR = "Columns cannot be null. Use star column to select all fields.";

    public static DrillTable getDrillTable(RelOptTable table) {
        DrillTable drillTable = table.unwrap(DrillTable.class);
        if (drillTable == null && table.unwrap(DrillTranslatableTable.class) != null) {
            drillTable = table.unwrap(DrillTranslatableTable.class).getDrillTable();
        }
        return drillTable;
    }
}
