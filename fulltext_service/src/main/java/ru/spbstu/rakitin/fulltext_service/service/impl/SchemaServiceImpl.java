package ru.spbstu.rakitin.fulltext_service.service.impl;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Service;
import ru.spbstu.rakitin.fulltext_service.engine.schema.SolrSchema;
import ru.spbstu.rakitin.fulltext_service.model.FulltextTaskSchema;
import ru.spbstu.rakitin.fulltext_service.model.SchemaField;
import ru.spbstu.rakitin.fulltext_service.service.SchemaService;

import java.io.*;
import java.util.*;

@Service
public class SchemaServiceImpl implements SchemaService {

    private static final String PATH_TO_DEFAULT_SCHEMA = "solr/config/managed-schema.xml";

    private final File defaultSchema;

    public SchemaServiceImpl() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File file = new File("managed-schema.xml");
        try (InputStream resourceAsStream = classLoader.getResourceAsStream(PATH_TO_DEFAULT_SCHEMA);
             OutputStream outputStream = new FileOutputStream(file)) {
            IOUtils.copy(Objects.requireNonNull(resourceAsStream), outputStream);
            defaultSchema = file;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public SolrSchema createSolrSchema(FulltextTaskSchema schema) {
        List<Map<String, Object>> fieldsDescriptions = new ArrayList<>(schema.getSchema().stream().map(schemaField -> {
            Map<String, Object> field = new HashMap<>();
            field.put("name", schemaField.getFieldName());
            field.put("stored", true);
            field.put("indexed", true);
            if (schemaField.getFieldType() == SchemaField.FieldType.ARRAY) {
                field.put("type", schemaField.getSubType().getSolrTypeArray());
                field.put("multiValued", true);
            } else {
                field.put("type", schemaField.getFieldType().getSolrType());
            }

            return field;
        }).toList());

        if (schema.getTimestampField().isUseInsertionDate()) {
            Map<String, Object> field = new HashMap<>();
            field.put("name", schema.getTimestampField().getFieldName());
            field.put("stored", true);
            field.put("indexed", true);
            field.put("type", SchemaField.FieldType.DATE.getSolrType());
            fieldsDescriptions.add(field);

        }


        return SolrSchema.builder()
                .defaultSchemaFile(defaultSchema)
                .fieldsDescriptions(fieldsDescriptions).build();
    }
}
