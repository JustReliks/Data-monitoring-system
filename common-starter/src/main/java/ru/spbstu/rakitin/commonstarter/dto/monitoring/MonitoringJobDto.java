package ru.spbstu.rakitin.commonstarter.dto.monitoring;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.spbstu.rakitin.commonstarter.dto.JobDto;
import ru.spbstu.rakitin.commonstarter.dto.TaskSchemaDto;

@Getter
@Setter
@NoArgsConstructor
public class MonitoringJobDto extends JobDto<TaskSchemaDto> {


    @Builder
    public MonitoringJobDto(long projectId, long instanceId, long topicId, String taskName, TaskSchemaDto schema) {
        super(projectId, instanceId, topicId, taskName, schema);
    }
}
