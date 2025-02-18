package ru.spbstu.rakitin.fulltext_service.engine.schema;

import lombok.Builder;
import lombok.Data;

import java.io.File;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class SolrSchema {

    private final File defaultSchemaFile;
    private final List<Map<String, Object>> fieldsDescriptions;

}
