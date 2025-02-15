package ru.spbstu.rakitin.commonstarter.dto.fulltext;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FulltextTaskSchemaDto {
    private List<SchemaFieldDto> fields;
    private TimestampFieldDto timestampField;


}
