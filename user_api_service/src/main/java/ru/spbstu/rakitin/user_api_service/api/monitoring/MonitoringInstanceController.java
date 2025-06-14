package ru.spbstu.rakitin.user_api_service.api.monitoring;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.spbstu.rakitin.commonstarter.admin.aspect.LogController;
import ru.spbstu.rakitin.commonstarter.monitoring.MonitoringServiceManager;

@RestController
@RequestMapping("/api/v1/monitoring/instance")
@RequiredArgsConstructor
@Tag(name = "9. Операции с экземплярами задач мониторинга")
public class MonitoringInstanceController {

    private final MonitoringServiceManager monitoringServiceManager;

    @PostMapping("/resume/{configId}")
    @LogController
    @Operation(description = "Запуск задачи мониторинга")
    public long resume(@PathVariable("configId") long configId, Authentication authentication) {
        return monitoringServiceManager.resume(configId, authentication);
    }

    @PostMapping("/suspend/{configId}")
    @LogController
    @Operation(description = "Остановка задачи мониторинга")
    public void suspend(@PathVariable("configId") long configId, Authentication authentication) {
        monitoringServiceManager.suspendTask(configId, authentication);
    }

    @PutMapping("/update/{configId}")
    @LogController
    @Operation(description = "Обновление задачи мониторинга")
    public void update(@PathVariable("configId") long configId, Authentication authentication) {
        monitoringServiceManager.update(configId, authentication);
    }

}
