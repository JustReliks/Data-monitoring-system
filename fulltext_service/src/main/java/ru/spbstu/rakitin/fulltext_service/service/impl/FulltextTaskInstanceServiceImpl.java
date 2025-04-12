package ru.spbstu.rakitin.fulltext_service.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.spbstu.rakitin.commonentites.model.PermissionTypeEnum;
import ru.spbstu.rakitin.commonstarter.admin.AdminManager;
import ru.spbstu.rakitin.commonstarter.datamanagement.DataManagementManager;
import ru.spbstu.rakitin.dto.JobNameDto;
import ru.spbstu.rakitin.dto.TaskStatus;
import ru.spbstu.rakitin.commonstarter.exception.InstanceInitiationFailedException;
import ru.spbstu.rakitin.fulltext_service.dto.FulltextTaskConfigMapper;
import ru.spbstu.rakitin.fulltext_service.engine.SolrClientManager;
import ru.spbstu.rakitin.fulltext_service.exception.*;
import ru.spbstu.rakitin.fulltext_service.model.FulltextTaskConfig;
import ru.spbstu.rakitin.fulltext_service.model.FulltextTaskInstance;
import ru.spbstu.rakitin.fulltext_service.repository.FulltextTaskInstanceRepository;
import ru.spbstu.rakitin.fulltext_service.service.FulltextTaskConfigService;
import ru.spbstu.rakitin.fulltext_service.service.FulltextTaskInstanceService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class FulltextTaskInstanceServiceImpl implements FulltextTaskInstanceService {

    private FulltextTaskConfigService fulltextTaskConfigService;
    private final FulltextTaskInstanceRepository fulltextTaskInstanceRepository;
    private final AdminManager adminManager;
    private final SolrClientManager solrClientManager;
    private final DataManagementManager dataManagementManager;
    private final FulltextTaskConfigMapper fulltextTaskConfigMapper;

    public FulltextTaskInstanceServiceImpl(FulltextTaskInstanceRepository fulltextTaskInstanceRepository, AdminManager adminManager, SolrClientManager solrClientManager, DataManagementManager dataManagementManager, FulltextTaskConfigMapper fulltextTaskConfigMapper) {
        this.fulltextTaskInstanceRepository = fulltextTaskInstanceRepository;
        this.adminManager = adminManager;
        this.solrClientManager = solrClientManager;
        this.dataManagementManager = dataManagementManager;
        this.fulltextTaskConfigMapper = fulltextTaskConfigMapper;
    }

    @Autowired
    public void setFulltextTaskConfigService(@Lazy FulltextTaskConfigService fulltextTaskConfigService) {
        this.fulltextTaskConfigService = fulltextTaskConfigService;
    }


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
    public long resume(long configId, Authentication authentication)
            throws FulltextConfigNotFoundException,
            InstanceInitiationFailedException, FulltextTaskInstanceResumeException {
        FulltextTaskConfig config = fulltextTaskConfigService.findById(configId, authentication);
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
            throw new FulltextTaskInstanceResumeException(String.format("Fulltext task instance for config with id %s already running", configId));
        }
        if (taskInstance.getTaskStatus() == TaskStatus.CREATED || taskInstance.getTaskStatus() == TaskStatus.INITIATION_FAILED) {
            try {
                solrClientManager.initiateFulltextInstance(config);
                if (taskInstance.isNeedUpdate()) {
                    taskInstance.setNeedUpdate(false);
                }
            } catch (Exception e) {
                taskInstance.setTaskStatus(TaskStatus.INITIATION_FAILED);
                fulltextTaskInstanceRepository.save(taskInstance);
                throw new InstanceInitiationFailedException("Initiation of instance with configId " + config.getId() + " was failed!", e);
            }
            taskInstance.setTaskStatus(TaskStatus.INITIATED);
            fulltextTaskInstanceRepository.save(taskInstance);
        } else if (taskInstance.isNeedUpdate()) {
            log.warn("Starting fulltext task instance for config with id {} with outdated configuration.", configId);
        }

        try {
            dataManagementManager.startFulltextJob(fulltextTaskConfigMapper.mapFulltextTaskConfigToJobDto(taskInstance), authentication);
            taskInstance.setTaskStatus(TaskStatus.RUNNING);
            return fulltextTaskInstanceRepository.save(taskInstance).getId();
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
        fulltextTaskInstanceRepository.saveAndFlush(instance);
    }

    @Override
    public void update(long configId, Authentication authentication) throws Exception {
        FulltextTaskInstance instance = findByConfigId(configId);
        if (instance.getTaskStatus() == TaskStatus.CREATED || instance.getTaskStatus() == TaskStatus.INITIATION_FAILED) {
            throw new FulltextTaskInstanceUpdateException("Cant update not initiated task");
        }
        boolean running = instance.getTaskStatus() == TaskStatus.RUNNING;
        if (running) {
            suspendTask(configId, authentication);
        }
        solrClientManager.updateFulltextTaskInstance(instance.getConfig());
        instance.setNeedUpdate(false);
        update(instance);
        if (running) {
            resume(configId, authentication);
        }
    }

    @Override
    public List<FulltextTaskInstance> findAllTaskInstancesWithStatus(TaskStatus taskStatus) {
        return fulltextTaskInstanceRepository.findAllByTaskStatus(taskStatus);
    }

    @Override
    public FulltextTaskInstance findByConfigId(long configId) throws FulltextTaskInstanceNotFoundException {
        return fulltextTaskInstanceRepository.findByConfigId(configId).orElseThrow(() -> new FulltextTaskInstanceNotFoundException(String.format("Instance for config id %s not found!", configId)));
    }

    @Override
    public Optional<FulltextTaskInstance> findByConfigIdOptionally(long configId) {
        return fulltextTaskInstanceRepository.findByConfigId(configId);
    }

    @Override
    public FulltextTaskInstance findById(long id) throws FulltextTaskInstanceNotFoundException {
        return fulltextTaskInstanceRepository.findById(id).orElseThrow(() -> new FulltextTaskInstanceNotFoundException(String.format("Instance with %s not found!", id)));
    }

    @Override
    public void removeInstance(Long id, Authentication authentication) throws FulltextTaskInstanceNotFoundException, FulltextStatusWontChangedException, SolrServerException, IOException {
        FulltextTaskInstance instance = findById(id);
        TaskStatus taskStatus = instance.getTaskStatus();
        if (taskStatus == TaskStatus.RUNNING) {
            suspendTask(instance.getConfig().getId(), authentication);
        }
        if (taskStatus != TaskStatus.CREATED && taskStatus != TaskStatus.INITIATION_FAILED) {
            solrClientManager.removeFulltextTaskInstance(instance.getConfig());
        }
        fulltextTaskInstanceRepository.deleteById(id);
    }

    @Override
    public void update(FulltextTaskInstance taskInstance) throws FulltextTaskInstanceNotFoundException {
        findById(taskInstance.getId());
        fulltextTaskInstanceRepository.save(taskInstance);
    }
}
