package ru.spbstu.rakitin.monitoring_service.api.instance;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.spbstu.rakitin.commonstarter.admin.aspect.LogController;
import ru.spbstu.rakitin.monitoring_service.exception.MonitoringStatusWontChangedException;
import ru.spbstu.rakitin.monitoring_service.exception.MonitoringTaskConfigNotFoundException;
import ru.spbstu.rakitin.monitoring_service.exception.MonitoringTaskInstanceNotFoundException;
import ru.spbstu.rakitin.monitoring_service.service.MonitoringTaskInstanceService;

@RestController
@RequestMapping("/api/v1/monitoring/instance")
@RequiredArgsConstructor
public class MonitoringTaskInstanceController {

    private final MonitoringTaskInstanceService monitoringTaskInstanceService;

    @PostMapping("/resume/{configId}")
    @LogController
    public long resume(@PathVariable("configId") long configId, Authentication authentication) throws Exception {
        return monitoringTaskInstanceService.resume(configId, authentication);
    }

    @PostMapping("/suspend/{configId}")
    @LogController
    public void suspend(@PathVariable("configId") long configId, Authentication authentication) throws MonitoringStatusWontChangedException, MonitoringTaskConfigNotFoundException, MonitoringTaskInstanceNotFoundException {
        monitoringTaskInstanceService.suspendTask(configId, authentication);
    }

    @PutMapping("/update/{configId}")
    @LogController
    public void update(@PathVariable("configId") long configId, Authentication authentication) throws Exception {
        monitoringTaskInstanceService.update(configId, authentication);
    }

}
