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

import com.google.protobuf.*;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.EnumDescriptor;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FileDescriptor;
import com.google.protobuf.Descriptors.OneofDescriptor;
import com.google.protobuf.GeneratedMessageV3.FieldAccessorTable;
import com.google.protobuf.Internal.EnumLiteMap;
import com.google.protobuf.Internal.ListAdapter;
import com.google.protobuf.Internal.ListAdapter.Converter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class TypeProtos {
    private static final Descriptor internal_static_common_MajorType_descriptor;
    private static final FieldAccessorTable internal_static_common_MajorType_fieldAccessorTable;
    private static FileDescriptor descriptor;

    private TypeProtos() {
    }

    public static void registerAllExtensions(ExtensionRegistryLite registry) {
    }

    public static void registerAllExtensions(ExtensionRegistry registry) {
        registerAllExtensions((ExtensionRegistryLite)registry);
    }

    public static FileDescriptor getDescriptor() {
        return descriptor;
    }

    static {
        String[] descriptorData = new String[]{"\n\u000bTypes.proto\u0012\u0006common\"º\u0001\n\tMajorType\u0012%\n\nminor_type\u0018\u0001 \u0001(\u000e2\u0011.common.MinorType\u0012\u001e\n\u0004mode\u0018\u0002 \u0001(\u000e2\u0010.common.DataMode\u0012\r\n\u0005width\u0018\u0003 \u0001(\u0005\u0012\u0011\n\tprecision\u0018\u0004 \u0001(\u0005\u0012\r\n\u0005scale\u0018\u0005 \u0001(\u0005\u0012\u0010\n\btimeZone\u0018\u0006 \u0001(\u0005\u0012#\n\bsub_type\u0018\u0007 \u0003(\u000e2\u0011.common.MinorType*¯\u0004\n\tMinorType\u0012\b\n\u0004LATE\u0010\u0000\u0012\u0007\n\u0003MAP\u0010\u0001\u0012\u000b\n\u0007TINYINT\u0010\u0003\u0012\f\n\bSMALLINT\u0010\u0004\u0012\u0007\n\u0003INT\u0010\u0005\u0012\n\n\u0006BIGINT\u0010\u0006\u0012\f\n\bDECIMAL9\u0010\u0007\u0012\r\n\tDECIMAL18\u0010\b\u0012\u0013\n\u000fDECIMAL28SPARSE\u0010\t\u0012\u0013\n\u000fDECIMAL38SPARSE\u0010\n\u0012\t\n\u0005MONEY\u0010\u000b\u0012\b\n\u0004DATE\u0010\f\u0012\b\n\u0004TIME\u0010\r\u0012\n\n\u0006TIMETZ\u0010\u000e\u0012\u000f\n\u000bTIMESTAMPTZ\u0010\u000f\u0012\r\n\tTIMESTAMP\u0010\u0010\u0012\f\n\bINTERVAL\u0010\u0011\u0012\n\n\u0006FLOAT4\u0010\u0012\u0012\n\n\u0006FLOAT8\u0010\u0013\u0012\u0007\n\u0003BIT\u0010\u0014\u0012\r\n\tFIXEDCHAR\u0010\u0015\u0012\u000f\n\u000bFIXED16CHAR\u0010\u0016\u0012\u000f\n\u000bFIXEDBINARY\u0010\u0017\u0012\u000b\n\u0007VARCHAR\u0010\u0018\u0012\r\n\tVAR16CHAR\u0010\u0019\u0012\r\n\tVARBINARY\u0010\u001a\u0012\t\n\u0005UINT1\u0010\u001d\u0012\t\n\u0005UINT2\u0010\u001e\u0012\t\n\u0005UINT4\u0010\u001f\u0012\t\n\u0005UINT8\u0010 \u0012\u0012\n\u000eDECIMAL28DENSE\u0010!\u0012\u0012\n\u000eDECIMAL38DENSE\u0010\"\u0012\b\n\u0004NULL\u0010%\u0012\u0010\n\fINTERVALYEAR\u0010&\u0012\u000f\n\u000bINTERVALDAY\u0010'\u0012\b\n\u0004LIST\u0010(\u0012\u0012\n\u000eGENERIC_OBJECT\u0010)\u0012\t\n\u0005UNION\u0010*\u0012\u000e\n\nVARDECIMAL\u0010+\u0012\b\n\u0004DICT\u0010,*4\n\bDataMode\u0012\f\n\bOPTIONAL\u0010\u0000\u0012\f\n\bREQUIRED\u0010\u0001\u0012\f\n\bREPEATED\u0010\u0002B-\n\u001dorg.apache.drill.common.typesB\nTypeProtosH\u0001"};
        Descriptors.FileDescriptor.InternalDescriptorAssigner descriptorAssigner = new FileDescriptor.InternalDescriptorAssigner() {
            @Override
            public ExtensionRegistry assignDescriptors(FileDescriptor fileDescriptor) {
                descriptor = fileDescriptor;
                return ExtensionRegistry.newInstance();
            }
        };

        FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new FileDescriptor[0], descriptorAssigner);
        internal_static_common_MajorType_descriptor = (Descriptor)getDescriptor().getMessageTypes().get(0);
        internal_static_common_MajorType_fieldAccessorTable = new FieldAccessorTable(internal_static_common_MajorType_descriptor, new String[]{"MinorType", "Mode", "Width", "Precision", "Scale", "TimeZone", "SubType"});
    }

    public static final class MajorType extends GeneratedMessageV3 implements MajorTypeOrBuilder {
        private static final long serialVersionUID = 0L;
        private int bitField0_;
        public static final int MINOR_TYPE_FIELD_NUMBER = 1;
        private int minorType_;
        public static final int MODE_FIELD_NUMBER = 2;
        private int mode_;
        public static final int WIDTH_FIELD_NUMBER = 3;
        private int width_;
        public static final int PRECISION_FIELD_NUMBER = 4;
        private int precision_;
        public static final int SCALE_FIELD_NUMBER = 5;
        private int scale_;
        public static final int TIMEZONE_FIELD_NUMBER = 6;
        private int timeZone_;
        public static final int SUB_TYPE_FIELD_NUMBER = 7;
        private List<Integer> subType_;
        private static final Converter<Integer, MinorType> subType_converter_ = new Converter<Integer, MinorType>() {
            public MinorType convert(Integer from) {
                MinorType result = MinorType.valueOf(from);
                return result == null ? MinorType.LATE : result;
            }
        };
        private byte memoizedIsInitialized;
        private static final MajorType DEFAULT_INSTANCE = new MajorType();
        /** @deprecated */
        @Deprecated
        public static final Parser<MajorType> PARSER = new AbstractParser<MajorType>() {
            public MajorType parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return new MajorType(input, extensionRegistry);
            }
        };

        private MajorType(GeneratedMessageV3.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
        }

        private MajorType() {
            this.memoizedIsInitialized = -1;
            this.minorType_ = 0;
            this.mode_ = 0;
            this.subType_ = Collections.emptyList();
        }

//        protected Object newInstance(UnusedPrivateParameter unused) {
//            return new MajorType();
//        }

        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        private MajorType(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this();
            if (extensionRegistry == null) {
                throw new NullPointerException();
            } else {
                int mutable_bitField0_ = 0;
                UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

                try {
                    boolean done = false;

                    while(true) {
                        while(!done) {
                            int tag = input.readTag();
                            int rawValue;
                            MinorType value;
                            switch(tag) {
                                case 0:
                                    done = true;
                                    break;
                                case 8:
                                    rawValue = input.readEnum();
                                    value = MinorType.valueOf(rawValue);
                                    if (value == null) {
                                        unknownFields.mergeVarintField(1, rawValue);
                                    } else {
                                        this.bitField0_ |= 1;
                                        this.minorType_ = rawValue;
                                    }
                                    break;
                                case 16:
                                    rawValue = input.readEnum();
                                    DataMode value_data = DataMode.valueOf(rawValue);
                                    if (value_data == null) {
                                        unknownFields.mergeVarintField(2, rawValue);
                                    } else {
                                        this.bitField0_ |= 2;
                                        this.mode_ = rawValue;
                                    }
                                    break;
                                case 24:
                                    this.bitField0_ |= 4;
                                    this.width_ = input.readInt32();
                                    break;
                                case 32:
                                    this.bitField0_ |= 8;
                                    this.precision_ = input.readInt32();
                                    break;
                                case 40:
                                    this.bitField0_ |= 16;
                                    this.scale_ = input.readInt32();
                                    break;
                                case 48:
                                    this.bitField0_ |= 32;
                                    this.timeZone_ = input.readInt32();
                                    break;
                                case 56:
                                    rawValue = input.readEnum();
                                    value = MinorType.valueOf(rawValue);
                                    if (value == null) {
                                        unknownFields.mergeVarintField(7, rawValue);
                                    } else {
                                        if ((mutable_bitField0_ & 64) == 0) {
                                            this.subType_ = new ArrayList();
                                            mutable_bitField0_ |= 64;
                                        }

                                        this.subType_.add(rawValue);
                                    }
                                    break;
                                case 58:
                                    rawValue = input.readRawVarint32();
                                    int oldLimit = input.pushLimit(rawValue);

                                    while(input.getBytesUntilLimit() > 0) {
                                        int rawValue_i = input.readEnum();
                                        MinorType value_raw = MinorType.valueOf(rawValue_i);
                                        if (value_raw == null) {
                                            unknownFields.mergeVarintField(7, rawValue_i);
                                        } else {
                                            if ((mutable_bitField0_ & 64) == 0) {
                                                this.subType_ = new ArrayList();
                                                mutable_bitField0_ |= 64;
                                            }

                                            this.subType_.add(rawValue_i);
                                        }
                                    }

                                    input.popLimit(oldLimit);
                                    break;
                                default:
                                    if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                                        done = true;
                                    }
                            }
                        }

                        return;
                    }
                } catch (InvalidProtocolBufferException var15) {
                    throw var15.setUnfinishedMessage(this);
                } catch (IOException var16) {
                    throw (new InvalidProtocolBufferException(var16)).setUnfinishedMessage(this);
                } finally {
                    if ((mutable_bitField0_ & 64) != 0) {
                        this.subType_ = Collections.unmodifiableList(this.subType_);
                    }

                    this.unknownFields = unknownFields.build();
                    this.makeExtensionsImmutable();
                }
            }
        }

        public static final Descriptor getDescriptor() {
            return TypeProtos.internal_static_common_MajorType_descriptor;
        }

        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return TypeProtos.internal_static_common_MajorType_fieldAccessorTable.ensureFieldAccessorsInitialized(MajorType.class, Builder.class);
        }

        public boolean hasMinorType() {
            return (this.bitField0_ & 1) != 0;
        }

        public MinorType getMinorType() {
            MinorType result = MinorType.valueOf(this.minorType_);
            return result == null ? MinorType.LATE : result;
        }

        public boolean hasMode() {
            return (this.bitField0_ & 2) != 0;
        }

        public DataMode getMode() {
            DataMode result = DataMode.valueOf(this.mode_);
            return result == null ? DataMode.OPTIONAL : result;
        }

        public boolean hasWidth() {
            return (this.bitField0_ & 4) != 0;
        }

        public int getWidth() {
            return this.width_;
        }

        public boolean hasPrecision() {
            return (this.bitField0_ & 8) != 0;
        }

        public int getPrecision() {
            return this.precision_;
        }

        public boolean hasScale() {
            return (this.bitField0_ & 16) != 0;
        }

        public int getScale() {
            return this.scale_;
        }

        public boolean hasTimeZone() {
            return (this.bitField0_ & 32) != 0;
        }

        public int getTimeZone() {
            return this.timeZone_;
        }

        public List<MinorType> getSubTypeList() {
            return new ListAdapter(this.subType_, subType_converter_);
        }

        public int getSubTypeCount() {
            return this.subType_.size();
        }

        public MinorType getSubType(int index) {
            return (MinorType)subType_converter_.convert(this.subType_.get(index));
        }

        public final boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized == 1) {
                return true;
            } else if (isInitialized == 0) {
                return false;
            } else {
                this.memoizedIsInitialized = 1;
                return true;
            }
        }

        public void writeTo(CodedOutputStream output) throws IOException {
            if ((this.bitField0_ & 1) != 0) {
                output.writeEnum(1, this.minorType_);
            }

            if ((this.bitField0_ & 2) != 0) {
                output.writeEnum(2, this.mode_);
            }

            if ((this.bitField0_ & 4) != 0) {
                output.writeInt32(3, this.width_);
            }

            if ((this.bitField0_ & 8) != 0) {
                output.writeInt32(4, this.precision_);
            }

            if ((this.bitField0_ & 16) != 0) {
                output.writeInt32(5, this.scale_);
            }

            if ((this.bitField0_ & 32) != 0) {
                output.writeInt32(6, this.timeZone_);
            }

            for(int i = 0; i < this.subType_.size(); ++i) {
                output.writeEnum(7, (Integer)this.subType_.get(i));
            }

            this.unknownFields.writeTo(output);
        }

        public int getSerializedSize() {
            int size = this.memoizedSize;
            if (size != -1) {
                return size;
            } else {
                size = 0;
                if ((this.bitField0_ & 1) != 0) {
                    size += CodedOutputStream.computeEnumSize(1, this.minorType_);
                }

                if ((this.bitField0_ & 2) != 0) {
                    size += CodedOutputStream.computeEnumSize(2, this.mode_);
                }

                if ((this.bitField0_ & 4) != 0) {
                    size += CodedOutputStream.computeInt32Size(3, this.width_);
                }

                if ((this.bitField0_ & 8) != 0) {
                    size += CodedOutputStream.computeInt32Size(4, this.precision_);
                }

                if ((this.bitField0_ & 16) != 0) {
                    size += CodedOutputStream.computeInt32Size(5, this.scale_);
                }

                if ((this.bitField0_ & 32) != 0) {
                    size += CodedOutputStream.computeInt32Size(6, this.timeZone_);
                }

                int dataSize = 0;

                for(int i = 0; i < this.subType_.size(); ++i) {
                    dataSize += CodedOutputStream.computeEnumSizeNoTag((Integer)this.subType_.get(i));
                }

                size += dataSize;
                size += 1 * this.subType_.size();
                size += this.unknownFields.getSerializedSize();
                this.memoizedSize = size;
                return size;
            }
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            } else if (!(obj instanceof MajorType)) {
                return super.equals(obj);
            } else {
                MajorType other = (MajorType)obj;
                if (this.hasMinorType() != other.hasMinorType()) {
                    return false;
                } else if (this.hasMinorType() && this.minorType_ != other.minorType_) {
                    return false;
                } else if (this.hasMode() != other.hasMode()) {
                    return false;
                } else if (this.hasMode() && this.mode_ != other.mode_) {
                    return false;
                } else if (this.hasWidth() != other.hasWidth()) {
                    return false;
                } else if (this.hasWidth() && this.getWidth() != other.getWidth()) {
                    return false;
                } else if (this.hasPrecision() != other.hasPrecision()) {
                    return false;
                } else if (this.hasPrecision() && this.getPrecision() != other.getPrecision()) {
                    return false;
                } else if (this.hasScale() != other.hasScale()) {
                    return false;
                } else if (this.hasScale() && this.getScale() != other.getScale()) {
                    return false;
                } else if (this.hasTimeZone() != other.hasTimeZone()) {
                    return false;
                } else if (this.hasTimeZone() && this.getTimeZone() != other.getTimeZone()) {
                    return false;
                } else if (!this.subType_.equals(other.subType_)) {
                    return false;
                } else {
                    return this.unknownFields.equals(other.unknownFields);
                }
            }
        }

        public int hashCode() {
            if (this.memoizedHashCode != 0) {
                return this.memoizedHashCode;
            } else {
                int hash = 41;
                hash = 19 * hash + getDescriptor().hashCode();
                if (this.hasMinorType()) {
                    hash = 37 * hash + 1;
                    hash = 53 * hash + this.minorType_;
                }

                if (this.hasMode()) {
                    hash = 37 * hash + 2;
                    hash = 53 * hash + this.mode_;
                }

                if (this.hasWidth()) {
                    hash = 37 * hash + 3;
                    hash = 53 * hash + this.getWidth();
                }

                if (this.hasPrecision()) {
                    hash = 37 * hash + 4;
                    hash = 53 * hash + this.getPrecision();
                }

                if (this.hasScale()) {
                    hash = 37 * hash + 5;
                    hash = 53 * hash + this.getScale();
                }

                if (this.hasTimeZone()) {
                    hash = 37 * hash + 6;
                    hash = 53 * hash + this.getTimeZone();
                }

                if (this.getSubTypeCount() > 0) {
                    hash = 37 * hash + 7;
                    hash = 53 * hash + this.subType_.hashCode();
                }

                hash = 29 * hash + this.unknownFields.hashCode();
                this.memoizedHashCode = hash;
                return hash;
            }
        }

        public static MajorType parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return (MajorType)PARSER.parseFrom(data);
        }

        public static MajorType parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (MajorType)PARSER.parseFrom(data, extensionRegistry);
        }

        public static MajorType parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return (MajorType)PARSER.parseFrom(data);
        }

        public static MajorType parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (MajorType)PARSER.parseFrom(data, extensionRegistry);
        }

        public static MajorType parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return (MajorType)PARSER.parseFrom(data);
        }

        public static MajorType parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (MajorType)PARSER.parseFrom(data, extensionRegistry);
        }

        public static MajorType parseFrom(InputStream input) throws IOException {
            return (MajorType)GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static MajorType parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (MajorType)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static MajorType parseDelimitedFrom(InputStream input) throws IOException {
            return (MajorType)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
        }

        public static MajorType parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (MajorType)GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }

        public static MajorType parseFrom(CodedInputStream input) throws IOException {
            return (MajorType)GeneratedMessageV3.parseWithIOException(PARSER, input);
        }

        public static MajorType parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (MajorType)GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
        }

        public Builder newBuilderForType() {
            return newBuilder();
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(MajorType prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }

        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE ? new Builder() : (new Builder()).mergeFrom(this);
        }

        protected Builder newBuilderForType(BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }

        public static MajorType getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<MajorType> parser() {
            return PARSER;
        }

        public Parser<MajorType> getParserForType() {
            return PARSER;
        }

        public MajorType getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

        public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MajorTypeOrBuilder {
            private int bitField0_;
            private int minorType_;
            private int mode_;
            private int width_;
            private int precision_;
            private int scale_;
            private int timeZone_;
            private List<Integer> subType_;

            public static final Descriptor getDescriptor() {
                return TypeProtos.internal_static_common_MajorType_descriptor;
            }

            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return TypeProtos.internal_static_common_MajorType_fieldAccessorTable.ensureFieldAccessorsInitialized(MajorType.class, Builder.class);
            }

            private Builder() {
                this.minorType_ = 0;
                this.mode_ = 0;
                this.subType_ = Collections.emptyList();
                this.maybeForceBuilderInitialization();
            }

            private Builder(BuilderParent parent) {
                super(parent);
                this.minorType_ = 0;
                this.mode_ = 0;
                this.subType_ = Collections.emptyList();
                this.maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                if (MajorType.alwaysUseFieldBuilders) {
                }

            }

            public Builder clear() {
                super.clear();
                this.minorType_ = 0;
                this.bitField0_ &= -2;
                this.mode_ = 0;
                this.bitField0_ &= -3;
                this.width_ = 0;
                this.bitField0_ &= -5;
                this.precision_ = 0;
                this.bitField0_ &= -9;
                this.scale_ = 0;
                this.bitField0_ &= -17;
                this.timeZone_ = 0;
                this.bitField0_ &= -33;
                this.subType_ = Collections.emptyList();
                this.bitField0_ &= -65;
                return this;
            }

            public Descriptor getDescriptorForType() {
                return TypeProtos.internal_static_common_MajorType_descriptor;
            }

            public MajorType getDefaultInstanceForType() {
                return MajorType.getDefaultInstance();
            }

            public MajorType build() {
                MajorType result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException(result);
                } else {
                    return result;
                }
            }

            public MajorType buildPartial() {
                MajorType result = new MajorType(this);
                int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 1) != 0) {
                    to_bitField0_ |= 1;
                }

                result.minorType_ = this.minorType_;
                if ((from_bitField0_ & 2) != 0) {
                    to_bitField0_ |= 2;
                }

                result.mode_ = this.mode_;
                if ((from_bitField0_ & 4) != 0) {
                    result.width_ = this.width_;
                    to_bitField0_ |= 4;
                }

                if ((from_bitField0_ & 8) != 0) {
                    result.precision_ = this.precision_;
                    to_bitField0_ |= 8;
                }

                if ((from_bitField0_ & 16) != 0) {
                    result.scale_ = this.scale_;
                    to_bitField0_ |= 16;
                }

                if ((from_bitField0_ & 32) != 0) {
                    result.timeZone_ = this.timeZone_;
                    to_bitField0_ |= 32;
                }

                if ((this.bitField0_ & 64) != 0) {
                    this.subType_ = Collections.unmodifiableList(this.subType_);
                    this.bitField0_ &= -65;
                }

                result.subType_ = this.subType_;
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }

            public Builder clone() {
                return (Builder)super.clone();
            }

            public Builder setField(FieldDescriptor field, Object value) {
                return (Builder)super.setField(field, value);
            }

            public Builder clearField(FieldDescriptor field) {
                return (Builder)super.clearField(field);
            }

            public Builder clearOneof(OneofDescriptor oneof) {
                return (Builder)super.clearOneof(oneof);
            }

            public Builder setRepeatedField(FieldDescriptor field, int index, Object value) {
                return (Builder)super.setRepeatedField(field, index, value);
            }

            public Builder addRepeatedField(FieldDescriptor field, Object value) {
                return (Builder)super.addRepeatedField(field, value);
            }

            public Builder mergeFrom(Message other) {
                if (other instanceof MajorType) {
                    return this.mergeFrom((MajorType)other);
                } else {
                    super.mergeFrom(other);
                    return this;
                }
            }

            public Builder mergeFrom(MajorType other) {
                if (other == MajorType.getDefaultInstance()) {
                    return this;
                } else {
                    if (other.hasMinorType()) {
                        this.setMinorType(other.getMinorType());
                    }

                    if (other.hasMode()) {
                        this.setMode(other.getMode());
                    }

                    if (other.hasWidth()) {
                        this.setWidth(other.getWidth());
                    }

                    if (other.hasPrecision()) {
                        this.setPrecision(other.getPrecision());
                    }

                    if (other.hasScale()) {
                        this.setScale(other.getScale());
                    }

                    if (other.hasTimeZone()) {
                        this.setTimeZone(other.getTimeZone());
                    }

                    if (!other.subType_.isEmpty()) {
                        if (this.subType_.isEmpty()) {
                            this.subType_ = other.subType_;
                            this.bitField0_ &= -65;
                        } else {
                            this.ensureSubTypeIsMutable();
                            this.subType_.addAll(other.subType_);
                        }

                        this.onChanged();
                    }

                    this.mergeUnknownFields(other.unknownFields);
                    this.onChanged();
                    return this;
                }
            }

            public final boolean isInitialized() {
                return true;
            }

            public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
                MajorType parsedMessage = null;

                try {
                    parsedMessage = (MajorType) MajorType.PARSER.parsePartialFrom(input, extensionRegistry);
                } catch (InvalidProtocolBufferException var8) {
                    parsedMessage = (MajorType)var8.getUnfinishedMessage();
                    throw var8.unwrapIOException();
                } finally {
                    if (parsedMessage != null) {
                        this.mergeFrom(parsedMessage);
                    }

                }

                return this;
            }

            public boolean hasMinorType() {
                return (this.bitField0_ & 1) != 0;
            }

            public MinorType getMinorType() {
                MinorType result = MinorType.valueOf(this.minorType_);
                return result == null ? MinorType.LATE : result;
            }

            public Builder setMinorType(MinorType value) {
                if (value == null) {
                    throw new NullPointerException();
                } else {
                    this.bitField0_ |= 1;
                    this.minorType_ = value.getNumber();
                    this.onChanged();
                    return this;
                }
            }

            public Builder clearMinorType() {
                this.bitField0_ &= -2;
                this.minorType_ = 0;
                this.onChanged();
                return this;
            }

            public boolean hasMode() {
                return (this.bitField0_ & 2) != 0;
            }

            public DataMode getMode() {
                DataMode result = DataMode.valueOf(this.mode_);
                return result == null ? DataMode.OPTIONAL : result;
            }

            public Builder setMode(DataMode value) {
                if (value == null) {
                    throw new NullPointerException();
                } else {
                    this.bitField0_ |= 2;
                    this.mode_ = value.getNumber();
                    this.onChanged();
                    return this;
                }
            }

            public Builder clearMode() {
                this.bitField0_ &= -3;
                this.mode_ = 0;
                this.onChanged();
                return this;
            }

            public boolean hasWidth() {
                return (this.bitField0_ & 4) != 0;
            }

            public int getWidth() {
                return this.width_;
            }

            public Builder setWidth(int value) {
                this.bitField0_ |= 4;
                this.width_ = value;
                this.onChanged();
                return this;
            }

            public Builder clearWidth() {
                this.bitField0_ &= -5;
                this.width_ = 0;
                this.onChanged();
                return this;
            }

            public boolean hasPrecision() {
                return (this.bitField0_ & 8) != 0;
            }

            public int getPrecision() {
                return this.precision_;
            }

            public Builder setPrecision(int value) {
                this.bitField0_ |= 8;
                this.precision_ = value;
                this.onChanged();
                return this;
            }

            public Builder clearPrecision() {
                this.bitField0_ &= -9;
                this.precision_ = 0;
                this.onChanged();
                return this;
            }

            public boolean hasScale() {
                return (this.bitField0_ & 16) != 0;
            }

            public int getScale() {
                return this.scale_;
            }

            public Builder setScale(int value) {
                this.bitField0_ |= 16;
                this.scale_ = value;
                this.onChanged();
                return this;
            }

            public Builder clearScale() {
                this.bitField0_ &= -17;
                this.scale_ = 0;
                this.onChanged();
                return this;
            }

            public boolean hasTimeZone() {
                return (this.bitField0_ & 32) != 0;
            }

            public int getTimeZone() {
                return this.timeZone_;
            }

            public Builder setTimeZone(int value) {
                this.bitField0_ |= 32;
                this.timeZone_ = value;
                this.onChanged();
                return this;
            }

            public Builder clearTimeZone() {
                this.bitField0_ &= -33;
                this.timeZone_ = 0;
                this.onChanged();
                return this;
            }

            private void ensureSubTypeIsMutable() {
                if ((this.bitField0_ & 64) == 0) {
                    this.subType_ = new ArrayList(this.subType_);
                    this.bitField0_ |= 64;
                }

            }

            public List<MinorType> getSubTypeList() {
                return new ListAdapter(this.subType_, MajorType.subType_converter_);
            }

            public int getSubTypeCount() {
                return this.subType_.size();
            }

            public MinorType getSubType(int index) {
                return (MinorType) MajorType.subType_converter_.convert(this.subType_.get(index));
            }

            public Builder setSubType(int index, MinorType value) {
                if (value == null) {
                    throw new NullPointerException();
                } else {
                    this.ensureSubTypeIsMutable();
                    this.subType_.set(index, value.getNumber());
                    this.onChanged();
                    return this;
                }
            }

            public Builder addSubType(MinorType value) {
                if (value == null) {
                    throw new NullPointerException();
                } else {
                    this.ensureSubTypeIsMutable();
                    this.subType_.add(value.getNumber());
                    this.onChanged();
                    return this;
                }
            }

            public Builder addAllSubType(Iterable<? extends MinorType> values) {
                this.ensureSubTypeIsMutable();
                Iterator var2 = values.iterator();

                while(var2.hasNext()) {
                    MinorType value = (MinorType)var2.next();
                    this.subType_.add(value.getNumber());
                }

                this.onChanged();
                return this;
            }

            public Builder clearSubType() {
                this.subType_ = Collections.emptyList();
                this.bitField0_ &= -65;
                this.onChanged();
                return this;
            }

            public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
                return (Builder)super.setUnknownFields(unknownFields);
            }

            public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
                return (Builder)super.mergeUnknownFields(unknownFields);
            }
        }
    }

    public interface MajorTypeOrBuilder extends MessageOrBuilder {
        boolean hasMinorType();

        MinorType getMinorType();

        boolean hasMode();

        DataMode getMode();

        boolean hasWidth();

        int getWidth();

        boolean hasPrecision();

        int getPrecision();

        boolean hasScale();

        int getScale();

        boolean hasTimeZone();

        int getTimeZone();

        List<MinorType> getSubTypeList();

        int getSubTypeCount();

        MinorType getSubType(int var1);
    }

    public static enum DataMode implements ProtocolMessageEnum {
        OPTIONAL(0),
        REQUIRED(1),
        REPEATED(2);

        public static final int OPTIONAL_VALUE = 0;
        public static final int REQUIRED_VALUE = 1;
        public static final int REPEATED_VALUE = 2;
        private static final EnumLiteMap<DataMode> internalValueMap = new EnumLiteMap<DataMode>() {
            public DataMode findValueByNumber(int number) {
                return DataMode.forNumber(number);
            }
        };
        private static final DataMode[] VALUES = values();
        private final int value;

        public final int getNumber() {
            return this.value;
        }

        /** @deprecated */
        @Deprecated
        public static DataMode valueOf(int value) {
            return forNumber(value);
        }

        public static DataMode forNumber(int value) {
            switch(value) {
                case 0:
                    return OPTIONAL;
                case 1:
                    return REQUIRED;
                case 2:
                    return REPEATED;
                default:
                    return null;
            }
        }

        public static EnumLiteMap<DataMode> internalGetValueMap() {
            return internalValueMap;
        }

        public final EnumValueDescriptor getValueDescriptor() {
            return (EnumValueDescriptor)getDescriptor().getValues().get(this.ordinal());
        }

        public final EnumDescriptor getDescriptorForType() {
            return getDescriptor();
        }

        public static final EnumDescriptor getDescriptor() {
            return (EnumDescriptor)TypeProtos.getDescriptor().getEnumTypes().get(1);
        }

        public static DataMode valueOf(EnumValueDescriptor desc) {
            if (desc.getType() != getDescriptor()) {
                throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
            } else {
                return VALUES[desc.getIndex()];
            }
        }

        private DataMode(int value) {
            this.value = value;
        }
    }

    public static enum MinorType implements ProtocolMessageEnum {
        LATE(0),
        MAP(1),
        TINYINT(3),
        SMALLINT(4),
        INT(5),
        BIGINT(6),
        DECIMAL9(7),
        DECIMAL18(8),
        DECIMAL28SPARSE(9),
        DECIMAL38SPARSE(10),
        MONEY(11),
        DATE(12),
        TIME(13),
        TIMETZ(14),
        TIMESTAMPTZ(15),
        TIMESTAMP(16),
        INTERVAL(17),
        FLOAT4(18),
        FLOAT8(19),
        BIT(20),
        FIXEDCHAR(21),
        FIXED16CHAR(22),
        FIXEDBINARY(23),
        VARCHAR(24),
        VAR16CHAR(25),
        VARBINARY(26),
        UINT1(29),
        UINT2(30),
        UINT4(31),
        UINT8(32),
        DECIMAL28DENSE(33),
        DECIMAL38DENSE(34),
        NULL(37),
        INTERVALYEAR(38),
        INTERVALDAY(39),
        LIST(40),
        GENERIC_OBJECT(41),
        UNION(42),
        VARDECIMAL(43),
        DICT(44);

        public static final int LATE_VALUE = 0;
        public static final int MAP_VALUE = 1;
        public static final int TINYINT_VALUE = 3;
        public static final int SMALLINT_VALUE = 4;
        public static final int INT_VALUE = 5;
        public static final int BIGINT_VALUE = 6;
        public static final int DECIMAL9_VALUE = 7;
        public static final int DECIMAL18_VALUE = 8;
        public static final int DECIMAL28SPARSE_VALUE = 9;
        public static final int DECIMAL38SPARSE_VALUE = 10;
        public static final int MONEY_VALUE = 11;
        public static final int DATE_VALUE = 12;
        public static final int TIME_VALUE = 13;
        public static final int TIMETZ_VALUE = 14;
        public static final int TIMESTAMPTZ_VALUE = 15;
        public static final int TIMESTAMP_VALUE = 16;
        public static final int INTERVAL_VALUE = 17;
        public static final int FLOAT4_VALUE = 18;
        public static final int FLOAT8_VALUE = 19;
        public static final int BIT_VALUE = 20;
        public static final int FIXEDCHAR_VALUE = 21;
        public static final int FIXED16CHAR_VALUE = 22;
        public static final int FIXEDBINARY_VALUE = 23;
        public static final int VARCHAR_VALUE = 24;
        public static final int VAR16CHAR_VALUE = 25;
        public static final int VARBINARY_VALUE = 26;
        public static final int UINT1_VALUE = 29;
        public static final int UINT2_VALUE = 30;
        public static final int UINT4_VALUE = 31;
        public static final int UINT8_VALUE = 32;
        public static final int DECIMAL28DENSE_VALUE = 33;
        public static final int DECIMAL38DENSE_VALUE = 34;
        public static final int NULL_VALUE = 37;
        public static final int INTERVALYEAR_VALUE = 38;
        public static final int INTERVALDAY_VALUE = 39;
        public static final int LIST_VALUE = 40;
        public static final int GENERIC_OBJECT_VALUE = 41;
        public static final int UNION_VALUE = 42;
        public static final int VARDECIMAL_VALUE = 43;
        public static final int DICT_VALUE = 44;
        private static final EnumLiteMap<MinorType> internalValueMap = new EnumLiteMap<MinorType>() {
            public MinorType findValueByNumber(int number) {
                return MinorType.forNumber(number);
            }
        };
        private static final MinorType[] VALUES = values();
        private final int value;

        public final int getNumber() {
            return this.value;
        }

        /** @deprecated */
        @Deprecated
        public static MinorType valueOf(int value) {
            return forNumber(value);
        }

        public static MinorType forNumber(int value) {
            switch(value) {
                case 0:
                    return LATE;
                case 1:
                    return MAP;
                case 2:
                case 27:
                case 28:
                case 35:
                case 36:
                default:
                    return null;
                case 3:
                    return TINYINT;
                case 4:
                    return SMALLINT;
                case 5:
                    return INT;
                case 6:
                    return BIGINT;
                case 7:
                    return DECIMAL9;
                case 8:
                    return DECIMAL18;
                case 9:
                    return DECIMAL28SPARSE;
                case 10:
                    return DECIMAL38SPARSE;
                case 11:
                    return MONEY;
                case 12:
                    return DATE;
                case 13:
                    return TIME;
                case 14:
                    return TIMETZ;
                case 15:
                    return TIMESTAMPTZ;
                case 16:
                    return TIMESTAMP;
                case 17:
                    return INTERVAL;
                case 18:
                    return FLOAT4;
                case 19:
                    return FLOAT8;
                case 20:
                    return BIT;
                case 21:
                    return FIXEDCHAR;
                case 22:
                    return FIXED16CHAR;
                case 23:
                    return FIXEDBINARY;
                case 24:
                    return VARCHAR;
                case 25:
                    return VAR16CHAR;
                case 26:
                    return VARBINARY;
                case 29:
                    return UINT1;
                case 30:
                    return UINT2;
                case 31:
                    return UINT4;
                case 32:
                    return UINT8;
                case 33:
                    return DECIMAL28DENSE;
                case 34:
                    return DECIMAL38DENSE;
                case 37:
                    return NULL;
                case 38:
                    return INTERVALYEAR;
                case 39:
                    return INTERVALDAY;
                case 40:
                    return LIST;
                case 41:
                    return GENERIC_OBJECT;
                case 42:
                    return UNION;
                case 43:
                    return VARDECIMAL;
                case 44:
                    return DICT;
            }
        }

        public static EnumLiteMap<MinorType> internalGetValueMap() {
            return internalValueMap;
        }

        public final EnumValueDescriptor getValueDescriptor() {
            return (EnumValueDescriptor)getDescriptor().getValues().get(this.ordinal());
        }

        public final EnumDescriptor getDescriptorForType() {
            return getDescriptor();
        }

        public static final EnumDescriptor getDescriptor() {
            return (EnumDescriptor)TypeProtos.getDescriptor().getEnumTypes().get(0);
        }

        public static MinorType valueOf(EnumValueDescriptor desc) {
            if (desc.getType() != getDescriptor()) {
                throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
            } else {
                return VALUES[desc.getIndex()];
            }
        }

        private MinorType(int value) {
            this.value = value;
        }
    }
}
