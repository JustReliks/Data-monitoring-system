package ru.spbstu.rakitin.fulltext_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.spbstu.rakitin.commonstarter.dto.FieldType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SchemaField {

    private String fieldName;
    private FieldType fieldType;
    private FieldType subType;

}
