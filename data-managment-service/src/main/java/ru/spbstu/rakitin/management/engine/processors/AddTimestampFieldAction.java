package ru.spbstu.rakitin.management.engine.processors;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.kstream.ForeachAction;
import ru.spbstu.rakitin.dto.TaskSchemaDto;
import ru.spbstu.rakitin.dto.MapJson;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
public class AddTimestampFieldAction implements ForeachAction<String, MapJson> {

    private final TaskSchemaDto schema;


    @Override
    public void apply(String key, MapJson value) {
        if (schema.getTimestampField().isUseInsertionDate()) {
            value.put(schema.getTimestampField().getFieldName(), DateTimeFormatter.ISO_INSTANT.format(ZonedDateTime.now()));
        }
    }
}
