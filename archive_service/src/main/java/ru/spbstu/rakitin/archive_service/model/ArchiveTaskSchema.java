package ru.spbstu.rakitin.archive_service.model;

import lombok.Data;
import ru.spbstu.rakitin.dto.FilterExpression;
import ru.spbstu.rakitin.dto.SchemaFieldDto;
import ru.spbstu.rakitin.dto.TimestampFieldDto;

import java.util.List;

@Data
public class ArchiveTaskSchema {

    private List<SchemaFieldDto> schema;
    private TimestampFieldDto timestampField;
    private FilterExpression filter;
    private String filenameFieldName;
    private String directoryFieldName;

}
