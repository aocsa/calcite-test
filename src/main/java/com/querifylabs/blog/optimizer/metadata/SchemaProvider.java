package com.querifylabs.blog.optimizer.metadata;


import java.io.IOException;
import java.util.Map;

/**
 * Provides mechanisms to manage schema: store / read / delete.
 */
public interface SchemaProvider {

    /**
     * Default schema file name where schema is stored on file system.
     * File is hidden to avoid including it when reading table data.
     */
    String DEFAULT_SCHEMA_NAME = ".drill.schema";

    /**
     * Deletes schema.
     */
    void delete() throws IOException;

    /**
     * Reads schema into {@link SchemaContainer}. Depending on implementation, can read from a file
     * or from the given input.
     *
     * @return table schema instance
     */
    SchemaContainer read() throws IOException;

    /**
     * Checks if schema exists.
     *
     * @return true if schema exists, false otherwise
     */
    boolean exists() throws IOException;
}
