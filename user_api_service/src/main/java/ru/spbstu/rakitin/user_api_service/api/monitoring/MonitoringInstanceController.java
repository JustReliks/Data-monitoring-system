package ru.spbstu.rakitin.user_api_service.api.monitoring;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.spbstu.rakitin.commonstarter.admin.aspect.LogController;
import ru.spbstu.rakitin.commonstarter.monitoring.MonitoringServiceManager;

@RestController
@RequestMapping("/api/v1/monitoring/instance")
@RequiredArgsConstructor
public class MonitoringInstanceController {

    private final MonitoringServiceManager monitoringServiceManager;

    @PostMapping("/resume/{configId}")
    @LogController
    public long resume(@PathVariable("configId") long configId, Authentication authentication) {
        return monitoringServiceManager.resume(configId, authentication);
    }

    @PostMapping("/suspend/{configId}")
    @LogController
    public void suspend(@PathVariable("configId") long configId, Authentication authentication) {
        monitoringServiceManager.suspendTask(configId, authentication);
    }

    @PutMapping("/suspend/{configId}")
    @LogController
    public void update(@PathVariable("configId") long configId, Authentication authentication) {
        monitoringServiceManager.update(configId, authentication);
    }

}
