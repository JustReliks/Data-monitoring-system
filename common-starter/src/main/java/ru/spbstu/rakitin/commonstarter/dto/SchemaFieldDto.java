package ru.spbstu.rakitin.commonstarter.dto;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchemaFieldDto {

    private String fieldName;
    private FieldType fieldType;
    private FieldType subType;

    public enum FieldType {
        DOUBLE {
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
        }, LONG {
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
        }, STRING {
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
        }, TEXT {
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
        }, DATE {
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
        }, ARRAY {
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
        };

        public abstract boolean isValueCompatible(String value, SchemaFieldDto schemaFieldDto);

        public abstract FieldType[] compatibilityList();

        public boolean isCompatibleWith(FieldType fieldType) {
            return this.equals(fieldType)
                    || Arrays.asList(compatibilityList()).contains(fieldType)
                    || Arrays.asList(fieldType.compatibilityList()).contains(this);
        }

        protected final ObjectMapper objectMapper = new ObjectMapper();
    }

}
