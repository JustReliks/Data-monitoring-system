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

    private final MonitoringTaskInstanceService fulltextTaskInstanceService;

    @PostMapping("/resume/{configId}")
    @LogController
    public long resume(@PathVariable("configId") long configId, Authentication authentication) throws Exception {
        return fulltextTaskInstanceService.resume(configId, authentication);
    }

    @PostMapping("/suspend/{configId}")
    @LogController
    public void suspend(@PathVariable("configId") long configId, Authentication authentication) throws MonitoringStatusWontChangedException, MonitoringTaskConfigNotFoundException, MonitoringTaskInstanceNotFoundException {
        fulltextTaskInstanceService.suspendTask(configId, authentication);
    }

    @PutMapping("/suspend/{configId}")
    @LogController
    public void update(@PathVariable("configId") long configId, Authentication authentication) throws Exception {
        fulltextTaskInstanceService.update(configId, authentication);
    }

}
