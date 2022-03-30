package com.querifylabs.blog.optimizer.metadata;


import com.blazingdb.calcite.drill.DrillStatsTable;
import com.blazingdb.calcite.metastore.metadata.TableInfo;
import com.blazingdb.calcite.metastore.metadata.TableMetadataProvider;

public class MetastoreMetadataProviderManager implements MetadataProviderManager {

    private final MetastoreRegistry metastoreRegistry;
    private final TableInfo tableInfo;
    private final MetastoreMetadataProviderConfig config;

    private TableMetadataProvider tableMetadataProvider;

    private SchemaProvider schemaProvider;
    private DrillStatsTable statsProvider;

    public MetastoreMetadataProviderManager(MetastoreRegistry metastoreRegistry,
                                            TableInfo tableInfo, MetastoreMetadataProviderConfig config) {
        this.metastoreRegistry = metastoreRegistry;
        this.tableInfo = tableInfo;
        this.config = config;
    }

    @Override
    public void setSchemaProvider(SchemaProvider schemaProvider) {
        this.schemaProvider = schemaProvider;
    }

    @Override
    public SchemaProvider getSchemaProvider() {
        return schemaProvider;
    }

    @Override
    public void setStatsProvider(DrillStatsTable statsProvider) {
        this.statsProvider = statsProvider;
    }

    @Override
    public DrillStatsTable getStatsProvider() {
        return statsProvider;
    }

    @Override
    public void setTableMetadataProvider(TableMetadataProvider tableMetadataProvider) {
        this.tableMetadataProvider = tableMetadataProvider;
    }

    @Override
    public TableMetadataProvider getTableMetadataProvider() {
        return tableMetadataProvider;
    }

    public MetastoreRegistry getMetastoreRegistry() {
        return metastoreRegistry;
    }

    public TableInfo getTableInfo() {
        return tableInfo;
    }

    public MetastoreMetadataProviderConfig getConfig() {
        return config;
    }

    public boolean usesMetastore() {
        return true;
    }

    public static class MetastoreMetadataProviderConfig {
        private final boolean useSchema;
        private final boolean useStatistics;
        private final boolean fallbackToFileMetadata;

        public MetastoreMetadataProviderConfig(boolean useSchema, boolean useStatistics, boolean fallbackToFileMetadata) {
            this.useSchema = useSchema;
            this.useStatistics = useStatistics;
            this.fallbackToFileMetadata = fallbackToFileMetadata;
        }

        public boolean useSchema() {
            return useSchema;
        }

        public boolean useStatistics() {
            return useStatistics;
        }

        public boolean fallbackToFileMetadata() {
            return fallbackToFileMetadata;
        }
    }
}
