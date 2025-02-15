package ru.spbstu.rakitin.commonstarter.dto.fulltext;

import lombok.Data;

import java.time.format.DateTimeFormatter;

@Data
public class SchemaFieldDto {

    private String fieldName;
    private FieldType fieldType;
    private FieldType subType;

    public enum FieldType {
        DOUBLE {
            @Override
            public boolean isValueCompatible(String value) {
                try {
                    Double.parseDouble(value);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
        }, LONG {
            @Override
            public boolean isValueCompatible(String value) {
                try {
                    Long.parseLong(value);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
        }, STRING {
            @Override
            public boolean isValueCompatible(String value) {
                return true;
            }
        }, TEXT {
            @Override
            public boolean isValueCompatible(String value) {
                return true;
            }
        }, DATE {
            @Override
            public boolean isValueCompatible(String value) {
                try {
                    DateTimeFormatter.ISO_INSTANT.parse(value);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
        }, ARRAY {
            @Override
            public boolean isValueCompatible(String value) {
                return false;
            }
        };

        public abstract boolean isValueCompatible(String value);

    }

}
