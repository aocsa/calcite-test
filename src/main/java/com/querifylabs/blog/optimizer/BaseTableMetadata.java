package com.querifylabs.blog.optimizer;


import com.blazingdb.calcite.expression.SchemaPath;
import com.blazingdb.calcite.metastore.metadata.BaseMetadata;
import com.blazingdb.calcite.metastore.metadata.TableMetadata;
import com.blazingdb.calcite.metastore.statistics.ColumnStatistics;
import com.blazingdb.calcite.metastore.statistics.StatisticsHolder;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Base implementation of {@link TableMetadata} interface.
 */
public class BaseTableMetadata extends BaseMetadata implements TableMetadata {

    private final Path location;
    private final Map<String, String> partitionKeys;
    private final List<SchemaPath> interestingColumns;

    private BaseTableMetadata(BaseTableMetadataBuilder builder) {
        super(builder);
        this.location = builder.location;
        this.partitionKeys = builder.partitionKeys;
        this.interestingColumns = builder.interestingColumns;
    }

    public boolean isPartitionColumn(String fieldName) {
        return partitionKeys.containsKey(fieldName);
    }

    boolean isPartitioned() {
        return !partitionKeys.isEmpty();
    }

    @Override
    public Path getLocation() {
        return location;
    }

    @Override
    public List<SchemaPath> getInterestingColumns() {
        return interestingColumns;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        BaseTableMetadata that = (BaseTableMetadata) o;
        return Objects.equals(location, that.location)
                && Objects.equals(partitionKeys, that.partitionKeys)
                && Objects.equals(interestingColumns, that.interestingColumns);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), location, partitionKeys, interestingColumns);
    }

    @Override
    public String toString() {
        return new StringJoiner(",\n", BaseTableMetadata.class.getSimpleName() + "[\n", "]")
                .add("location=" + location)
                .add("partitionKeys=" + partitionKeys)
                .add("interestingColumns=" + interestingColumns)
                .add("tableInfo=" + tableInfo)
                .add("metadataInfo=" + metadataInfo)
                .add("schema=" + schema)
                .add("columnsStatistics=" + columnsStatistics)
                .add("metadataStatistics=" + metadataStatistics)
                .add("lastModifiedTime=" + lastModifiedTime)
                .toString();
    }

    @Override
    public BaseTableMetadata cloneWithStats(Map<SchemaPath, ColumnStatistics<?>> columnStatistics, List<StatisticsHolder<?>> tableStatistics) {
        Map<String, StatisticsHolder<?>> mergedTableStatistics = new HashMap<>(this.metadataStatistics);

        // overrides statistics value for the case when new statistics is exact or existing one was estimated
        tableStatistics.stream()
                .filter(statisticsHolder -> statisticsHolder.getStatisticsKind().isExact()
                        || !this.metadataStatistics.containsKey(statisticsHolder.getStatisticsKind().getName())
                        || !this.metadataStatistics.get(statisticsHolder.getStatisticsKind().getName()).getStatisticsKind().isExact())
                .forEach(statisticsHolder -> mergedTableStatistics.put(statisticsHolder.getStatisticsKind().getName(), statisticsHolder));

        Map<SchemaPath, ColumnStatistics<?>> newColumnsStatistics = new HashMap<>(this.columnsStatistics);
        this.columnsStatistics.forEach(
                (columnName, value) -> {
                    ColumnStatistics<?> sourceStatistics = columnStatistics.get(columnName);
                    if (sourceStatistics != null) {
                        newColumnsStatistics.put(columnName, value.genericClone(sourceStatistics));
                    }
                });

        return BaseTableMetadata.builder()
                .tableInfo(tableInfo)
                .metadataInfo(metadataInfo)
                .location(location)
                .schema(schema)
                .columnsStatistics(newColumnsStatistics)
                .metadataStatistics(mergedTableStatistics.values())
                .lastModifiedTime(lastModifiedTime)
                .partitionKeys(partitionKeys)
                .interestingColumns(interestingColumns)
                .build();
    }
//
//    @Override
//    protected void toMetadataUnitBuilder(TableMetadataUnit.Builder builder) {
//        if (location != null) {
//            builder.location(location.toUri().getPath());
//        }
//        builder.partitionKeys(partitionKeys);
//        if (interestingColumns != null) {
//            builder.interestingColumns(interestingColumns.stream()
//                    .map(SchemaPath::toString)
//                    .collect(Collectors.toList()));
//        }
//    }

    @Override
    public BaseTableMetadataBuilder toBuilder() {
        return builder()
                .tableInfo(tableInfo)
                .metadataInfo(metadataInfo)
                .location(location)
                .schema(schema)
                .columnsStatistics(columnsStatistics)
                .metadataStatistics(metadataStatistics.values())
                .lastModifiedTime(lastModifiedTime)
                .partitionKeys(partitionKeys)
                .interestingColumns(interestingColumns);
    }

    public static BaseTableMetadataBuilder builder() {
        return new BaseTableMetadataBuilder();
    }

    public static class BaseTableMetadataBuilder extends BaseMetadataBuilder<BaseTableMetadataBuilder> {
        private Path location;
        private Map<String, String> partitionKeys;
        private List<SchemaPath> interestingColumns;

        public BaseTableMetadataBuilder location(Path location) {
            this.location = location;
            return self();
        }

        public BaseTableMetadataBuilder partitionKeys(Map<String, String> partitionKeys) {
            this.partitionKeys = partitionKeys;
            return self();
        }

        public BaseTableMetadataBuilder interestingColumns(List<SchemaPath> interestingColumns) {
            this.interestingColumns = interestingColumns;
            return self();
        }

        @Override
        protected void checkRequiredValues() {
            super.checkRequiredValues();
            Objects.requireNonNull(partitionKeys, "partitionKeys were not set");
        }

        @Override
        public BaseTableMetadata build() {
            checkRequiredValues();
            return new BaseTableMetadata(this);
        }

        @Override
        protected BaseTableMetadataBuilder self() {
            return this;
        }
//
//        @Override
//        protected BaseTableMetadataBuilder metadataUnitInternal(TableMetadataUnit unit) {
//            if (unit.location() != null) {
//                location(new Path(unit.location()));
//            }
//            partitionKeys(unit.partitionKeys());
//            if (unit.interestingColumns() != null) {
//                interestingColumns(unit.interestingColumns().stream()
//                        .map(SchemaPath::parseFromString)
//                        .collect(Collectors.toList()));
//            }
//            return self();
//        }
    }
}
