package ru.spbstu.rakitin.monitoring_service.service.impl;

import org.springframework.stereotype.Service;
import ru.spbstu.rakitin.commonstarter.exception.InvalidSchemaException;
import ru.spbstu.rakitin.commonstarter.service.impl.SchemaValidationServiceImpl;
import ru.spbstu.rakitin.dto.monitoring.MonitoringTaskSchemaDto;

import java.util.List;

@Service
public class MonitoringSchemaValidationService extends SchemaValidationServiceImpl<MonitoringTaskSchemaDto> {

    @Override
    public void validateSchema(MonitoringTaskSchemaDto taskSchemaDto) throws InvalidSchemaException {
        super.validateSchema(taskSchemaDto);
        if (taskSchemaDto.getTags() == null) {
            return;
        }
        List<String> list = taskSchemaDto.getTags().stream()
                .filter(s ->
                        taskSchemaDto.getFields()
                                .stream()
                                .noneMatch(schemaFieldDto -> schemaFieldDto.getFieldName().equals(s))).toList();
        if (!list.isEmpty()) {
            throw new InvalidSchemaException(String.format("There are some fields in tags that do not exists in schema: %s", list));
        }
    }
}
