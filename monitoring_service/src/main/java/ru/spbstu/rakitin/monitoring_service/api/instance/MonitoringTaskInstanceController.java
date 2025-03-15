package ru.spbstu.rakitin.monitoring_service.api.instance;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.spbstu.rakitin.commonstarter.admin.aspect.LogController;
import ru.spbstu.rakitin.commonstarter.exception.InstanceInitiationFailedException;
import ru.spbstu.rakitin.fulltext_service.service.FulltextTaskInstanceService;
import ru.spbstu.rakitin.monitoring_service.exception.MonitoringTaskConfigNotFoundException;
import ru.spbstu.rakitin.monitoring_service.exception.MonitoringTaskResumeException;
import ru.spbstu.rakitin.monitoring_service.service.MonitoringTaskInstanceService;

@RestController
@RequestMapping("/api/v1/monitoring/instance")
@RequiredArgsConstructor
public class MonitoringTaskInstanceController {

    private final MonitoringTaskInstanceService fulltextTaskInstanceService;

    @PostMapping("/resume/{configId}")
    @LogController
    public long resume(@PathVariable("configId") long configId, Authentication authentication) throws MonitoringTaskConfigNotFoundException, MonitoringTaskResumeException {
        return fulltextTaskInstanceService.resume(configId, authentication);
    }

    @PostMapping("/suspend/{configId}")
    @LogController
    public void suspend(@PathVariable("configId") long configId, Authentication authentication)  {
        fulltextTaskInstanceService.suspendTask(configId, authentication);
    }

}
