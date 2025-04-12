package ru.spbstu.rakitin.management.engine.processors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.Predicate;
import ru.spbstu.rakitin.dto.TaskSchemaDto;
import ru.spbstu.rakitin.dto.MapJson;


@Slf4j
@RequiredArgsConstructor
public class SchemaCompatibleFilter implements Predicate<String, MapJson> {

    private final TaskSchemaDto schema;
    private final String taskName;

    @Override
    public boolean test(String key, MapJson value) {
        return schema.getFields().stream().allMatch(schemaFieldDto -> {
            Object fieldValue = value.get(schemaFieldDto.getFieldName());
            if (fieldValue != null && schemaFieldDto.getFieldType().isValueCompatible(fieldValue.toString(), schemaFieldDto)) {
                return true;
            }
            log.warn("[{}] Unable to find compatible field in json {} for schema field {}", taskName, value, schemaFieldDto.getFieldName());
            return false;
        });
    }
}
