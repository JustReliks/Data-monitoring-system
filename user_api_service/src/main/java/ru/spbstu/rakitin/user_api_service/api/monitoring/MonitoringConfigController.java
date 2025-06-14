package ru.spbstu.rakitin.user_api_service.api.monitoring;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.spbstu.rakitin.commonstarter.admin.aspect.LogController;
import ru.spbstu.rakitin.commonstarter.admin.aspect.ProjectIdContainer;
import ru.spbstu.rakitin.commonstarter.monitoring.MonitoringServiceManager;
import ru.spbstu.rakitin.dto.monitoring.MonitoringTaskConfigDto;
import ru.spbstu.rakitin.dto.monitoring.MonitoringTaskResponse;

import java.util.List;

@RestController
@RequestMapping("/api/v1/monitoring/config")
@Tag(name = "8. Операции с конфигурациями задач мониторинга")
@RequiredArgsConstructor
public class MonitoringConfigController {

    private final MonitoringServiceManager monitoringServiceManager;

    @PostMapping("/create")
    @LogController
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(description = "Создание конфигурации задачи мониторинга")
    public long create(Authentication authentication, @RequestBody @ProjectIdContainer(innerFieldName = "projectId") MonitoringTaskConfigDto configDto) {
        return monitoringServiceManager.createConfig(configDto, authentication);
    }

    @GetMapping("/list")
    @LogController
    @Operation(description = "Запрос списка задач мониторинга")
    public List<MonitoringTaskResponse> list(Authentication authentication, @RequestParam List<Long> projects) {
        return monitoringServiceManager.list(projects, authentication);

    }

    @DeleteMapping("/{configId}/delete")
    @LogController
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(description = "Удаление конфигурации задачи мониторинга")
    public void delete(Authentication authentication, @PathVariable Long configId,
                       @RequestParam(required = false, defaultValue = "false") boolean forceDelete) {
        monitoringServiceManager.removeConfig(configId, forceDelete, authentication);
    }

    @PutMapping("/{configId}/update")
    @LogController
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(description = "Обновление конфигурации задачи мониторинга")
    public void updateConfig(Authentication authentication, @PathVariable long configId, @RequestBody MonitoringTaskConfigDto configDto) {
        monitoringServiceManager.updateConfig(configId, configDto, authentication);
    }

    @GetMapping("/{taskId}")
    @Operation(description = "Получение задачи мониторинга по индентификатору")
    public MonitoringTaskResponse findById(@PathVariable long taskId, Authentication authentication) {
        return monitoringServiceManager.findById(taskId, authentication);
    }

    @GetMapping("/name/{taskName}")
    @Operation(description = "Получение задачи мониторинга по имени")
    public MonitoringTaskResponse findByName(@PathVariable String taskName, @RequestParam("projectId") Long projectId, Authentication authentication) {
        return monitoringServiceManager.findByName(taskName, projectId, authentication);
    }


}
