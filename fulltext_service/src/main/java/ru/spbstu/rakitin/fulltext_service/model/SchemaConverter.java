package ru.spbstu.rakitin.fulltext_service.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class SchemaConverter implements AttributeConverter<FulltextTaskSchema, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(FulltextTaskSchema attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public FulltextTaskSchema convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, FulltextTaskSchema.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
