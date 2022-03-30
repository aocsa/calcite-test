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

import com.blazingdb.calcite.expression.expr.*;
import com.blazingdb.calcite.expression.expr.ValueExpressions.*;

public interface ExprVisitor<T, VAL, EXCEP extends Exception> {
    T visitFunctionCall(FunctionCall var1, VAL var2) throws EXCEP;

    T visitFunctionHolderExpression(FunctionHolderExpression var1, VAL var2) throws EXCEP;

    T visitIfExpression(IfExpression var1, VAL var2) throws EXCEP;

    T visitBooleanOperator(BooleanOperator var1, VAL var2) throws EXCEP;

    T visitSchemaPath(SchemaPath var1, VAL var2) throws EXCEP;

    T visitIntConstant(IntExpression var1, VAL var2) throws EXCEP;

    T visitFloatConstant(FloatExpression var1, VAL var2) throws EXCEP;

    T visitLongConstant(LongExpression var1, VAL var2) throws EXCEP;

    T visitDateConstant(DateExpression var1, VAL var2) throws EXCEP;

    T visitTimeConstant(TimeExpression var1, VAL var2) throws EXCEP;

    T visitTimeStampConstant(TimeStampExpression var1, VAL var2) throws EXCEP;

    T visitIntervalYearConstant(IntervalYearExpression var1, VAL var2) throws EXCEP;

    T visitIntervalDayConstant(IntervalDayExpression var1, VAL var2) throws EXCEP;

    T visitDecimal9Constant(Decimal9Expression var1, VAL var2) throws EXCEP;

    T visitDecimal18Constant(Decimal18Expression var1, VAL var2) throws EXCEP;

    T visitDecimal28Constant(Decimal28Expression var1, VAL var2) throws EXCEP;

    T visitDecimal38Constant(Decimal38Expression var1, VAL var2) throws EXCEP;

    T visitVarDecimalConstant(VarDecimalExpression var1, VAL var2) throws EXCEP;

    T visitDoubleConstant(DoubleExpression var1, VAL var2) throws EXCEP;

    T visitBooleanConstant(BooleanExpression var1, VAL var2) throws EXCEP;

    T visitQuotedStringConstant(QuotedString var1, VAL var2) throws EXCEP;

    /*T visitNullConstant(TypedNullConstant var1, VAL var2) throws EXCEP;

    T visitNullExpression(NullExpression var1, VAL var2) throws EXCEP;

    T visitUnknown(LogicalExpression var1, VAL var2) throws EXCEP;*/

    T visitCastExpression(CastExpression var1, VAL var2) throws EXCEP;

    T visitConvertExpression(ConvertExpression var1, VAL var2) throws EXCEP;

    T visitParameter(ParameterExpression var1, VAL var2) throws EXCEP;

    /*T visitTypedFieldExpr(TypedFieldExpr var1, VAL var2) throws EXCEP;*/

    T visitAnyValueExpression(AnyValueExpression var1, VAL var2) throws EXCEP;
}
