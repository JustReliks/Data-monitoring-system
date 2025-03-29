package ru.spbstu.rakitin.commonstarter.dto.monitoring;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.spbstu.rakitin.commonstarter.dto.TaskInstanceResponse;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MonitoringTaskResponse {

    private long id;
    private MonitoringTaskConfigDto config;
    private TaskInstanceResponse instance;

}
