package ru.spbstu.rakitin.fulltext_service.service;

import ru.spbstu.rakitin.fulltext_service.engine.schema.SolrSchema;
import ru.spbstu.rakitin.fulltext_service.model.FulltextTaskSchema;

public interface SolrSchemaService {

    SolrSchema createSolrSchema(FulltextTaskSchema schema);

}
