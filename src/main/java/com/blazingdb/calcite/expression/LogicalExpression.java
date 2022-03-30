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

import com.blazingdb.calcite.types.TypeProtos;

/*@JsonSerialize(
        using = LogicalExpression.Se.class
)*/
public interface LogicalExpression extends Iterable<LogicalExpression> {
    TypeProtos.MajorType getMajorType();

    <T, V, E extends Exception> T accept(ExprVisitor<T, V, E> var1, V var2) throws E;

    ExpressionPosition getPosition();

    int getSelfCost();

    int getCumulativeCost();

    /* public static class Se extends StdSerializer<LogicalExpression> {
        protected Se() {
            super(LogicalExpression.class);
        }

        public void serialize(LogicalExpression value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            StringBuilder sb = new StringBuilder();
            ExpressionStringBuilder esb = new ExpressionStringBuilder();
            value.accept(esb, sb);
            jgen.writeString(sb.toString());
        }
    }

    public static class De extends StdDeserializer<LogicalExpression> {
        DrillConfig config;

        public De(DrillConfig config) {
            super(LogicalExpression.class);
            this.config = config;
        }

        public LogicalExpression deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            String expr = jp.getText();
            return expr != null && !expr.isEmpty() ? LogicalExpressionParser.parse(expr) : null;
        }
    }*/
}
