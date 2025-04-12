package ru.spbstu.rakitin.monitoring_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.spbstu.rakitin.dto.FieldType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SchemaField {

    private String fieldName;
    private FieldType fieldType;
    private FieldType subType;

}
