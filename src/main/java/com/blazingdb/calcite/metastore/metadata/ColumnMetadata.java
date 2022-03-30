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

import java.time.format.DateTimeFormatter;

import com.blazingdb.calcite.metastore.record.MaterializedField;
import com.blazingdb.calcite.types.TypeProtos.DataMode;
import com.blazingdb.calcite.types.TypeProtos.MajorType;
import com.blazingdb.calcite.types.TypeProtos.MinorType;

public interface ColumnMetadata extends Propertied {
    String EXPECTED_CARDINALITY_PROP = "drill.cardinality";
    String DEFAULT_VALUE_PROP = "drill.default";
    String EXPECTED_WIDTH_PROP = "drill.width";
    String FORMAT_PROP = "drill.format";
    String BLANK_AS_PROP = "drill.blank-as";
    String BLANK_AS_NULL = "null";
    String BLANK_AS_ZERO = "0";
    String EXCLUDE_FROM_WILDCARD = "drill.special";
    int DEFAULT_ARRAY_SIZE = 10;
    String IMPLICIT_COL_TYPE = "drill.implicit";
    String IMPLICIT_FQN = "fqn";
    String IMPLICIT_FILEPATH = "filepath";
    String IMPLICIT_FILENAME = "filename";
    String IMPLICIT_SUFFIX = "suffix";
    String IMPLICIT_PARTITION_PREFIX = "dir";

    StructureType structureType();

    TupleMetadata tupleSchema();

    // VariantMetadata variantSchema();

    ColumnMetadata childSchema();

    MaterializedField schema();

    MaterializedField emptySchema();

    String name();

    MinorType type();

    MajorType majorType();

    DataMode mode();

    int dimensions();

    boolean isNullable();

    boolean isArray();

    boolean isVariableWidth();

    boolean isMap();

    boolean isVariant();

    boolean isDict();

    boolean isDynamic();

    boolean isMultiList();

    boolean isEquivalent(ColumnMetadata var1);

    void setExpectedWidth(int var1);

    int expectedWidth();

    void setExpectedElementCount(int var1);

    int expectedElementCount();

    void setFormat(String var1);

    String format();

    DateTimeFormatter dateTimeFormatter();

    void setDefaultValue(String var1);

    String defaultValue();

    Object decodeDefaultValue();

    String valueToString(Object var1);

    Object valueFromString(String var1);

    ColumnMetadata cloneEmpty();

    int precision();

    int scale();

    void bind(TupleMetadata var1);

    ColumnMetadata copy();

    String typeString();

    String columnString();

    public static enum StructureType {
        PRIMITIVE,
        TUPLE,
        VARIANT,
        MULTI_ARRAY,
        DICT,
        DYNAMIC;

        private StructureType() {
        }
    }
}
