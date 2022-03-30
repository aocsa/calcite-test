package com.querifylabs.blog.optimizer;


import com.blazingdb.calcite.drill.DrillStatsTable;
import com.blazingdb.calcite.drill.DrillStatsTable.ColumnStatistics_v1;
import com.blazingdb.calcite.drill.NumericEquiDepthHistogram;
import com.blazingdb.calcite.expression.SchemaPath;
import com.blazingdb.calcite.metastore.statistics.Histogram;
import com.blazingdb.calcite.metastore.statistics.StatisticsHolder;
import com.blazingdb.calcite.metastore.statistics.TableStatisticsKind;
import org.apache.calcite.rel.RelCollation;
import org.apache.calcite.rel.RelDistribution;
import org.apache.calcite.rel.RelDistributionTraitDef;
import org.apache.calcite.rel.RelReferentialConstraint;
import org.apache.calcite.schema.Statistic;
import org.apache.calcite.util.ImmutableBitSet;

import java.util.*;

public class CatalogTableStatsImpl implements Statistic {
    /**
     * Default constructore does nothing
     */

    public  DrillStatsTable statsTable;

    public CatalogTableStatsImpl() {}

    public CatalogTableStatsImpl(DrillStatsTable statsTable) {
        if (statsTable == null) return;
        this.statsTable = statsTable;
        this.rowCount = statsTable.getRowCount();
        System.out.println( this.rowCount );
        this.nnRowCount = new HashMap<>();
        this.ndv = new HashMap<>();
        this.histograms = new HashMap<>();
        for (SchemaPath col : statsTable.getColumns()) {
            String col_name = col.rootName();
            Double nn = statsTable.getNNRowCount(col);
            this.nnRowCount.put(col_name, nn);
            this.ndv.put(col.rootName(), statsTable.getNdv(col));
            if (statsTable.getHistogram(col) != null) {
                this.histograms.put(col.rootName(),
                        new CatalogHistogramImpl((NumericEquiDepthHistogram) statsTable.getHistogram(col)));
            }
        }
    }
     private Long id;
     private Double rowCount;
     private Map<String, Double> nnRowCount;
     private Map<String, Double> ndv;

    private Map<String, CatalogHistogramImpl> histograms;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    @Override
    public Double getRowCount() {
        return rowCount;
    }

    @Override
    public boolean isKey(ImmutableBitSet columns) {
        return false;
    }

    @Override
    public List<ImmutableBitSet> getKeys() {
        return Collections.emptyList();
    }

    @Override
    public List<RelReferentialConstraint> getReferentialConstraints() {
        return Collections.emptyList();
    }

    @Override
    public List<RelCollation> getCollations() {
        return Collections.emptyList();
    }

    @Override
    public RelDistribution getDistribution() {
        return RelDistributionTraitDef.INSTANCE.getDefault();
    }

    public void setRowCount(Double rowCount) {
        this.rowCount = rowCount;
    }

    public Map<String, Double> getNnRowCount() {
        return nnRowCount;
    }

    public void setNnRowCount(Map<String, Double> nnRowCount) {
        this.nnRowCount = nnRowCount;
    }

    public Map<String, Double> getNdv() {
        return ndv;
    }

    public void setNdv(Map<String, Double> ndv) {
        this.ndv = ndv;
    }

    public Map<String, CatalogHistogramImpl> getHistograms() {
        return histograms;
    }

    public void setHistograms(Map<String, CatalogHistogramImpl> histograms) {
        this.histograms = histograms;
    }
}
