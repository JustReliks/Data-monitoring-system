package ru.spbstu.rakitin.commonstarter.dto.fulltext;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
        }, STRING {
            @Override
            public boolean isValueCompatible(String value, SchemaFieldDto schemaFieldDto) {
                return true;
            }
        }, TEXT {
            @Override
            public boolean isValueCompatible(String value, SchemaFieldDto schemaFieldDto) {
                return true;
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
        };

        public abstract boolean isValueCompatible(String value, SchemaFieldDto schemaFieldDto);

        protected final ObjectMapper objectMapper = new ObjectMapper();
    }

}
