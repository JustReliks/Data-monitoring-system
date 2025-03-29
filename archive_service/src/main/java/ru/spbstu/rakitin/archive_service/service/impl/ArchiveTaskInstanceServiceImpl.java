package ru.spbstu.rakitin.archive_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.spbstu.rakitin.archive_service.dto.ArchiveTaskConfigMapper;
import ru.spbstu.rakitin.archive_service.engine.HdfsManager;
import ru.spbstu.rakitin.archive_service.exception.ArchiveInstanceUpdateException;
import ru.spbstu.rakitin.archive_service.exception.ArchiveStatusWontChangedException;
import ru.spbstu.rakitin.archive_service.exception.ArchiveTaskInstanceNotFoundException;
import ru.spbstu.rakitin.archive_service.exception.ArchiveTaskInstanceResumeException;
import ru.spbstu.rakitin.archive_service.model.ArchiveTaskConfig;
import ru.spbstu.rakitin.archive_service.model.ArchiveTaskInstance;
import ru.spbstu.rakitin.archive_service.repository.ArchiveTaskInstanceRepository;
import ru.spbstu.rakitin.archive_service.service.ArchiveTaskConfigService;
import ru.spbstu.rakitin.archive_service.service.ArchiveTaskInstanceService;
import ru.spbstu.rakitin.commonentites.model.PermissionTypeEnum;
import ru.spbstu.rakitin.commonstarter.admin.AdminManager;
import ru.spbstu.rakitin.commonstarter.datamanagement.DataManagementManager;
import ru.spbstu.rakitin.commonstarter.dto.JobNameDto;
import ru.spbstu.rakitin.commonstarter.dto.TaskStatus;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ArchiveTaskInstanceServiceImpl implements ArchiveTaskInstanceService {

    private final ArchiveTaskInstanceRepository archiveTaskInstanceRepository;
    private final ArchiveTaskConfigMapper archiveTaskConfigMapper;
    private ArchiveTaskConfigService archiveTaskConfigService;
    private final HdfsManager hdfsManager;
    private final AdminManager adminManager;
    private final DataManagementManager dataManagementManager;

    public ArchiveTaskInstanceServiceImpl(ArchiveTaskInstanceRepository archiveTaskInstanceRepository, ArchiveTaskConfigMapper archiveTaskConfigMapper, HdfsManager hdfsManager, AdminManager adminManager, DataManagementManager dataManagementManager) {
        this.archiveTaskInstanceRepository = archiveTaskInstanceRepository;
        this.archiveTaskConfigMapper = archiveTaskConfigMapper;
        this.hdfsManager = hdfsManager;
        this.adminManager = adminManager;
        this.dataManagementManager = dataManagementManager;
    }

    @Autowired
    public void setArchiveTaskConfigService(@Lazy ArchiveTaskConfigService archiveTaskConfigService) {
        this.archiveTaskConfigService = archiveTaskConfigService;
    }

    @Override
    public long resume(long configId, Authentication authentication) throws Exception {
        Optional<ArchiveTaskInstance> archiveTaskInstanceOptional = archiveTaskInstanceRepository.findArchiveTaskInstanceByConfigId(configId);
        ArchiveTaskConfig archiveTaskConfig = archiveTaskConfigService.findById(configId, authentication);
        adminManager.checkAccessThrowable(authentication, archiveTaskConfig.getProject().getId(), PermissionTypeEnum.ARCHIVE_MANAGE_TASK);

        ArchiveTaskInstance archiveTaskInstance = archiveTaskInstanceOptional.orElseGet(() -> {
            ArchiveTaskInstance newArchiveTaskInstance = new ArchiveTaskInstance();
            newArchiveTaskInstance.setConfig(archiveTaskConfig);
            newArchiveTaskInstance.setStatus(TaskStatus.CREATED);

            return newArchiveTaskInstance;
        });

        if (archiveTaskInstance.getStatus() == TaskStatus.RUNNING) {
            throw new ArchiveTaskInstanceResumeException(String.format("Task with id %s for config %s already running", archiveTaskInstance.getId(), configId));
        }

        if (archiveTaskInstance.getStatus() == TaskStatus.CREATED || archiveTaskInstance.getStatus() == TaskStatus.INITIATION_FAILED) {
            try {
                hdfsManager.initiateTask(archiveTaskConfig);
            } catch (Exception e) {
                archiveTaskInstance.setStatus(TaskStatus.INITIATION_FAILED);
                archiveTaskInstance = archiveTaskInstanceRepository.save(archiveTaskInstance);
                throw new ArchiveTaskInstanceResumeException(String.format("Field to initiate task %s for config %s", archiveTaskInstance.getId(), configId), e);
            }
            archiveTaskInstance.setStatus(TaskStatus.INITIATED);
        }

        try {
            dataManagementManager.startArchiveJob(archiveTaskConfigMapper.mapArchiveTaskToArchiveJobDto(archiveTaskInstance), authentication);
            archiveTaskInstance.setStatus(TaskStatus.RUNNING);
        } catch (Exception e) {
            archiveTaskInstance.setStatus(TaskStatus.FAILED);
            throw e;
        }

        return archiveTaskInstanceRepository.save(archiveTaskInstance).getId();
    }

    @Override
    public void suspendTask(long configId, Authentication authentication) throws ArchiveTaskInstanceNotFoundException, ArchiveStatusWontChangedException {
        ArchiveTaskInstance instance = archiveTaskInstanceRepository.findArchiveTaskInstanceByConfigId(configId)
                .orElseThrow(() -> new ArchiveTaskInstanceNotFoundException("Task instance with id %s not found!"));
        ArchiveTaskConfig config = instance.getConfig();
        adminManager.checkAccessThrowable(authentication, config.getProject().getId(), PermissionTypeEnum.ARCHIVE_MANAGE_TASK);
        if (instance.getStatus() != TaskStatus.RUNNING) {
            throw new ArchiveStatusWontChangedException(String.format("Can't change status for task with id %s to %s because its status already equal to it", instance.getId(), TaskStatus.SUSPENDED));
        }
        dataManagementManager.stopArchiveJob(JobNameDto.builder()
                .projectName(config.getProject().getProjectName())
                .taskName(config.getName()).build(), authentication);
        forceChangeArchiveInstanceStatus(instance.getId(), TaskStatus.SUSPENDED);

    }

    @Override
    public void forceChangeArchiveInstanceStatus(long instanceId, TaskStatus taskStatus) throws ArchiveTaskInstanceNotFoundException, ArchiveStatusWontChangedException {
        ArchiveTaskInstance instance = archiveTaskInstanceRepository.findById(instanceId).orElseThrow(() -> new ArchiveTaskInstanceNotFoundException(String.format("Fulltext task instance with id %s not found!", instanceId)));
        if (instance.getStatus() == taskStatus) {
            throw new ArchiveStatusWontChangedException(String.format("Can't change status for task with id %s to %s because its status already equal to it", instanceId, taskStatus));
        }
        log.warn("Force change status of fulltext task with id {} from {} to {}",
                instanceId,
                instance.getStatus(),
                taskStatus);
        instance.setStatus(taskStatus);
        archiveTaskInstanceRepository.save(instance);
    }

    @Override
    public List<ArchiveTaskInstance> findAllTaskInstancesWithStatus(TaskStatus taskStatus) {
        return archiveTaskInstanceRepository.findAllByStatus(taskStatus);
    }

    @Override
    public ArchiveTaskInstance findByConfigId(long configId) throws ArchiveTaskInstanceNotFoundException {
        return archiveTaskInstanceRepository.findArchiveTaskInstanceByConfigId(configId).orElseThrow(() -> new ArchiveTaskInstanceNotFoundException(String.format("Instance for config id %s not found!", configId)));
    }

    @Override
    public Optional<ArchiveTaskInstance> findByConfigIdOptionally(long configId) {
        return archiveTaskInstanceRepository.findArchiveTaskInstanceByConfigId(configId);
    }

    @Override
    public ArchiveTaskInstance findById(long id) throws ArchiveTaskInstanceNotFoundException {
        return archiveTaskInstanceRepository.findById(id).orElseThrow(() -> new ArchiveTaskInstanceNotFoundException(String.format("Archive task instance with id %s not found!", id)));
    }

    @Override
    public void removeInstance(long id, Authentication authentication) throws ArchiveTaskInstanceNotFoundException, ArchiveStatusWontChangedException, IOException {
        ArchiveTaskInstance instance = findById(id);
        TaskStatus taskStatus = instance.getStatus();
        if (taskStatus == TaskStatus.RUNNING) {
            suspendTask(instance.getConfig().getId(), authentication);
        }
        if (taskStatus != TaskStatus.CREATED && taskStatus != TaskStatus.INITIATION_FAILED) {
            hdfsManager.removeArchiveInstance(instance.getConfig());
        }
        archiveTaskInstanceRepository.deleteById(id);
    }

    @Override
    public void saveInstance(ArchiveTaskInstance archiveTaskInstance) {
        archiveTaskInstanceRepository.save(archiveTaskInstance);
    }

    @Override
    public void update(long configId, Authentication authentication) throws Exception {
        ArchiveTaskInstance instance = findByConfigId(configId);
        adminManager.checkAccessThrowable(authentication, instance.getConfig().getProject().getId(), PermissionTypeEnum.ARCHIVE_MANAGE_TASK);
        boolean running = instance.getStatus() == TaskStatus.RUNNING;
        if (instance.getStatus() == TaskStatus.INITIATION_FAILED || instance.getStatus() == TaskStatus.CREATED) {
            throw new ArchiveInstanceUpdateException("Cant update not initiated task instance");
        }
        if (running) {
            suspendTask(configId, authentication);
            resume(configId, authentication);
        }
        instance.setNeedUpdate(false);
        archiveTaskInstanceRepository.save(instance);
    }


}
