package ru.spbstu.rakitin.monitoring_service.api.internal;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.spbstu.rakitin.commonstarter.dto.TaskStatus;
import ru.spbstu.rakitin.commonstarter.dto.monitoring.MonitoringJobDto;
import ru.spbstu.rakitin.monitoring_service.dto.MonitoringTaskConfigMapper;
import ru.spbstu.rakitin.monitoring_service.exception.MonitoringStatusWontChangedException;
import ru.spbstu.rakitin.monitoring_service.exception.MonitoringTaskInstanceNotFoundException;
import ru.spbstu.rakitin.monitoring_service.service.MonitoringTaskInstanceService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/internal/monitoring/instance")
public class MonitoringTaskInternalController {

    private final MonitoringTaskInstanceService monitoringTaskInstanceService;
    private final MonitoringTaskConfigMapper monitoringTaskConfigMapper;

    @PostMapping("/{taskId}/status/{status}")
    public void changeStatus(@PathVariable long taskId, @PathVariable TaskStatus status) throws MonitoringStatusWontChangedException, MonitoringTaskInstanceNotFoundException {
        monitoringTaskInstanceService.forceChangeMonitoringInstanceStatus(taskId, status);
    }

    @GetMapping("/status/{status}")
    public List<MonitoringJobDto> getAllTaskWithStatus(@PathVariable TaskStatus status) {
        return monitoringTaskInstanceService.findAllTaskInstancesWithStatus(status)
                .stream().map(monitoringTaskConfigMapper::mapMonitoringTaskConfigToJobDto).toList();
    }

}
