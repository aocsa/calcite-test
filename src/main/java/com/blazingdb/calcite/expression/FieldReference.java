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
package com.blazingdb.calcite.expression;

import com.blazingdb.calcite.types.TypeProtos.MajorType;
import com.blazingdb.calcite.expression.PathSegment.NameSegment;

public class FieldReference extends SchemaPath {
    private MajorType overrideType;

    public FieldReference(SchemaPath sp) {
        super(sp);
    }

    public FieldReference(CharSequence value) {
        this(value, ExpressionPosition.UNKNOWN);
    }

    public FieldReference(CharSequence value, ExpressionPosition pos) {
        super(new NameSegment(value), pos);
    }

    public FieldReference(String value, ExpressionPosition pos, MajorType dataType) {
        this(value, pos);
        this.overrideType = dataType;
    }

    /**
     * Create a {@link FieldReference} given an unquoted name. (Note: the
     * name here is a misnomer: the name may have been quoted in SQL, but
     * must be unquoted when passed in here.)
     *
     * @param safeString the unquoted field reference
     * @return the field reference expression
     */
    public static FieldReference getWithQuotedRef(CharSequence safeString) {
        return new FieldReference(safeString, ExpressionPosition.UNKNOWN);
    }

    @Override
    public MajorType getMajorType() {
        if (overrideType == null) {
            return super.getMajorType();
        } else {
            return overrideType;
        }
    }
}
