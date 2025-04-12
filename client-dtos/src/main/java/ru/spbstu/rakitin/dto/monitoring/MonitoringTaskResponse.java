package ru.spbstu.rakitin.dto.monitoring;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.spbstu.rakitin.dto.TaskInstanceResponse;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MonitoringTaskResponse {

    private long id;
    private MonitoringTaskConfigDto config;
    private TaskInstanceResponse instance;

}
