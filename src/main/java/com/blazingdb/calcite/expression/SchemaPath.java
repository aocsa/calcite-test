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

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import com.google.common.base.Preconditions;
import com.blazingdb.calcite.types.TypeProtos.MajorType;
import com.blazingdb.calcite.expression.PathSegment.ArraySegment;
import com.blazingdb.calcite.expression.PathSegment.NameSegment;

public class SchemaPath extends LogicalExpressionBase {
    public static final String DYNAMIC_STAR = "**";
    public static final SchemaPath STAR_COLUMN = getSimplePath("**");
    private final NameSegment rootSegment;

    public SchemaPath(SchemaPath path) {
        this(path.rootSegment, path.getPosition());
    }

    public SchemaPath(NameSegment rootSegment) {
        this(rootSegment, ExpressionPosition.UNKNOWN);
    }

    /** @deprecated */
    @Deprecated
    public SchemaPath(String simpleName, ExpressionPosition pos) {
        this(new NameSegment(simpleName), pos);
    }

    public SchemaPath(NameSegment rootSegment, ExpressionPosition pos) {
        super(pos);
        this.rootSegment = rootSegment;
    }

    public static SchemaPath getSimplePath(String name) {
        return getCompoundPath(name);
    }

    public static SchemaPath getCompoundPath(String... path) {
        return getCompoundPath(path.length, path);
    }

    public static SchemaPath getCompoundPath(int n, String... path) {
        Preconditions.checkArgument(n > 0);
        NameSegment s = null;

        for(int i = n - 1; i >= 0; --i) {
            s = new NameSegment(path[i], s);
        }

        return new SchemaPath(s);
    }

    public PathSegment getLastSegment() {
        Object s;
        for(s = this.rootSegment; ((PathSegment)s).getChild() != null; s = ((PathSegment)s).getChild()) {
        }

        return (PathSegment)s;
    }

    /*public NamePart getAsNamePart() {
        return getNamePart(this.rootSegment);
    }

    private static NamePart getNamePart(PathSegment s) {
        if (s == null) {
            return null;
        } else {
            Builder b = NamePart.newBuilder();
            if (s.getChild() != null) {
                NamePart namePart = getNamePart(s.getChild());
                if (namePart != null) {
                    b.setChild(namePart);
                }
            }

            if (s.isArray()) {
                if (s.getArraySegment().hasIndex()) {
                    throw new IllegalStateException("You cannot convert a indexed schema path to a NamePart.  NameParts can only reference Vectors, not individual records or values.");
                }

                b.setType(Type.ARRAY);
            } else {
                b.setType(Type.NAME);
                b.setName(s.getNameSegment().getPath());
            }

            return b.build();
        }
    }

    private static PathSegment getPathSegment(NamePart n) {
        PathSegment child = n.hasChild() ? getPathSegment(n.getChild()) : null;
        return (PathSegment)(n.getType() == Type.ARRAY ? new ArraySegment(child) : new NameSegment(n.getName(), child));
    }

    public static SchemaPath create(NamePart namePart) {
        Preconditions.checkArgument(namePart.getType() == Type.NAME);
        return new SchemaPath((NameSegment)getPathSegment(namePart));
    }*/

    public SchemaPath getUnIndexed() {
        return new SchemaPath(this.getUnIndexedNameSegment(this.rootSegment, (NameSegment)null));
    }

    private NameSegment getUnIndexedNameSegment(PathSegment currentSegment, NameSegment resultingSegment) {
        if (!currentSegment.isLastPath()) {
            resultingSegment = this.getUnIndexedNameSegment(currentSegment.getChild(), resultingSegment);
        }

        if (currentSegment.isNamed()) {
            String path = currentSegment.getNameSegment().getPath();
            return new NameSegment(path, resultingSegment);
        } else {
            return resultingSegment;
        }
    }

    public static SchemaPath parseFromString(String expr) {
        if (expr != null && !expr.isEmpty()) {
            if ("**".equals(expr)) {
                return getSimplePath(expr);
            } else {
                LogicalExpression logicalExpression = LogicalExpressionParser.parse(expr);
                if (logicalExpression instanceof SchemaPath) {
                    return (SchemaPath)logicalExpression;
                } else {
                    throw new IllegalStateException(String.format("Schema path is not a valid format: %s.", logicalExpression));
                }
            }
        } else {
            return null;
        }
    }

    public boolean isSimplePath() {
        for(Object seg = this.rootSegment; seg != null; seg = ((PathSegment)seg).getChild()) {
            if (((PathSegment)seg).isArray() && !((PathSegment)seg).isLastPath()) {
                return false;
            }
        }

        return true;
    }

    public boolean isArray() {
        for(Object seg = this.rootSegment; seg != null; seg = ((PathSegment)seg).getChild()) {
            if (((PathSegment)seg).isArray()) {
                return true;
            }
        }

        return false;
    }

    public boolean isLeaf() {
        return this.rootSegment.isLastPath();
    }

    public boolean isDynamicStar() {
        return this.isLeaf() && this.nameEquals("**");
    }

    public boolean nameEquals(String name) {
        return this.rootSegment.nameEquals(name);
    }

    public String rootName() {
        return this.rootSegment.getPath();
    }

    public <T, V, E extends Exception> T accept(ExprVisitor<T, V, E> visitor, V value) throws E {
        return visitor.visitSchemaPath(this, value);
    }

    public SchemaPath getChild(String childPath) {
        NameSegment newRoot = this.rootSegment.cloneWithNewChild(new NameSegment(childPath));
        return new SchemaPath(newRoot);
    }

    public SchemaPath getChild(String childPath, Object originalValue, MajorType valueType) {
        NameSegment newRoot = this.rootSegment.cloneWithNewChild(new NameSegment(childPath, originalValue, valueType));
        return new SchemaPath(newRoot);
    }

    public SchemaPath getChild(int index) {
        NameSegment newRoot = this.rootSegment.cloneWithNewChild(new ArraySegment(index));
        return new SchemaPath(newRoot);
    }

    public SchemaPath getChild(int index, Object originalValue, MajorType valueType) {
        NameSegment newRoot = this.rootSegment.cloneWithNewChild(new ArraySegment(index, originalValue, valueType));
        return new SchemaPath(newRoot);
    }

    public NameSegment getRootSegment() {
        return this.rootSegment;
    }

    public String getAsUnescapedPath() {
        StringBuilder sb = new StringBuilder();
        PathSegment seg = this.getRootSegment();
        if (((PathSegment)seg).isArray()) {
            throw new IllegalStateException("Drill doesn't currently support top level arrays");
        } else {
            sb.append(((PathSegment)seg).getNameSegment().getPath());

            while((seg = ((PathSegment)seg).getChild()) != null) {
                if (((PathSegment)seg).isNamed()) {
                    sb.append('.');
                    sb.append(((PathSegment)seg).getNameSegment().getPath());
                } else {
                    sb.append('[');
                    sb.append(((PathSegment)seg).getArraySegment().getIndex());
                    sb.append(']');
                }
            }

            return sb.toString();
        }
    }

    /* public MajorType getMajorType() {
        return Types.LATE_BIND_TYPE;
    }*/

    public int hashCode() {
        return this.rootSegment == null ? 0 : this.rootSegment.hashCode();
    }

    public boolean equals(Object obj) {
        return this == obj || obj instanceof SchemaPath && Objects.equals(this.rootSegment, ((SchemaPath)obj).rootSegment);
    }

    public boolean contains(SchemaPath path) {
        return this == path || path != null && (this.rootSegment == null || this.rootSegment.contains(path.rootSegment));
    }

    public Iterator<LogicalExpression> iterator() {
        return Collections.emptyIterator();
    }

    /* public String toString() {
        return ExpressionStringBuilder.toString(this);
    }

    public String toExpr() {
        return ExpressionStringBuilder.toString(this);
    }*/

    public String getRootSegmentPath() {
        return this.rootSegment.getPath();
    }

    public static class De extends StdDeserializer<SchemaPath> {
        public De() {
            super(LogicalExpression.class);
        }

        public SchemaPath deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            return SchemaPath.parseFromString(jp.getText());
        }
    }
}
