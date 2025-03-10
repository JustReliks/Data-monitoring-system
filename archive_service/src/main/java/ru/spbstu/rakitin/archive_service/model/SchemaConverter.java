package ru.spbstu.rakitin.archive_service.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class SchemaConverter implements AttributeConverter<ArchiveTaskSchema, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(ArchiveTaskSchema attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArchiveTaskSchema convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, ArchiveTaskSchema.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
