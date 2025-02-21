package ru.spbstu.rakitin.commonstarter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskSchemaDto {
    private List<SchemaFieldDto> fields;
    private TimestampFieldDto timestampField;
    private FilterExpressionDto filterExpression;

    public Optional<SchemaFieldDto> getField(String fieldName) {
        Optional<SchemaFieldDto> findInFields = fields.stream().filter(schemaFieldDto -> schemaFieldDto.getFieldName().equals(fieldName)).findAny();
        if (findInFields.isEmpty()) {
            findInFields = Optional.ofNullable(timestampField).stream().filter(timestampFieldDto -> timestampFieldDto.getFieldName().equals(fieldName)).findAny()
                    .map(timestampFieldDto -> SchemaFieldDto.builder()
                            .fieldName(fieldName)
                            .fieldType(SchemaFieldDto.FieldType.DATE).build());
        }
        return findInFields;
    }


}
