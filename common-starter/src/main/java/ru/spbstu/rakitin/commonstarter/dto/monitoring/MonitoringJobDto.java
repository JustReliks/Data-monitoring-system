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

    private String organization;


    @Builder
    public MonitoringJobDto(long projectId, long instanceId, long topicId, String taskName, TaskSchemaDto schema, String organization) {
        super(projectId, instanceId, topicId, taskName, schema);
        this.organization = organization;
    }
}
