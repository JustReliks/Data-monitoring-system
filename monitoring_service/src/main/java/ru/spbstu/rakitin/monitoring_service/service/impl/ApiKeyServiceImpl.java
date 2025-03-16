package ru.spbstu.rakitin.monitoring_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.spbstu.rakitin.commonentites.model.PermissionTypeEnum;
import ru.spbstu.rakitin.commonentites.model.Project;
import ru.spbstu.rakitin.commonstarter.admin.AdminManager;
import ru.spbstu.rakitin.monitoring_service.dto.CreateReadApiKeyDto;
import ru.spbstu.rakitin.monitoring_service.engine.InfluxDBManager;
import ru.spbstu.rakitin.monitoring_service.exception.OrganizationNotFoundException;
import ru.spbstu.rakitin.monitoring_service.model.MonitoringTaskInstance;
import ru.spbstu.rakitin.monitoring_service.service.ApiKeyService;
import ru.spbstu.rakitin.monitoring_service.service.MonitoringTaskInstanceService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiKeyServiceImpl implements ApiKeyService {

    private final InfluxDBManager influxDBManager;
    private final AdminManager adminManager;
    private final MonitoringTaskInstanceService monitoringTaskInstanceService;

    @Override
    public String createApiKey(CreateReadApiKeyDto readApiKeyDto, Authentication authentication) throws OrganizationNotFoundException {
        Project project = adminManager.findProjectById(readApiKeyDto.getProjectId());
        adminManager.checkAccessThrowable(authentication, readApiKeyDto.getProjectId(), PermissionTypeEnum.MONITORING_CREATE_API_KEY);
        List<MonitoringTaskInstance> configs = monitoringTaskInstanceService.findAllByConfigIds(readApiKeyDto.getTasks());
        readApiKeyDto.getTasks().stream().filter(taskId -> configs.stream().noneMatch(monitoringTaskInstance -> monitoringTaskInstance.getId().equals(taskId)))
                .forEach(taskId -> log.warn("Task with id {} not found", taskId));
        return influxDBManager.createReadApiKey(readApiKeyDto.getDescription(), project.getProjectName(), configs.stream().map(monitoringTaskInstance -> monitoringTaskInstance.getConfig().getName()).toList());
    }
}
