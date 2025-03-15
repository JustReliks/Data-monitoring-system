package ru.spbstu.rakitin.monitoring_service.dto;

import lombok.Data;
import ru.spbstu.rakitin.commonstarter.dto.TaskSchemaDto;

@Data
public class MonitoringTaskConfigDto {

    private String name;
    private long projectId;
    private long topicId;
    private TaskSchemaDto schema;

}
