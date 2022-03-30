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
package com.blazingdb.calcite.types;

import com.google.protobuf.TextFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import com.blazingdb.calcite.types.TypeProtos.DataMode;
import com.blazingdb.calcite.types.TypeProtos.MajorType;
import com.blazingdb.calcite.types.TypeProtos.MinorType;
import com.blazingdb.calcite.types.TypeProtos.MajorType.Builder;

public class Types {
    public static final int MAX_VARCHAR_LENGTH = 65535;
    public static final int UNDEFINED = 0;
    public static final MajorType NULL;
    public static final MajorType LATE_BIND_TYPE;
    public static final MajorType REQUIRED_BIT;
    public static final MajorType OPTIONAL_BIT;
    public static final MajorType OPTIONAL_INT;

    public Types() {
    }

    public static boolean isUnion(MajorType toType) {
        return toType.getMinorType() == MinorType.UNION;
    }

    public static boolean isComplex(MajorType type) {
        switch(type.getMinorType()) {
            case LIST:
            case MAP:
            case DICT:
                return true;
            default:
                return false;
        }
    }

    public static boolean isRepeated(MajorType type) {
        return type.getMode() == DataMode.REPEATED;
    }

    public static boolean isNumericType(MajorType type) {
        return type.getMode() == DataMode.REPEATED ? false : isNumericType(type.getMinorType());
    }

    public static boolean isNumericType(MinorType type) {
        switch(type) {
            case BIGINT:
            case VARDECIMAL:
            case DECIMAL38SPARSE:
            case DECIMAL38DENSE:
            case DECIMAL28SPARSE:
            case DECIMAL28DENSE:
            case DECIMAL18:
            case DECIMAL9:
            case FLOAT4:
            case FLOAT8:
            case INT:
            case MONEY:
            case SMALLINT:
            case TINYINT:
            case UINT1:
            case UINT2:
            case UINT4:
            case UINT8:
                return true;
            default:
                return false;
        }
    }

    public static boolean isDateTimeType(MajorType type) {
        return type.getMode() == DataMode.REPEATED ? false : isDateTimeType(type.getMinorType());
    }

    public static boolean isDateTimeType(MinorType type) {
        switch(type) {
            case TIME:
            case TIMETZ:
            case DATE:
            case TIMESTAMP:
            case TIMESTAMPTZ:
                return true;
            default:
                return false;
        }
    }

    public static boolean isIntervalType(MajorType type) {
        return type.getMode() == DataMode.REPEATED ? false : isIntervalType(type.getMinorType());
    }

    public static boolean isIntervalType(MinorType type) {
        switch(type) {
            case INTERVAL:
            case INTERVALDAY:
            case INTERVALYEAR:
                return true;
            default:
                return false;
        }
    }

    public static boolean areDecimalTypes(MinorType... types) {
        return Arrays.stream(types).allMatch(Types::isDecimalType);
    }

    public static boolean isDecimalType(MajorType type) {
        return isDecimalType(type.getMinorType());
    }

    public static boolean isDecimalType(MinorType minorType) {
        switch(minorType) {
            case VARDECIMAL:
            case DECIMAL38SPARSE:
            case DECIMAL38DENSE:
            case DECIMAL28SPARSE:
            case DECIMAL28DENSE:
            case DECIMAL18:
            case DECIMAL9:
                return true;
            default:
                return false;
        }
    }

    public static String getSqlTypeName(MajorType type) {
        return type.getMode() != DataMode.REPEATED && type.getMinorType() != MinorType.LIST ? getBaseSqlTypeName(type) : "ARRAY";
    }

    public static String getBaseSqlTypeName(MajorType type) {
        switch(type.getMinorType()) {
            case LIST:
                return "LIST";
            case MAP:
                return "STRUCT";
            case DICT:
                return "MAP";
            case BIGINT:
                return "BIGINT";
            case VARDECIMAL:
            case DECIMAL38SPARSE:
            case DECIMAL38DENSE:
            case DECIMAL28SPARSE:
            case DECIMAL28DENSE:
            case DECIMAL18:
            case DECIMAL9:
                return "DECIMAL";
            case FLOAT4:
                return "FLOAT";
            case FLOAT8:
                return "DOUBLE";
            case INT:
                return "INTEGER";
            case MONEY:
                return "DECIMAL";
            case SMALLINT:
                return "SMALLINT";
            case TINYINT:
                return "TINYINT";
            case UINT1:
                return "TINYINT";
            case UINT2:
                return "SMALLINT";
            case UINT4:
                return "INTEGER";
            case UINT8:
                return "BIGINT";
            case TIME:
                return "TIME";
            case TIMETZ:
                return "TIME WITH TIME ZONE";
            case DATE:
                return "DATE";
            case TIMESTAMP:
                return "TIMESTAMP";
            case TIMESTAMPTZ:
                return "TIMESTAMP WITH TIME ZONE";
            case INTERVAL:
                return "INTERVAL";
            case INTERVALDAY:
                return "INTERVAL DAY TO SECOND";
            case INTERVALYEAR:
                return "INTERVAL YEAR TO MONTH";
            case BIT:
                return "BOOLEAN";
            case VARCHAR:
                return "CHARACTER VARYING";
            case FIXEDCHAR:
                return "CHARACTER";
            case VAR16CHAR:
                return "NATIONAL CHARACTER VARYING";
            case FIXED16CHAR:
                return "NATIONAL CHARACTER";
            case VARBINARY:
                return "BINARY VARYING";
            case FIXEDBINARY:
                return "BINARY";
            case LATE:
                return "ANY";
            case NULL:
                return "NULL";
            case UNION:
                return "UNION";
            case GENERIC_OBJECT:
                return "JAVA_OBJECT";
            default:
                throw new AssertionError("Unexpected/unhandled MinorType value " + type.getMinorType());
        }
    }

    public static String getExtendedSqlTypeName(MajorType type) {
        String typeName = getBaseSqlTypeName(type);
        switch(type.getMinorType()) {
            case LIST:
                typeName = "ARRAY";
            case MAP:
            case DICT:
            case BIGINT:
            default:
                break;
            case VARDECIMAL:
            case DECIMAL38SPARSE:
            case DECIMAL38DENSE:
            case DECIMAL28SPARSE:
            case DECIMAL28DENSE:
            case DECIMAL18:
            case DECIMAL9:
                if (type.getPrecision() > 0) {
                    typeName = typeName + String.format("(%d, %d)", type.getPrecision(), type.getScale());
                }
        }

        return typeName;
    }

    public static String getSqlModeName(MajorType type) {
        switch(type.getMode()) {
            case REQUIRED:
                return "NOT NULL";
            case OPTIONAL:
                return "NULLABLE";
            case REPEATED:
                return "ARRAY";
            default:
                return "UNKNOWN";
        }
    }

    public static int getJdbcTypeCode(String sqlTypeName) {
        byte var2 = -1;
        switch(sqlTypeName.hashCode()) {
            case -2034720975:
                if (sqlTypeName.equals("DECIMAL")) {
                    var2 = 9;
                }
                break;
            case -1967338833:
                if (sqlTypeName.equals("TIMESTAMP WITH TIME ZONE")) {
                    var2 = 24;
                }
                break;
            case -1838645291:
                if (sqlTypeName.equals("STRUCT")) {
                    var2 = 16;
                }
                break;
            case -1618932450:
                if (sqlTypeName.equals("INTEGER")) {
                    var2 = 12;
                }
                break;
            case -1453246218:
                if (sqlTypeName.equals("TIMESTAMP")) {
                    var2 = 25;
                }
                break;
            case -1344909767:
                if (sqlTypeName.equals("CHARACTER VARYING")) {
                    var2 = 6;
                }
                break;
            case -594415409:
                if (sqlTypeName.equals("TINYINT")) {
                    var2 = 26;
                }
                break;
            case 64972:
                if (sqlTypeName.equals("ANY")) {
                    var2 = 0;
                }
                break;
            case 76092:
                if (sqlTypeName.equals("MAP")) {
                    var2 = 17;
                }
                break;
            case 2090926:
                if (sqlTypeName.equals("DATE")) {
                    var2 = 8;
                }
                break;
            case 2407815:
                if (sqlTypeName.equals("NULL")) {
                    var2 = 20;
                }
                break;
            case 2575053:
                if (sqlTypeName.equals("TIME")) {
                    var2 = 23;
                }
                break;
            case 55823113:
                if (sqlTypeName.equals("CHARACTER")) {
                    var2 = 7;
                }
                break;
            case 62552633:
                if (sqlTypeName.equals("ARRAY")) {
                    var2 = 1;
                }
                break;
            case 66988604:
                if (sqlTypeName.equals("FLOAT")) {
                    var2 = 11;
                }
                break;
            case 80895663:
                if (sqlTypeName.equals("UNION")) {
                    var2 = 27;
                }
                break;
            case 176095624:
                if (sqlTypeName.equals("SMALLINT")) {
                    var2 = 21;
                }
                break;
            case 383880731:
                if (sqlTypeName.equals("NATIONAL CHARACTER")) {
                    var2 = 19;
                }
                break;
            case 435511523:
                if (sqlTypeName.equals("INTERVAL YEAR TO MONTH")) {
                    var2 = 14;
                }
                break;
            case 782694408:
                if (sqlTypeName.equals("BOOLEAN")) {
                    var2 = 5;
                }
                break;
            case 812904440:
                if (sqlTypeName.equals("TIME WITH TIME ZONE")) {
                    var2 = 22;
                }
                break;
            case 1353045189:
                if (sqlTypeName.equals("INTERVAL")) {
                    var2 = 13;
                }
                break;
            case 1616163322:
                if (sqlTypeName.equals("INTERVAL DAY TO SECOND")) {
                    var2 = 15;
                }
                break;
            case 1696795441:
                if (sqlTypeName.equals("BINARY VARYING")) {
                    var2 = 3;
                }
                break;
            case 1810802684:
                if (sqlTypeName.equals("JAVA_OBJECT")) {
                    var2 = 28;
                }
                break;
            case 1942019915:
                if (sqlTypeName.equals("NATIONAL CHARACTER VARYING")) {
                    var2 = 18;
                }
                break;
            case 1959128815:
                if (sqlTypeName.equals("BIGINT")) {
                    var2 = 2;
                }
                break;
            case 1959329793:
                if (sqlTypeName.equals("BINARY")) {
                    var2 = 4;
                }
                break;
            case 2022338513:
                if (sqlTypeName.equals("DOUBLE")) {
                    var2 = 10;
                }
        }

        switch(var2) {
            case 0:
                return 1111;
            case 1:
                return 1111;
            case 2:
                return -5;
            case 3:
                return -3;
            case 4:
                return -2;
            case 5:
                return 16;
            case 6:
                return 12;
            case 7:
                return -15;
            case 8:
                return 91;
            case 9:
                return 3;
            case 10:
                return 8;
            case 11:
                return 6;
            case 12:
                return 4;
            case 13:
                return 1111;
            case 14:
                return 1111;
            case 15:
                return 1111;
            case 16:
                return 1111;
            case 17:
                return 1111;
            case 18:
                return -9;
            case 19:
                return -15;
            case 20:
                return 0;
            case 21:
                return 5;
            case 22:
            case 23:
                return 92;
            case 24:
            case 25:
                return 93;
            case 26:
                return -6;
            case 27:
                return 1111;
            case 28:
                return 2000;
            default:
                throw new UnsupportedOperationException("Unexpected/unhandled SqlType value " + sqlTypeName);
        }
    }

    public static boolean isJdbcSignedType(MajorType type) {
        boolean isSigned;
        switch(type.getMode()) {
            case REQUIRED:
            case OPTIONAL:
                switch(type.getMinorType()) {
                    case LIST:
                    case MAP:
                    case DICT:
                    case UINT1:
                    case UINT2:
                    case UINT4:
                    case UINT8:
                    case TIME:
                    case TIMETZ:
                    case DATE:
                    case TIMESTAMP:
                    case TIMESTAMPTZ:
                    case BIT:
                    case VARCHAR:
                    case FIXEDCHAR:
                    case VAR16CHAR:
                    case FIXED16CHAR:
                    case VARBINARY:
                    case FIXEDBINARY:
                    case LATE:
                    case NULL:
                    case UNION:
                    case GENERIC_OBJECT:
                        isSigned = false;
                        return isSigned;
                    case BIGINT:
                    case VARDECIMAL:
                    case DECIMAL38SPARSE:
                    case DECIMAL38DENSE:
                    case DECIMAL28SPARSE:
                    case DECIMAL28DENSE:
                    case DECIMAL18:
                    case DECIMAL9:
                    case FLOAT4:
                    case FLOAT8:
                    case INT:
                    case MONEY:
                    case SMALLINT:
                    case TINYINT:
                    case INTERVAL:
                    case INTERVALDAY:
                    case INTERVALYEAR:
                        isSigned = true;
                        return isSigned;
                    default:
                        throw new UnsupportedOperationException("Unexpected/unhandled MinorType value " + type.getMinorType());
                }
            case REPEATED:
                isSigned = false;
                return isSigned;
            default:
                throw new UnsupportedOperationException("Unexpected/unhandled DataMode value " + type.getMode());
        }
    }

    public static int getJdbcDisplaySize(MajorType type) {
        if (type.getMode() != DataMode.REPEATED && type.getMinorType() != MinorType.LIST) {
            int precision = getPrecision(type);
            switch(type.getMinorType()) {
                case MAP:
                case DICT:
                case INTERVAL:
                case LATE:
                case NULL:
                case UNION:
                    return 0;
                case BIGINT:
                    return 20;
                case VARDECIMAL:
                case DECIMAL38SPARSE:
                case DECIMAL38DENSE:
                case DECIMAL28SPARSE:
                case DECIMAL28DENSE:
                case DECIMAL18:
                case DECIMAL9:
                case MONEY:
                    return 2 + precision;
                case FLOAT4:
                    return 14;
                case FLOAT8:
                    return 24;
                case INT:
                    return 11;
                case SMALLINT:
                    return 6;
                case TINYINT:
                    return 4;
                case UINT1:
                    return 3;
                case UINT2:
                    return 5;
                case UINT4:
                    return 10;
                case UINT8:
                    return 19;
                case TIME:
                    return precision > 0 ? 9 + precision : 8;
                case TIMETZ:
                    return precision > 0 ? 15 + precision : 14;
                case DATE:
                    return 10;
                case TIMESTAMP:
                    return precision > 0 ? 20 + precision : 19;
                case TIMESTAMPTZ:
                    return precision > 0 ? 26 + precision : 25;
                case INTERVALDAY:
                    return precision > 0 ? 12 + precision : 22;
                case INTERVALYEAR:
                    return precision > 0 ? 5 + precision : 9;
                case BIT:
                    return 1;
                case VARCHAR:
                case FIXEDCHAR:
                case VAR16CHAR:
                case FIXED16CHAR:
                    return precision;
                case VARBINARY:
                case FIXEDBINARY:
                    return 2 * precision;
                default:
                    throw new UnsupportedOperationException("Unexpected/unhandled MinorType value " + type.getMinorType());
            }
        } else {
            return 0;
        }
    }

    public static boolean usesHolderForGet(MajorType type) {
        if (type.getMode() == DataMode.REPEATED) {
            return true;
        } else {
            switch(type.getMinorType()) {
                case BIGINT:
                case FLOAT4:
                case FLOAT8:
                case INT:
                case MONEY:
                case SMALLINT:
                case TINYINT:
                case UINT1:
                case UINT2:
                case UINT4:
                case UINT8:
                case TIME:
                case DATE:
                case TIMESTAMP:
                case INTERVALYEAR:
                    return false;
                case VARDECIMAL:
                case DECIMAL38SPARSE:
                case DECIMAL38DENSE:
                case DECIMAL28SPARSE:
                case DECIMAL28DENSE:
                case DECIMAL18:
                case DECIMAL9:
                case TIMETZ:
                case TIMESTAMPTZ:
                case INTERVAL:
                case INTERVALDAY:
                default:
                    return true;
            }
        }
    }

    public static boolean isFixedWidthType(MajorType type) {
        return isFixedWidthType(type.getMinorType());
    }

    public static boolean isFixedWidthType(MinorType type) {
        return !isVarWidthType(type);
    }

    public static boolean isVarWidthType(MinorType type) {
        switch(type) {
            case VARDECIMAL:
            case VARCHAR:
            case VAR16CHAR:
            case VARBINARY:
            case UNION:
                return true;
            default:
                return false;
        }
    }

    public static boolean isScalarStringType(MajorType type) {
        if (type.getMode() == DataMode.REPEATED) {
            return false;
        } else {
            switch(type.getMinorType()) {
                case VARCHAR:
                case FIXEDCHAR:
                case VAR16CHAR:
                case FIXED16CHAR:
                    return true;
                default:
                    return false;
            }
        }
    }

    public static boolean softEquals(MajorType a, MajorType b, boolean allowNullSwap) {
        if (a.getMinorType() != b.getMinorType()) {
            return false;
        } else {
            if (allowNullSwap) {
                switch(a.getMode()) {
                    case REQUIRED:
                    case OPTIONAL:
                        switch(b.getMode()) {
                            case REQUIRED:
                            case OPTIONAL:
                                return true;
                        }
                }
            }

            return a.getMode() == b.getMode();
        }
    }

    public static boolean isUntypedNull(MajorType type) {
        return type.getMinorType() == MinorType.NULL;
    }

    public static MajorType withMode(MinorType type, DataMode mode) {
        return MajorType.newBuilder().setMode(mode).setMinorType(type).build();
    }

    public static MajorType withPrecision(MinorType type, DataMode mode, int precision) {
        return MajorType.newBuilder().setMinorType(type).setMode(mode).setPrecision(precision).build();
    }

    public static MajorType withPrecisionAndScale(MinorType type, DataMode mode, int precision, int scale) {
        return MajorType.newBuilder().setMinorType(type).setMode(mode).setScale(scale).setPrecision(precision).build();
    }

    public static MajorType required(MinorType type) {
        return MajorType.newBuilder().setMode(DataMode.REQUIRED).setMinorType(type).build();
    }

    public static MajorType repeated(MinorType type) {
        return MajorType.newBuilder().setMode(DataMode.REPEATED).setMinorType(type).build();
    }

    public static MajorType optional(MinorType type) {
        return MajorType.newBuilder().setMode(DataMode.OPTIONAL).setMinorType(type).build();
    }

    public static MajorType overrideMode(MajorType originalMajorType, DataMode overrideMode) {
        return originalMajorType.toBuilder().setMode(overrideMode).build();
    }

    public static MajorType getMajorTypeFromName(String typeName) {
        return getMajorTypeFromName(typeName, DataMode.REQUIRED);
    }

    public static MinorType getMinorTypeFromName(String typeName) {
        typeName = typeName.toLowerCase();
        byte var2 = -1;
        switch(typeName.hashCode()) {
            case -1968749622:
                if (typeName.equals("interval_day_time")) {
                    var2 = 23;
                }
                break;
            case -1725395742:
                if (typeName.equals("var16char")) {
                    var2 = 20;
                }
                break;
            case -1430915686:
                if (typeName.equals("simplejson")) {
                    var2 = 28;
                }
                break;
            case -1389167889:
                if (typeName.equals("bigint")) {
                    var2 = 9;
                }
                break;
            case -1388966911:
                if (typeName.equals("binary")) {
                    var2 = 26;
                }
                break;
            case -1325958191:
                if (typeName.equals("double")) {
                    var2 = 12;
                }
                break;
            case -1312398097:
                if (typeName.equals("tinyint")) {
                    var2 = 2;
                }
                break;
            case -891985903:
                if (typeName.equals("string")) {
                    var2 = 19;
                }
                break;
            case -887523944:
                if (typeName.equals("symbol")) {
                    var2 = 14;
                }
                break;
            case -606531192:
                if (typeName.equals("smallint")) {
                    var2 = 4;
                }
                break;
            case -446996552:
                if (typeName.equals("interval_year_month")) {
                    var2 = 22;
                }
                break;
            case 96748:
                if (typeName.equals("any")) {
                    var2 = 31;
                }
                break;
            case 104431:
                if (typeName.equals("int")) {
                    var2 = 7;
                }
                break;
            case 3029738:
                if (typeName.equals("bool")) {
                    var2 = 0;
                }
                break;
            case 3052374:
                if (typeName.equals("char")) {
                    var2 = 15;
                }
                break;
            case 3076014:
                if (typeName.equals("date")) {
                    var2 = 24;
                }
                break;
            case 3271912:
                if (typeName.equals("json")) {
                    var2 = 27;
                }
                break;
            case 3392903:
                if (typeName.equals("null")) {
                    var2 = 30;
                }
                break;
            case 3560141:
                if (typeName.equals("time")) {
                    var2 = 25;
                }
                break;
            case 3600241:
                if (typeName.equals("utf8")) {
                    var2 = 16;
                }
                break;
            case 55126294:
                if (typeName.equals("timestamp")) {
                    var2 = 21;
                }
                break;
            case 55448993:
                if (typeName.equals("extendedjson")) {
                    var2 = 29;
                }
                break;
            case 64711720:
                if (typeName.equals("boolean")) {
                    var2 = 1;
                }
                break;
            case 97526364:
                if (typeName.equals("float")) {
                    var2 = 11;
                }
                break;
            case 111289367:
                if (typeName.equals("uint1")) {
                    var2 = 3;
                }
                break;
            case 111289368:
                if (typeName.equals("uint2")) {
                    var2 = 5;
                }
                break;
            case 111289370:
                if (typeName.equals("uint4")) {
                    var2 = 8;
                }
                break;
            case 111289374:
                if (typeName.equals("uint8")) {
                    var2 = 10;
                }
                break;
            case 111607308:
                if (typeName.equals("utf16")) {
                    var2 = 18;
                }
                break;
            case 236613373:
                if (typeName.equals("varchar")) {
                    var2 = 17;
                }
                break;
            case 1542263633:
                if (typeName.equals("decimal")) {
                    var2 = 13;
                }
                break;
            case 1958052158:
                if (typeName.equals("integer")) {
                    var2 = 6;
                }
        }

        switch(var2) {
            case 0:
            case 1:
                return MinorType.BIT;
            case 2:
                return MinorType.TINYINT;
            case 3:
                return MinorType.UINT1;
            case 4:
                return MinorType.SMALLINT;
            case 5:
                return MinorType.UINT2;
            case 6:
            case 7:
                return MinorType.INT;
            case 8:
                return MinorType.UINT4;
            case 9:
                return MinorType.BIGINT;
            case 10:
                return MinorType.UINT8;
            case 11:
                return MinorType.FLOAT4;
            case 12:
                return MinorType.FLOAT8;
            case 13:
                return MinorType.VARDECIMAL;
            case 14:
            case 15:
            case 16:
            case 17:
                return MinorType.VARCHAR;
            case 18:
            case 19:
            case 20:
                return MinorType.VAR16CHAR;
            case 21:
                return MinorType.TIMESTAMP;
            case 22:
                return MinorType.INTERVALYEAR;
            case 23:
                return MinorType.INTERVALDAY;
            case 24:
                return MinorType.DATE;
            case 25:
                return MinorType.TIME;
            case 26:
                return MinorType.VARBINARY;
            case 27:
            case 28:
            case 29:
                return MinorType.LATE;
            case 30:
            case 31:
                return MinorType.NULL;
            default:
                throw new UnsupportedOperationException("Could not determine type: " + typeName);
        }
    }

    public static MajorType getMajorTypeFromName(String typeName, DataMode mode) {
        return withMode(getMinorTypeFromName(typeName), mode);
    }

    public static String getNameOfMinorType(MinorType type) throws Exception {
        switch(type) {
            case BIGINT:
                return "bigint";
            case VARDECIMAL:
            case DECIMAL38SPARSE:
            case DECIMAL28SPARSE:
            case DECIMAL18:
            case DECIMAL9:
                return "decimal";
            case DECIMAL38DENSE:
            case DECIMAL28DENSE:
            case MONEY:
            case TIMETZ:
            case TIMESTAMPTZ:
            case INTERVAL:
            case INTERVALDAY:
            case INTERVALYEAR:
            case FIXEDCHAR:
            case FIXED16CHAR:
            case FIXEDBINARY:
            default:
                throw new Exception("Unrecognized type " + type);
            case FLOAT4:
                return "float";
            case FLOAT8:
                return "double";
            case INT:
                return "int";
            case SMALLINT:
                return "smallint";
            case TINYINT:
                return "tinyint";
            case UINT1:
                return "uint1";
            case UINT2:
                return "uint2";
            case UINT4:
                return "uint4";
            case UINT8:
                return "uint8";
            case TIME:
                return "time";
            case DATE:
                return "date";
            case TIMESTAMP:
                return "timestamp";
            case BIT:
                return "bool";
            case VARCHAR:
                return "varchar";
            case VAR16CHAR:
                return "utf16";
            case VARBINARY:
                return "binary";
            case LATE:
                throw new Exception("The late type should never appear in execution or an SQL query, so it does not have a name to refer to it.");
        }
    }

    public static String toString(MajorType type) {
        return type != null ? "MajorType[" + TextFormat.shortDebugString(type) + "]" : "null";
    }

    public static int getPrecision(MajorType majorType) {
        if (majorType.hasPrecision()) {
            return majorType.getPrecision();
        } else {
            return isScalarStringType(majorType) ? '\uffff' : 0;
        }
    }

    public static int getScale(MajorType majorType) {
        return majorType.hasScale() ? majorType.getScale() : 0;
    }

    public static boolean isSortable(MinorType type) {
        switch(type) {
            case LIST:
            case MAP:
            case DICT:
                return false;
            default:
                return true;
        }
    }

    public static Builder calculateTypePrecisionAndScale(MajorType leftType, MajorType rightType, Builder typeBuilder) {
        if (leftType.getMinorType().equals(rightType.getMinorType())) {
            boolean isScalarString = isScalarStringType(leftType) && isScalarStringType(rightType);
            boolean isDecimal = isDecimalType(leftType);
            if (isScalarString && leftType.hasPrecision() && rightType.hasPrecision()) {
                typeBuilder.setPrecision(Math.max(leftType.getPrecision(), rightType.getPrecision()));
            }

            if (isDecimal) {
                int scale = Math.max(leftType.getScale(), rightType.getScale());
                int leftNumberOfDigits = leftType.getPrecision() - leftType.getScale();
                int rightNumberOfDigits = rightType.getPrecision() - rightType.getScale();
                int precision = Math.max(leftNumberOfDigits, rightNumberOfDigits) + scale;
                typeBuilder.setPrecision(precision);
                typeBuilder.setScale(scale);
            }
        }

        return typeBuilder;
    }

    public static boolean isSameType(MajorType type1, MajorType type2) {
        return isSameTypeAndMode(type1, type2) && type1.getScale() == type2.getScale() && type1.getPrecision() == type2.getPrecision();
    }

    public static boolean isSameTypeAndMode(MajorType first, MajorType second) {
        return first.getMinorType() == second.getMinorType() && first.getMode() == second.getMode();
    }

    public static boolean isEquivalent(MajorType type1, MajorType type2) {
        if (!isSameType(type1, type2)) {
            return false;
        } else if (type1.getMinorType() != MinorType.UNION) {
            return true;
        } else {
            List<MinorType> subtypes1 = type1.getSubTypeList();
            List<MinorType> subtypes2 = type2.getSubTypeList();
            if (subtypes1 == subtypes2) {
                return true;
            } else if (subtypes1 != null && subtypes2 != null) {
                if (subtypes1.size() != subtypes2.size()) {
                    return false;
                } else {
                    List<MinorType> copy1 = new ArrayList(subtypes1);
                    List<MinorType> copy2 = new ArrayList(subtypes2);
                    Collections.sort(copy1);
                    Collections.sort(copy2);
                    return copy1.equals(copy2);
                }
            } else {
                return false;
            }
        }
    }

    public static String typeKey(MinorType type) {
        return type.name().toLowerCase();
    }

    public static int maxPrecision(MinorType type) {
        switch(type) {
            case VARDECIMAL:
                return 38;
            case DECIMAL38SPARSE:
            case DECIMAL38DENSE:
                return 38;
            case DECIMAL28SPARSE:
            case DECIMAL28DENSE:
                return 28;
            case DECIMAL18:
                return 18;
            case DECIMAL9:
                return 9;
            default:
                return 0;
        }
    }

    public static boolean isNullable(MajorType type) {
        switch(type.getMode()) {
            case REQUIRED:
            case REPEATED:
                return false;
            case OPTIONAL:
                return !isComplex(type);
            default:
                throw new UnsupportedOperationException("Unexpected/unhandled DataMode value " + type.getMode());
        }
    }

    static {
        NULL = required(MinorType.NULL);
        LATE_BIND_TYPE = optional(MinorType.LATE);
        REQUIRED_BIT = required(MinorType.BIT);
        OPTIONAL_BIT = optional(MinorType.BIT);
        OPTIONAL_INT = optional(MinorType.INT);
    }
}
