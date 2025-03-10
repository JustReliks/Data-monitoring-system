package ru.spbstu.rakitin.archive_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.spbstu.rakitin.archive_service.engine.HdfsManager;
import ru.spbstu.rakitin.archive_service.exception.ArchiveConfigNotFoundException;
import ru.spbstu.rakitin.archive_service.exception.ArchiveTaskInstanceResumeException;
import ru.spbstu.rakitin.archive_service.model.ArchiveTaskConfig;
import ru.spbstu.rakitin.archive_service.model.ArchiveTaskInstance;
import ru.spbstu.rakitin.archive_service.repository.ArchiveTaskConfigRepository;
import ru.spbstu.rakitin.archive_service.repository.ArchiveTaskInstanceRepository;
import ru.spbstu.rakitin.archive_service.service.ArchiveTaskInstanceService;
import ru.spbstu.rakitin.commonstarter.dto.TaskStatus;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArchiveTaskInstanceServiceImpl implements ArchiveTaskInstanceService {

    private final ArchiveTaskInstanceRepository archiveTaskInstanceRepository;
    private final ArchiveTaskConfigRepository archiveTaskConfigRepository;
    private final HdfsManager hdfsManager;

    @Override
    public long resume(long configId, Authentication authentication) throws Exception {
        Optional<ArchiveTaskInstance> archiveTaskInstanceOptional = archiveTaskInstanceRepository.findArchiveTaskInstanceByConfigId(configId);
        ArchiveTaskConfig archiveTaskConfig = archiveTaskConfigRepository.findById(configId).orElseThrow(() -> new ArchiveConfigNotFoundException(String.format("Archive config with id %s not found", configId)));

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

        return archiveTaskInstanceRepository.save(archiveTaskInstance).getId();
    }
}
