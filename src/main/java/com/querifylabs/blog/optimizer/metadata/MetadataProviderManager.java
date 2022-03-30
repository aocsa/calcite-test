package com.querifylabs.blog.optimizer.metadata;

import com.blazingdb.calcite.drill.DrillStatsTable;
import com.blazingdb.calcite.metastore.metadata.TableMetadataProvider;

public interface MetadataProviderManager {

    void setSchemaProvider(SchemaProvider schemaProvider);

    SchemaProvider getSchemaProvider();

    void setStatsProvider(DrillStatsTable statsProvider);

    DrillStatsTable getStatsProvider();

    void setTableMetadataProvider(TableMetadataProvider tableMetadataProvider);

    TableMetadataProvider getTableMetadataProvider();

    /**
     * Returns {@code true} if current {@link MetadataProviderManager} instance uses Drill Metastore.
     *
     * @return {@code true} if current {@link MetadataProviderManager} instance uses Drill Metastore,
     * {@code false} otherwise.
     */
    boolean usesMetastore();
}
