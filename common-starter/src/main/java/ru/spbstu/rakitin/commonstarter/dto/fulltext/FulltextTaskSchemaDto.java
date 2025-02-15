package ru.spbstu.rakitin.commonstarter.dto.fulltext;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FulltextTaskSchemaDto {
    private List<SchemaFieldDto> fields;
    private TimestampFieldDto timestampField;


}
