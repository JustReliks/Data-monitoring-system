package ru.spbstu.rakitin.commonstarter.service;

import ru.spbstu.rakitin.commonstarter.dto.TaskSchemaDto;
import ru.spbstu.rakitin.commonstarter.exception.InvalidSchemaException;

public interface SchemaValidationService<T extends TaskSchemaDto> {

    void validateSchema(T taskSchemaDto) throws InvalidSchemaException;

}
