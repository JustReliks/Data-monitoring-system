package ru.spbstu.rakitin.fulltext_service.service;

import ru.spbstu.rakitin.fulltext_service.engine.schema.SolrSchema;
import ru.spbstu.rakitin.fulltext_service.model.FulltextTaskSchema;

import java.io.File;

public interface SchemaService {

    SolrSchema createSolrSchema(FulltextTaskSchema schema);

}
