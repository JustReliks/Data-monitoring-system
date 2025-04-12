package ru.spbstu.rakitin.dto.monitoring;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.spbstu.rakitin.dto.JobDto;
import ru.spbstu.rakitin.dto.TaskSchemaDto;

@Getter
@Setter
@NoArgsConstructor
public class MonitoringJobDto extends JobDto<TaskSchemaDto> {

    private String organization;


    @Builder
    public MonitoringJobDto(long projectId, long instanceId, long topicId, String taskName, boolean needUpdate, TaskSchemaDto schema, String organization) {
        super(projectId, instanceId, topicId, taskName, needUpdate, schema);
        this.organization = organization;
    }
}
