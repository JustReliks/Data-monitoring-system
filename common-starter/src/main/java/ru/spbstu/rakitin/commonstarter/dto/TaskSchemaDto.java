package ru.spbstu.rakitin.commonstarter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskSchemaDto {
    private List<SchemaFieldDto> fields;
    private TimestampFieldDto timestampField;


}
