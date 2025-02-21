package ru.spbstu.rakitin.commonstarter.dto;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public enum FieldType {
    DOUBLE("pdouble") {
        @Override
        public boolean isValueCompatible(String value, SchemaFieldDto schemaFieldDto) {
            try {
                Double.parseDouble(value);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        public FieldType[] compatibilityList() {
            return new FieldType[]{
                    LONG
            };
        }
    }, LONG("plong") {
        @Override
        public boolean isValueCompatible(String value, SchemaFieldDto schemaFieldDto) {
            try {
                Long.parseLong(value);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        public FieldType[] compatibilityList() {
            return new FieldType[]{
                    DOUBLE
            };
        }
    }, STRING("string") {
        @Override
        public boolean isValueCompatible(String value, SchemaFieldDto schemaFieldDto) {
            return true;
        }

        @Override
        public FieldType[] compatibilityList() {
            return new FieldType[]{
                    TEXT, DATE
            };
        }
    }, TEXT("text_general") {
        @Override
        public boolean isValueCompatible(String value, SchemaFieldDto schemaFieldDto) {
            return true;
        }

        @Override
        public FieldType[] compatibilityList() {
            return new FieldType[]{
                    STRING, DATE
            };
        }
    }, DATE("pdate") {
        @Override
        public boolean isValueCompatible(String value, SchemaFieldDto schemaFieldDto) {
            try {
                DateTimeFormatter.ISO_INSTANT.parse(value);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        public FieldType[] compatibilityList() {
            return new FieldType[]{
                    STRING, TEXT
            };
        }
    }, ARRAY("array") {
        @Override
        public boolean isValueCompatible(String value, SchemaFieldDto schemaFieldDto) {
            try {
                List<String> arrayValues = objectMapper.readValue(value, new TypeReference<List<String>>() {
                });
                return arrayValues.stream().anyMatch(val -> schemaFieldDto.getSubType().isValueCompatible(val, schemaFieldDto));
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        public FieldType[] compatibilityList() {
            return new FieldType[0];
        }
    },
    UNDEFINED("undefined") {
        @Override
        public boolean isValueCompatible(String value, SchemaFieldDto schemaFieldDto) {
            return false;
        }

        @Override
        public FieldType[] compatibilityList() {
            return new FieldType[0];
        }
    };

    private final String solrType;

    public abstract boolean isValueCompatible(String value, @Nullable SchemaFieldDto schemaFieldDto);
    public boolean isValueCompatible(String value) {
        return isValueCompatible(value, null);
    }

    public abstract FieldType[] compatibilityList();

    public boolean isCompatibleWith(FieldType fieldType) {
        return this.equals(fieldType)
                || Arrays.asList(compatibilityList()).contains(fieldType)
                || Arrays.asList(fieldType.compatibilityList()).contains(this);
    }


    public String getSolrType() {
        return solrType;
    }

    public String getSolrTypeArray() {
        return getSolrType() + "s";
    }

    protected final ObjectMapper objectMapper = new ObjectMapper();
}

