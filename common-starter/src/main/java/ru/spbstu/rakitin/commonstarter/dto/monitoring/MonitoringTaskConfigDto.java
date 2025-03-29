package ru.spbstu.rakitin.commonstarter.dto.monitoring;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.spbstu.rakitin.commonstarter.dto.TaskSchemaDto;

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
