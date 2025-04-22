package ru.spbstu.rakitin.dto.monitoring;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonitoringTaskConfigDto {

    private String name;
    private long projectId;
    private long topicId;
    private MonitoringTaskSchemaDto schema;
    private int retentionTimeSeconds;
    private long shardGroupDurationSeconds;

}
