package ru.spbstu.rakitin.dto.monitoring;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.spbstu.rakitin.dto.TaskInformator;
import ru.spbstu.rakitin.dto.TaskInstanceResponse;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MonitoringTaskResponse implements TaskInformator {

    private long id;
    private MonitoringTaskConfigDto config;
    private TaskInstanceResponse instance;

    @Override
    public long getTopicId() {
        return config.getTopicId();
    }

    @Override
    public long getTaskId() {
        return id;
    }

    @Override
    public long getProjectId() {
        return config.getProjectId();
    }


}
