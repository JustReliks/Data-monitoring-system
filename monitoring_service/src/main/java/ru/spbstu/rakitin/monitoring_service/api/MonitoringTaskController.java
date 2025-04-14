package ru.spbstu.rakitin.monitoring_service.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.spbstu.rakitin.commonentites.model.PermissionTypeEnum;
import ru.spbstu.rakitin.commonstarter.admin.aspect.CheckPermission;
import ru.spbstu.rakitin.commonstarter.admin.aspect.LogController;
import ru.spbstu.rakitin.commonstarter.admin.aspect.ProjectIdContainer;
import ru.spbstu.rakitin.commonstarter.exception.ConfigAlreadyExists;
import ru.spbstu.rakitin.commonstarter.exception.InvalidSchemaException;
import ru.spbstu.rakitin.commonstarter.exception.QuotaExceededException;
import ru.spbstu.rakitin.commonstarter.exception.UnavailableTopicException;
import ru.spbstu.rakitin.dto.monitoring.MonitoringTaskConfigDto;
import ru.spbstu.rakitin.dto.monitoring.MonitoringTaskResponse;
import ru.spbstu.rakitin.monitoring_service.dto.MonitoringTaskConfigMapper;
import ru.spbstu.rakitin.monitoring_service.exception.*;
import ru.spbstu.rakitin.monitoring_service.model.MonitoringTaskConfig;
import ru.spbstu.rakitin.monitoring_service.model.MonitoringTaskInstance;
import ru.spbstu.rakitin.monitoring_service.service.MonitoringTaskConfigService;
import ru.spbstu.rakitin.monitoring_service.service.MonitoringTaskInstanceService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/monitoring/config")
@RequiredArgsConstructor
public class MonitoringTaskController {

    private final MonitoringTaskConfigService monitoringTaskConfigService;
    private final MonitoringTaskInstanceService monitoringTaskInstanceService;
    private final MonitoringTaskConfigMapper monitoringTaskConfigMapper;

    @PostMapping("/create")
    @LogController
    @CheckPermission(permission = PermissionTypeEnum.FULL_TEXT_CREATE_TASK, userIdField = "authentication", projectIdField = "configDto")
    public long create(Authentication authentication, @RequestBody @ProjectIdContainer(innerFieldName = "projectId") MonitoringTaskConfigDto configDto) throws ConfigAlreadyExists, QuotaExceededException, UnavailableTopicException, InvalidSchemaException {
        return monitoringTaskConfigService.createConfig(monitoringTaskConfigMapper.mapDtoToMonitoringTaskConfig(configDto, authentication), authentication);
    }

    @GetMapping("/list")
    @LogController
    public List<MonitoringTaskResponse> list(Authentication authentication, @RequestParam List<Long> projects) {

        return monitoringTaskConfigService.findForProjects(projects, authentication)
                .stream().map(monitoringTaskConfig -> {
                    Optional<MonitoringTaskInstance> monitoringTaskInstance = monitoringTaskInstanceService.findByConfigIdOptionally(monitoringTaskConfig.getId());
                    return monitoringTaskConfigMapper.mapMonitoringTaskConfigAndInstanceToResponse(monitoringTaskConfig, monitoringTaskInstance);
                }).toList();
    }


    @GetMapping("/{taskId}")
    public MonitoringTaskResponse findById(@PathVariable long taskId, Authentication authentication) throws MonitoringTaskConfigNotFoundException {
        return monitoringTaskConfigMapper.mapMonitoringTaskConfigAndInstanceToResponse(monitoringTaskConfigService.findById(taskId, authentication), monitoringTaskInstanceService.findByConfigIdOptionally(taskId));
    }

    @GetMapping("/name/{taskName}")
    public MonitoringTaskResponse findByName(@PathVariable String taskName, @RequestParam("projectId") long projectId, Authentication authentication) throws MonitoringTaskConfigNotFoundException {
        MonitoringTaskConfig config = monitoringTaskConfigService.findByName(projectId, taskName, authentication);
        return monitoringTaskConfigMapper.mapMonitoringTaskConfigAndInstanceToResponse(config, monitoringTaskInstanceService.findByConfigIdOptionally(config.getId()));
    }


    @DeleteMapping("/{configId}/delete")
    @LogController
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(Authentication authentication, @PathVariable Long configId,
                       @RequestParam(required = false, defaultValue = "false") boolean forceDelete) throws MonitoringConfigDeletionForbiddenException, MonitoringTaskConfigNotFoundException, MonitoringStatusWontChangedException, MonitoringTaskInstanceNotFoundException {
        monitoringTaskConfigService.removeConfig(configId, forceDelete, authentication);
    }

    @PutMapping("/{configId}/update")
    @LogController
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateConfig(Authentication authentication, @PathVariable long configId, @RequestBody MonitoringTaskConfigDto configDto) throws InvalidSchemaException, MonitoringTaskConfigNotFoundException, MonitoringConfigUpdateException {
        monitoringTaskConfigService.updateConfig(configId, monitoringTaskConfigMapper.mapDtoToMonitoringTaskConfig(configDto, authentication), authentication);
    }

}
