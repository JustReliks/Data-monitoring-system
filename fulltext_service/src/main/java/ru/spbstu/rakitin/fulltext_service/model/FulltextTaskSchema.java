package ru.spbstu.rakitin.fulltext_service.model;

import lombok.Data;
import ru.spbstu.rakitin.dto.FilterExpression;

import java.util.List;

@Data
public class FulltextTaskSchema {

    private List<SchemaField> schema;
    private TimestampField timestampField;
    private FilterExpression filter;

}
