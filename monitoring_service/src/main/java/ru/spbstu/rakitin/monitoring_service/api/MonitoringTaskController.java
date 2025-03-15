package ru.spbstu.rakitin.monitoring_service.api;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.spbstu.rakitin.commonentites.model.PermissionTypeEnum;
import ru.spbstu.rakitin.commonstarter.admin.aspect.CheckPermission;
import ru.spbstu.rakitin.commonstarter.admin.aspect.LogController;
import ru.spbstu.rakitin.commonstarter.admin.aspect.ProjectIdContainer;
import ru.spbstu.rakitin.commonstarter.exception.ConfigAlreadyExists;
import ru.spbstu.rakitin.commonstarter.exception.InvalidSchemaException;
import ru.spbstu.rakitin.commonstarter.exception.QuotaExceededException;
import ru.spbstu.rakitin.commonstarter.exception.UnavailableTopicException;
import ru.spbstu.rakitin.monitoring_service.dto.MonitoringTaskConfigDto;
import ru.spbstu.rakitin.monitoring_service.dto.MonitoringTaskConfigMapper;
import ru.spbstu.rakitin.monitoring_service.service.MonitoringTaskConfigService;

@RestController
@RequestMapping("/api/v1/monitoring/config")
@RequiredArgsConstructor
public class MonitoringTaskController {

    private final MonitoringTaskConfigService monitoringTaskConfigService;
    private final MonitoringTaskConfigMapper monitoringTaskConfigMapper;

    @PostMapping("/create")
    @LogController
    @CheckPermission(permission = PermissionTypeEnum.FULL_TEXT_CREATE_TASK, userIdField = "authentication", projectIdField = "configDto")
    public long create(Authentication authentication, @RequestBody @ProjectIdContainer(innerFieldName = "projectId") MonitoringTaskConfigDto configDto) throws ConfigAlreadyExists, QuotaExceededException, UnavailableTopicException, InvalidSchemaException {
        return monitoringTaskConfigService.createConfig(monitoringTaskConfigMapper.mapDtoToMonitoringTaskConfig(configDto, authentication), authentication);
    }

}
