/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blazingdb.calcite.metastore.metadata;

import com.blazingdb.calcite.metastore.record.MaterializedField;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.IOException;
import java.util.List;


public interface TupleMetadata extends Propertied, Iterable<ColumnMetadata> {
    ObjectWriter WRITER = (new ObjectMapper()).writerFor(TupleMetadata.class);
    ObjectReader READER = (new ObjectMapper()).readerFor(TupleMetadata.class);
    String IS_STRICT_SCHEMA_PROP = "drill.strict";

    ColumnMetadata add(MaterializedField var1);

    int addColumn(ColumnMetadata var1);

    int size();

    boolean isEmpty();

    int index(String var1);

    ColumnMetadata metadata(int var1);

    ColumnMetadata metadata(String var1);

    MaterializedField column(int var1);

    MaterializedField column(String var1);

    boolean isEquivalent(TupleMetadata var1);

    ColumnMetadata parent();

    List<MaterializedField> toFieldList();

    List<ColumnMetadata> toMetadataList();

    String fullName(ColumnMetadata var1);

    String fullName(int var1);

    TupleMetadata copy();

    void replace(ColumnMetadata var1);

    default String jsonString() {
        try {
            return WRITER.writeValueAsString(this);
        } catch (JsonProcessingException var2) {
            throw new IllegalStateException("Unable to convert tuple metadata into JSON string: " + this.toString(), var2);
        }
    }

    static TupleMetadata of(String jsonString) {
        if (jsonString != null && !jsonString.trim().isEmpty()) {
            try {
                return (TupleMetadata)READER.readValue(jsonString);
            } catch (IOException var2) {
                throw new IllegalArgumentException("Unable to deserialize given JSON string into tuple metadata: " + jsonString, var2);
            }
        } else {
            return null;
        }
    }
}
