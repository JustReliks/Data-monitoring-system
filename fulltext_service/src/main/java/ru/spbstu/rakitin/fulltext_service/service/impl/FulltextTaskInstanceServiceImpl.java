package ru.spbstu.rakitin.fulltext_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.spbstu.rakitin.commonentites.model.PermissionTypeEnum;
import ru.spbstu.rakitin.commonstarter.admin.AdminManager;
import ru.spbstu.rakitin.commonstarter.datamanagement.DataManagementManager;
import ru.spbstu.rakitin.commonstarter.dto.JobNameDto;
import ru.spbstu.rakitin.fulltext_service.dto.FulltextTaskConfigMapper;
import ru.spbstu.rakitin.fulltext_service.engine.SolrClientManager;
import ru.spbstu.rakitin.fulltext_service.exception.*;
import ru.spbstu.rakitin.fulltext_service.model.FulltextTaskConfig;
import ru.spbstu.rakitin.fulltext_service.model.FulltextTaskInstance;
import ru.spbstu.rakitin.fulltext_service.model.TaskStatus;
import ru.spbstu.rakitin.fulltext_service.repository.FulltextTaskInstanceRepository;
import ru.spbstu.rakitin.fulltext_service.service.FulltextTaskConfigService;
import ru.spbstu.rakitin.fulltext_service.service.FulltextTaskInstanceService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FulltextTaskInstanceServiceImpl implements FulltextTaskInstanceService {

    private final FulltextTaskConfigService fulltextTaskConfigService;
    private final FulltextTaskInstanceRepository fulltextTaskInstanceRepository;
    private final AdminManager adminManager;
    private final SolrClientManager solrClientManager;
    private final DataManagementManager dataManagementManager;
    private final FulltextTaskConfigMapper fulltextTaskConfigMapper;


    @Override
    public void suspendTask(long configId, Authentication authentication) throws FulltextTaskInstanceNotFoundException, FulltextStatusWontChangedException {
        FulltextTaskInstance instance = fulltextTaskInstanceRepository.findByConfigId(configId)
                .orElseThrow(() -> new FulltextTaskInstanceNotFoundException("Task instance with id %s not found!"));
        FulltextTaskConfig config = instance.getConfig();
        adminManager.checkAccessThrowable(authentication, config.getProject().getId(), PermissionTypeEnum.FULL_TEXT_MANAGE_TASK);
        if (instance.getTaskStatus() != TaskStatus.RUNNING) {
            throw new FulltextStatusWontChangedException(String.format("Can't change status for task with id %s to %s because its status already equal to it", instance.getId(), TaskStatus.SUSPENDED));
        }
        dataManagementManager.stopFulltextJob(JobNameDto.builder()
                .projectName(config.getProject().getProjectName())
                .taskName(config.getName()).build(), authentication);
        forceChangeFulltextInstanceStatus(instance.getId(), TaskStatus.SUSPENDED);
    }

    @Override
    public void resume(long configId, Authentication authentication)
            throws FulltextConfigNotFoundException,
            FulltextTaskInstanceAlreadyRunningException,
            InstanceInitiationFailedException {
        FulltextTaskConfig config = fulltextTaskConfigService.findById(configId);
        Optional<FulltextTaskInstance> taskOptional = fulltextTaskInstanceRepository.findByConfigId(configId);
        FulltextTaskInstance taskInstance;
        if (taskOptional.isPresent()) {
            adminManager.checkAccessThrowable(authentication, config.getProject().getId(), PermissionTypeEnum.FULL_TEXT_MANAGE_TASK);
            taskInstance = taskOptional.get();
        } else {
            adminManager.checkAccessThrowable(authentication, config.getProject().getId(), PermissionTypeEnum.FULL_TEXT_CREATE_TASK);
            taskInstance = new FulltextTaskInstance();
            taskInstance.setTaskStatus(TaskStatus.CREATED);
            taskInstance.setConfig(config);
            taskInstance = fulltextTaskInstanceRepository.save(taskInstance);

        }

        if (taskInstance.getTaskStatus() == TaskStatus.RUNNING) {
            throw new FulltextTaskInstanceAlreadyRunningException(String.format("Fulltext task instance for config with id %s already running", configId));
        }
        if (taskInstance.getTaskStatus() == TaskStatus.CREATED || taskInstance.getTaskStatus() == TaskStatus.INITIATION_FAILED) {
            try {
                solrClientManager.initiateFulltextInstance(config);
            } catch (Exception e) {
                taskInstance.setTaskStatus(TaskStatus.INITIATION_FAILED);
                fulltextTaskInstanceRepository.save(taskInstance);
                throw new InstanceInitiationFailedException("Initiation of instance with configId " + config.getId() + " was failed!", e);
            }
            taskInstance.setTaskStatus(TaskStatus.INITIATED);
            fulltextTaskInstanceRepository.save(taskInstance);

        }

        try {
            dataManagementManager.startFulltextJob(fulltextTaskConfigMapper.mapFulltextTaskConfigToJobDto(taskInstance), authentication);
            taskInstance.setTaskStatus(TaskStatus.RUNNING);
            fulltextTaskInstanceRepository.save(taskInstance);
        } catch (Exception e) {
            taskInstance.setTaskStatus(TaskStatus.FAILED);
            fulltextTaskInstanceRepository.save(taskInstance);
            throw e;
        }

    }

    @Override
    public void forceChangeFulltextInstanceStatus(long instanceId, TaskStatus taskStatus) throws FulltextTaskInstanceNotFoundException, FulltextStatusWontChangedException {
        FulltextTaskInstance instance = fulltextTaskInstanceRepository.findById(instanceId).orElseThrow(() -> new FulltextTaskInstanceNotFoundException(String.format("Fulltext task instance with id %s not found!", instanceId)));
        if (instance.getTaskStatus() == taskStatus) {
            throw new FulltextStatusWontChangedException(String.format("Can't change status for task with id %s to %s because its status already equal to it", instanceId, taskStatus));
        }
        log.warn("Force change status of fulltext task with id {} from {} to {}",
                instanceId,
                instance.getTaskStatus(),
                taskStatus);
        instance.setTaskStatus(taskStatus);
        fulltextTaskInstanceRepository.save(instance);
    }

    @Override
    public List<FulltextTaskInstance> findAllTaskInstancesWithStatus(TaskStatus taskStatus) {
        return fulltextTaskInstanceRepository.findAllByTaskStatus(taskStatus);
    }
}
