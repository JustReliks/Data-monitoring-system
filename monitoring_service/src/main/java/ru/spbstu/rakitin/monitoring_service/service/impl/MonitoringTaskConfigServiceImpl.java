package ru.spbstu.rakitin.monitoring_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.spbstu.rakitin.commonentites.model.PermissionTypeEnum;
import ru.spbstu.rakitin.commonstarter.admin.AdminManager;
import ru.spbstu.rakitin.commonstarter.dto.TaskSchemaDto;
import ru.spbstu.rakitin.commonstarter.exception.ConfigAlreadyExists;
import ru.spbstu.rakitin.commonstarter.exception.InvalidSchemaException;
import ru.spbstu.rakitin.commonstarter.exception.QuotaExceededException;
import ru.spbstu.rakitin.commonstarter.exception.UnavailableTopicException;
import ru.spbstu.rakitin.commonstarter.service.SchemaValidationService;
import ru.spbstu.rakitin.monitoring_service.dto.MonitoringTaskConfigMapper;
import ru.spbstu.rakitin.monitoring_service.exception.MonitoringTaskConfigNotFoundException;
import ru.spbstu.rakitin.monitoring_service.model.MonitoringTaskConfig;
import ru.spbstu.rakitin.monitoring_service.repository.MonitoringTaskConfigRepository;
import ru.spbstu.rakitin.monitoring_service.service.MonitoringTaskConfigService;

@Service
@RequiredArgsConstructor
public class MonitoringTaskConfigServiceImpl implements MonitoringTaskConfigService {

    private final SchemaValidationService<TaskSchemaDto> schemaValidationService;
    private final MonitoringTaskConfigRepository monitoringTaskConfigRepository;
    private final MonitoringTaskConfigMapper monitoringTaskConfigMapper;
    private final AdminManager adminManager;

    @Override
    public long createConfig(MonitoringTaskConfig config, Authentication authentication) throws ConfigAlreadyExists, QuotaExceededException, UnavailableTopicException, InvalidSchemaException {
        if (monitoringTaskConfigRepository.existsByNameAndProjectId(config.getName(), config.getProject().getId())) {
            throw new ConfigAlreadyExists(String.format("Config with name %s already exists in project %s", config.getName(), config.getProject().getProjectName()));
        }
        if (config.getProject().getMonitoringQuota() <= monitoringTaskConfigRepository.countByProjectId(config.getProject().getId())) {
            throw new QuotaExceededException("Monitoring quota exceeded for project " + config.getProject().getProjectName());
        }
        if (!config.getTopic().getProject().getId().equals(config.getProject().getId())) {
            throw new UnavailableTopicException("The topic for the task must be from the same project as the task itself.");
        }
        try {
            schemaValidationService.validateSchema(monitoringTaskConfigMapper.mapMonitoringSchemaToSchemaDto(config.getSchema()));
        } catch (InvalidSchemaException invalidSchemaException) {
            throw new InvalidSchemaException(String.format("Unable to create config for fulltext task %s because schema is invalid!", config.getName()), invalidSchemaException);
        }
        return monitoringTaskConfigRepository.save(config).getId();
    }

    @Override
    public MonitoringTaskConfig getConfig(long id, Authentication authentication) throws MonitoringTaskConfigNotFoundException {
        MonitoringTaskConfig monitoringTaskConfig = monitoringTaskConfigRepository.findById(id).orElseThrow(() -> new MonitoringTaskConfigNotFoundException(String.format("Monitoring task config with %s not found", id)));
        Long projectId = monitoringTaskConfig.getProject().getId();

        adminManager.checkAccessThrowable(authentication, projectId, PermissionTypeEnum.MONITORING_VIEW_TASK);
        return monitoringTaskConfig;
    }
}
