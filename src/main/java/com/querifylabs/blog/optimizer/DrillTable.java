package com.querifylabs.blog.optimizer;

import com.blazingdb.calcite.drill.DrillStatsTable;
import com.blazingdb.calcite.expression.SchemaPath;
import com.blazingdb.calcite.metastore.metadata.*;

import com.blazingdb.calcite.metastore.statistics.ColumnStatistics;
import com.blazingdb.calcite.metastore.statistics.StatisticsHolder;
import com.blazingdb.calcite.metastore.statistics.TableStatisticsKind;
import com.blazingdb.calcite.types.TypeProtos;
import com.querifylabs.blog.optimizer.cost.ScanStats;
import com.querifylabs.blog.optimizer.metadata.MetadataProviderManager;
import com.querifylabs.blog.optimizer.metadata.MetastoreMetadataProviderManager;
import com.querifylabs.blog.optimizer.metadata.MetastoreRegistry;
import com.querifylabs.blog.optimizer.metadata.SchemaProvider;
import org.apache.calcite.DataContext;
import org.apache.calcite.adapter.enumerable.EnumerableTableScan;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.TableScan;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.rel.type.RelDataTypeFieldImpl;
import org.apache.calcite.rel.type.RelRecordType;
import org.apache.calcite.rel.type.StructKind;
import org.apache.calcite.schema.ScannableTable;
import org.apache.calcite.schema.Statistic;
import org.apache.calcite.schema.impl.AbstractTable;
import org.apache.calcite.sql.type.SqlTypeName;

import java.nio.file.Path;
import java.util.*;

public class DrillTable extends AbstractTable implements ScannableTable {

    private final String tableName;
    private final List<String> fieldNames;
    private final List<CatalogColumnDataType> fieldTypes;
    private final CatalogTableStatsImpl statistic;
    private TableMetadata tableMetadata;
    DrillStatsTable statsProvider;

    MetadataProviderManager providerManager = null;
    private RelDataType rowType;
    private ScanStats scanStats;
    private MetadataProviderManager metadataProviderManager;

    private DrillTable(String tableName, List<String> fieldNames, List<CatalogColumnDataType> fieldTypes, CatalogTableStatsImpl statistic) {
        this.tableName = tableName;
        this.fieldNames = fieldNames;
        this.fieldTypes = fieldTypes;
        this.statistic = statistic;

        scanStats = new ScanStats(ScanStats.GroupScanProperty.EXACT_ROW_COUNT, this.statistic.getRowCount(), 1, 1);
        //metadataProviderManager
    }

    public String getTableName() {
        return tableName;
    }


    public static RelDataType
    convertToSqlType(CatalogColumnDataType dataType, RelDataTypeFactory typeFactory) {
        RelDataType temp = null;
        switch(dataType) {
            case INT8:
            case UINT8:
                temp = typeFactory.createSqlType(SqlTypeName.TINYINT);
                break;
            case INT16:
            case UINT16:
                temp = typeFactory.createSqlType(SqlTypeName.SMALLINT);
                break;
            case INT32:
            case UINT32:
                temp = typeFactory.createSqlType(SqlTypeName.INTEGER);
                break;
            case INT64:
            case UINT64:
                temp = typeFactory.createSqlType(SqlTypeName.BIGINT);
                break;
            case FLOAT:
                temp = typeFactory.createSqlType(SqlTypeName.FLOAT);
                break;
            case DOUBLE:
                temp = typeFactory.createSqlType(SqlTypeName.DOUBLE);
                break;
            case BOOL:
                temp = typeFactory.createSqlType(SqlTypeName.BOOLEAN);
                break;
            case DATE32:
                temp = typeFactory.createSqlType(SqlTypeName.DATE);
                break;
            case TIMESTAMP:
                temp = typeFactory.createSqlType(SqlTypeName.TIMESTAMP);
                break;
            case DURATION:
                temp = typeFactory.createSqlType(SqlTypeName.TIME);
                break;
            case DICTIONARY:
            case STRING:
                temp = typeFactory.createSqlType(SqlTypeName.VARCHAR);
                break;
            /// Handle better this cases
            case DECIMAL:
            case DECIMAL128:
            case DECIMAL256:
                temp = typeFactory.createSqlType(SqlTypeName.DECIMAL);
                break;
            default:
                temp = null;
        }
        return temp;
    }

    @Override
    public RelDataType getRowType(RelDataTypeFactory typeFactory) {
        if (rowType == null) {
            List<RelDataTypeField> fields = new ArrayList<>(fieldNames.size());

            for (int i = 0; i < fieldNames.size(); i++) {
                RelDataType fieldType = convertToSqlType(fieldTypes.get(i), typeFactory);
                //RelDataType fieldType = typeFactory.createSqlType(colType);
                RelDataTypeField field = new RelDataTypeFieldImpl(fieldNames.get(i), i, fieldType);
                fields.add(field);
            }

            rowType = new RelRecordType(StructKind.PEEK_FIELDS, fields, false);
        }

        return rowType;
    }

    @Override
    public Statistic getStatistic() {
        return statistic;
    }

    @Override
    public Enumerable<Object[]> scan(DataContext root) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public static DrillTable.Builder newBuilder(String tableName) {
        return new DrillTable.Builder(tableName);
    }

    public RelNode toRel(RelOptTable.ToRelContext context, RelOptTable table) {
        return EnumerableTableScan.create(context.getCluster(), table);
    }


    private void setMetadataProviderManager(DrillTable table, String tableName) {
        if (table != null) {

            if (true/*schemaConfig.getOption(ExecConstants.METASTORE_ENABLED).bool_val*/) {
                try {
                     TableInfo tableInfo = TableInfo.builder()
                            .storagePlugin(tableName)
                            .workspace("tpch")
                            .name(tableName)
                            .build();

                    // @todo: @alex
                    MetastoreRegistry metastoreRegistry = null;
                    providerManager = new MetastoreMetadataProviderManager(metastoreRegistry, tableInfo,
                            new MetastoreMetadataProviderManager.MetastoreMetadataProviderConfig(true, true,
                                    true));

                } catch (Exception e) {
                    System.out.println("Exception happened during obtaining Metastore instance. File system metadata provider will be used.");
                }
            }
            //setMetadataTable(providerManager, table, tableName);
            providerManager.setStatsProvider(this.statistic.statsTable);
           // @todo: @alex
           // providerManager.setSchemaProvider(this.schema);
        }
    }

    public ScanStats getScanStats() {
        return this.scanStats;
    }

    public DrillStatsTable getStatsProvider() {
        if (tableMetadata == null) {
            this.getTableMetadata();
        }
        return  this.statsProvider;
    }

    public TableMetadata getTableMetadata() {
        if (tableMetadata == null) {
            this.setMetadataProviderManager(this, this.tableName);

            SchemaProvider schemaProvider = this.providerManager.getSchemaProvider();
            TableMetadataProvider source = providerManager.getTableMetadataProvider();
            if (source == null) {
                this.statsProvider = providerManager.getStatsProvider();
                Map<SchemaPath, ColumnStatistics<?>> columnsStatistics = new HashMap<>();

                if (statsProvider != null) {
                    if (!statsProvider.isMaterialized()) {
                        statsProvider.materialize();
                    }
                    if (statsProvider.isMaterialized()) {
                        for (SchemaPath column : statsProvider.getColumns()) {
                            columnsStatistics.put(column,
                                    new ColumnStatistics<>(DrillStatsTable.getEstimatedColumnStats(statsProvider, column)));
                        }
                    }
                }

                Path selectionRoot = null;

                TupleMetadata schema = null;
                //@todo, @alex
//                if (this.schema == null && schemaProvider != null) {
//                    try {
//                        schema = schemaProvider.read().getSchema();
//                    } catch (IOException | IllegalArgumentException e) {
//                        logger.warn("Unable to read schema from schema provider [{}]: {}.\n" +
//                                        "Query execution will continue without using the schema.",
//                                selectionRoot, e.getMessage());
//                        logger.trace("Error when reading the schema", e);
//                    }
//                }
                MetadataInfo metadataInfo = MetadataInfo.builder().type(MetadataType.TABLE).build();
                this.tableMetadata = BaseTableMetadata.builder()
                        .tableInfo(TableInfo.UNKNOWN_TABLE_INFO)
                        .metadataInfo(metadataInfo)
                        .location(selectionRoot)
                        .schema(schema)
                        .columnsStatistics(columnsStatistics)
                        .metadataStatistics(DrillStatsTable.getEstimatedTableStats(statsProvider))
                        .partitionKeys(Collections.emptyMap())
                        .build();

            }
        }
        return this.tableMetadata;
    }

    public static final class Builder {

        private final String tableName;
        private final List<String> fieldNames = new ArrayList<>();
        private final List<CatalogColumnDataType> fieldTypes = new ArrayList<>();
        private CatalogTableStatsImpl stats;

        private Builder(String tableName) {
            if (tableName == null || tableName.isEmpty()) {
                throw new IllegalArgumentException("Table name cannot be null or empty");
            }

            this.tableName = tableName;
        }

        public Builder addField(String name, CatalogColumnDataType typeName) {
            if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException("Field name cannot be null or empty");
            }

            if (fieldNames.contains(name)) {
                throw new IllegalArgumentException("Field already defined: " + name);
            }

            fieldNames.add(name);
            fieldTypes.add(typeName);

            return this;
        }


        public Builder withStats(CatalogTableStatsImpl stats) {
            this.stats = stats;

            return this;
        }

        public DrillTable build() {
            if (fieldNames.isEmpty()) {
                throw new IllegalStateException("Table must have at least one field");
            }

            if (this.stats == null) {
                throw new IllegalStateException("Table must have positive row count");
            }

            return new DrillTable(tableName, fieldNames, fieldTypes, this.stats);
        }
    }
}
