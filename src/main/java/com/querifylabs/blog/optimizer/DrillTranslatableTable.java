package com.querifylabs.blog.optimizer;

import org.apache.calcite.config.CalciteConnectionConfig;
import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.plan.RelOptTable.ToRelContext;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.Statistic;
import org.apache.calcite.schema.TranslatableTable;
import org.apache.calcite.schema.Schema.TableType;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlNode;

/**
 * TableMacros must return a TranslatableTable
 * This class adapts the existing DrillTable to a TranslatableTable
 */
public class DrillTranslatableTable implements TranslatableTable {

    /** all calls will be delegated to this field */
    private final DrillTable drillTable;

    public DrillTranslatableTable(DrillTable drillTable) {
        this.drillTable = drillTable;
    }

    public DrillTable getDrillTable() {
        return drillTable;
    }

    @Override
    public RelDataType getRowType(RelDataTypeFactory typeFactory) {
        return drillTable.getRowType(typeFactory);
    }

    @Override
    public Statistic getStatistic() {
        return drillTable.getStatistic();
    }

    @Override
    public RelNode toRel(ToRelContext context, RelOptTable table) {
        return drillTable.toRel(context, table);
    }

    @Override
    public TableType getJdbcTableType() {
        return drillTable.getJdbcTableType();
    }

    @Override
    public boolean rolledUpColumnValidInsideAgg(String column,
                                                SqlCall call, SqlNode parent, CalciteConnectionConfig config) {
        return true;
    }

    @Override
    public boolean isRolledUp(String column) {
        return false;
    }

    @Override
    public int hashCode() {
        return drillTable.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return drillTable.equals(obj);
    }

    @Override
    public String toString() {
        return drillTable.toString();
    }
}