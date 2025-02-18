package ru.spbstu.rakitin.fulltext_service.model;

import lombok.Data;

import java.util.List;

@Data
public class FulltextTaskSchema {

    private List<SchemaField> schema;
    private TimestampField timestampField;

}
