package ru.spbstu.rakitin.dto.monitoring;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.spbstu.rakitin.dto.TaskSchemaDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonitoringTaskConfigDto {

    private String name;
    private long projectId;
    private long topicId;
    private TaskSchemaDto schema;
    private int retentionTimeSeconds;
    private long shardGroupDurationSeconds;

}
