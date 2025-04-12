package ru.spbstu.rakitin.archive_service.service.impl;

import org.springframework.stereotype.Service;
import ru.spbstu.rakitin.dto.FieldType;
import ru.spbstu.rakitin.dto.archive.ArchiveTaskSchemaDto;
import ru.spbstu.rakitin.commonstarter.exception.InvalidSchemaException;
import ru.spbstu.rakitin.commonstarter.service.impl.SchemaValidationServiceImpl;

@Service
public class ArchiveSchemaValidationServiceImpl extends SchemaValidationServiceImpl<ArchiveTaskSchemaDto> {

    @Override
    public void validateSchema(ArchiveTaskSchemaDto taskSchemaDto) throws InvalidSchemaException {
        super.validateSchema(taskSchemaDto);
        taskSchemaDto.getField(taskSchemaDto.getFilenameFieldName())
                .filter(schemaFieldDto -> schemaFieldDto.getFieldType() == FieldType.STRING)
                .orElseThrow(() -> new InvalidSchemaException(String.format("Field %s not found in schema or its type is not a string", taskSchemaDto.getFilenameFieldName())));
    }
}
