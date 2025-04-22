package ru.spbstu.rakitin.monitoring_service.model;

import lombok.Data;
import ru.spbstu.rakitin.dto.FilterExpression;

import java.util.List;

@Data
public class MonitoringTaskSchema {

    private List<SchemaField> schema;
    private TimestampField timestampField;
    private FilterExpression filter;
    private List<String> tags;

}
