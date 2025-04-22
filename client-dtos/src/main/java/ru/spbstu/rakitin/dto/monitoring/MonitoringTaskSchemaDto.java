package ru.spbstu.rakitin.dto.monitoring;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.spbstu.rakitin.dto.FilterExpression;
import ru.spbstu.rakitin.dto.SchemaFieldDto;
import ru.spbstu.rakitin.dto.TaskSchemaDto;
import ru.spbstu.rakitin.dto.TimestampFieldDto;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class MonitoringTaskSchemaDto extends TaskSchemaDto {

    private List<String> tags;

    public MonitoringTaskSchemaDto(List<SchemaFieldDto> fields, TimestampFieldDto timestampField, FilterExpression filterExpression, List<String> tags) {
        super(fields, timestampField, filterExpression);
        this.tags = tags;
    }


}
