package ru.spbstu.rakitin.monitoring_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.spbstu.rakitin.commonentites.model.PermissionTypeEnum;
import ru.spbstu.rakitin.commonstarter.admin.AdminManager;
import ru.spbstu.rakitin.dto.TaskSchemaDto;
import ru.spbstu.rakitin.dto.TaskStatus;
import ru.spbstu.rakitin.commonstarter.exception.ConfigAlreadyExists;
import ru.spbstu.rakitin.commonstarter.exception.InvalidSchemaException;
import ru.spbstu.rakitin.commonstarter.exception.QuotaExceededException;
import ru.spbstu.rakitin.commonstarter.exception.UnavailableTopicException;
import ru.spbstu.rakitin.commonstarter.service.SchemaValidationService;
import ru.spbstu.rakitin.monitoring_service.dto.MonitoringTaskConfigMapper;
import ru.spbstu.rakitin.monitoring_service.exception.*;
import ru.spbstu.rakitin.monitoring_service.model.MonitoringTaskConfig;
import ru.spbstu.rakitin.monitoring_service.model.MonitoringTaskInstance;
import ru.spbstu.rakitin.monitoring_service.repository.MonitoringTaskConfigRepository;
import ru.spbstu.rakitin.monitoring_service.service.MonitoringTaskConfigService;
import ru.spbstu.rakitin.monitoring_service.service.MonitoringTaskInstanceService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MonitoringTaskConfigServiceImpl implements MonitoringTaskConfigService {

    private final SchemaValidationService<TaskSchemaDto> schemaValidationService;
    private final MonitoringTaskConfigRepository monitoringTaskConfigRepository;
    private final MonitoringTaskConfigMapper monitoringTaskConfigMapper;
    private final AdminManager adminManager;
    private final MonitoringTaskInstanceService monitoringTaskInstanceService;

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

    @Override
    public List<MonitoringTaskConfig> findForProjects(List<Long> projects, Authentication authentication) {
        projects.forEach(projectId -> {
            adminManager.checkAccessThrowable(authentication, projectId, PermissionTypeEnum.MONITORING_VIEW_TASK);
        });

        return monitoringTaskConfigRepository.findByProject_IdIn(projects);
    }

    @Override
    public void removeConfig(Long configId, boolean forceDelete, Authentication authentication) throws MonitoringTaskConfigNotFoundException, MonitoringConfigDeletionForbiddenException, MonitoringStatusWontChangedException, MonitoringTaskInstanceNotFoundException {
        MonitoringTaskConfig config = findById(configId, authentication);
        adminManager.checkAccessThrowable(authentication, config.getProject().getId(), PermissionTypeEnum.FULL_TEXT_MANAGE_TASK);
        Optional<MonitoringTaskInstance> monitoringTaskInstanceOptional = monitoringTaskInstanceService.findByConfigIdOptionally(configId);
        if (monitoringTaskInstanceOptional.isPresent()) {
            MonitoringTaskInstance monitoringTaskInstance = monitoringTaskInstanceOptional.get();
            if (!forceDelete) {
                throw new MonitoringConfigDeletionForbiddenException(String.format("Fulltext task config with id %s have instance with id %s with status %s. Delete it or use flag [forceDelete=true]",
                        config.getId(),
                        monitoringTaskInstance.getId(),
                        monitoringTaskInstance.getTaskStatus()));
            } else {
                log.info("Deleting fulltext task instance with id {}", monitoringTaskInstance.getId());
                monitoringTaskInstanceService.removeInstance(monitoringTaskInstance.getId(), authentication);
            }
        }

        monitoringTaskConfigRepository.deleteById(configId);

    }

    @Override
    public MonitoringTaskConfig findById(Long configId, Authentication authentication) throws MonitoringTaskConfigNotFoundException {
        MonitoringTaskConfig config = monitoringTaskConfigRepository.findById(configId).orElseThrow(() -> new MonitoringTaskConfigNotFoundException(String.format("Task config with id %s not found", configId)));
        adminManager.checkAccessThrowable(authentication, config.getProject().getId(), PermissionTypeEnum.MONITORING_VIEW_TASK);
        return config;
    }

    @Override
    public void updateConfig(long configId, MonitoringTaskConfig monitoringTaskConfig, Authentication authentication) throws MonitoringTaskConfigNotFoundException, InvalidSchemaException, MonitoringConfigUpdateException {
        MonitoringTaskConfig config = findById(configId, authentication);
        adminManager.checkAccessThrowable(authentication, config.getProject().getId(), PermissionTypeEnum.MONITORING_MANAGE_TASK);
        schemaValidationService.validateSchema(monitoringTaskConfigMapper.mapMonitoringSchemaToSchemaDto(monitoringTaskConfig.getSchema()));
        if (!monitoringTaskConfig.getProject().getId().equals(config.getProject().getId())) {
            throw new MonitoringConfigUpdateException("Can not change projects id");
        }
        if (!monitoringTaskConfig.getName().equals(config.getName())) {
            throw new MonitoringConfigUpdateException("Can not change task name");
        }
        config.setSchema(monitoringTaskConfig.getSchema());
        config.setTopic(monitoringTaskConfig.getTopic());
        config.setRetentionTimeSeconds(monitoringTaskConfig.getRetentionTimeSeconds());
        config.setShardGroupDurationSeconds(monitoringTaskConfig.getShardGroupDurationSeconds());
        Optional<MonitoringTaskInstance> instance = monitoringTaskInstanceService.findByConfigIdOptionally(configId);
        if (instance.isPresent()) {
            MonitoringTaskInstance monitoringTaskInstance = instance.get();
            if (monitoringTaskInstance.getTaskStatus() != TaskStatus.CREATED && monitoringTaskInstance.getTaskStatus() != TaskStatus.INITIATION_FAILED) {
                monitoringTaskInstance.setNeedUpdate(true);
                monitoringTaskInstanceService.saveInstance(monitoringTaskInstance);
            }
        }
        monitoringTaskConfigRepository.save(config);

    }
}
