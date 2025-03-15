package ru.spbstu.rakitin.monitoring_service.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class SchemaConverter implements AttributeConverter<MonitoringTaskSchema, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(MonitoringTaskSchema attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public MonitoringTaskSchema convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, MonitoringTaskSchema.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
