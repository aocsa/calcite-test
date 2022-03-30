package com.querifylabs.blog.optimizer.metadata;

import java.io.IOException;
import java.util.Map;

import com.blazingdb.calcite.metastore.metadata.TupleMetadata;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
/**
 * Holder class that contains table name, schema definition and current schema container version.
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class SchemaContainer {

    private final String table;
    private final TupleMetadata schema;
    private final Version version;

    @JsonCreator
    public SchemaContainer(@JsonProperty("table") String table,
                           @JsonProperty("schema") TupleMetadata schema,
                           @JsonProperty("version") Integer version) {
        this.table = table;
        this.schema = schema;
        this.version = new Version(version);
    }

    public SchemaContainer(String table, String schema, Map<String, String> properties) throws IOException {
        this(table, schema, properties, Version.VERSION_1); //current default version
    }

    public SchemaContainer(String table, String schema, Map<String, String> properties, Integer version) throws IOException {
        this.table = table;
        this.schema = schema == null ? null : convert(schema, properties);
        this.version = new Version(version);
    }

    @JsonProperty("table")
    public String getTable() {
        return table;
    }

    @JsonProperty("schema")
    public TupleMetadata getSchema() {
        return schema;
    }

    @JsonProperty("version")
    public Integer getVersionValue() {
        return version.getValue();
    }

    @JsonIgnore
    public Version getVersion() {
        return version;
    }

    private TupleMetadata convert(String schemaString, Map<String, String> properties) throws IOException {
        TupleMetadata schema = SchemaExprParser.parseSchema(schemaString);
        if (properties != null) {
            schema.setProperties(properties);
        }
        return schema;
    }

    @Override
    public String toString() {
        return "SchemaContainer{" + "table='" + table + '\'' + ", schema=" + schema + ", version=" + version + '}';
    }

    /**
     * Schema container version holder contains version in int representation.
     * If during initialization null or less then 1 was given, replaces it with
     * {@link #UNDEFINED_VERSION} value.
     */
    public static class Version {

        public static final int UNDEFINED_VERSION = -1;

        public static final int VERSION_1 = 1;

        // is used for testing
        public static final int CURRENT_DEFAULT_VERSION = VERSION_1;

        private final int value;

        public Version(Integer value) {
            this.value = value == null || value < 1 ? UNDEFINED_VERSION : value;
        }

        public int getValue() {
            return value;
        }

        public boolean isUndefined() {
            return UNDEFINED_VERSION == value;
        }

        public int compare(Version versionToCompare) {
            return Integer.compare(value, versionToCompare.value);
        }

        @Override
        public String toString() {
            return "Version{" + "value=" + value + '}';
        }
    }
}
