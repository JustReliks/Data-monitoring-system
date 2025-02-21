package ru.spbstu.rakitin.management.engine.processors;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.kstream.ForeachAction;
import ru.spbstu.rakitin.commonstarter.dto.TaskSchemaDto;
import ru.spbstu.rakitin.commonstarter.utils.MapJson;

@RequiredArgsConstructor
public class RemoveExtraFieldsAction implements ForeachAction<String, MapJson> {

    private final TaskSchemaDto schema;

    @Override
    public void apply(String key, MapJson value) {
        value.entrySet().removeIf(entry ->
                schema.getFields()
                        .stream()
                        .noneMatch(schemaFieldDto -> schemaFieldDto.getFieldName().equals(entry.getKey())));
    }
}
