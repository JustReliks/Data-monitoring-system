package ru.spbstu.rakitin.archive_service.service;

import org.springframework.security.core.Authentication;
import ru.spbstu.rakitin.archive_service.exception.ArchiveStatusWontChangedException;
import ru.spbstu.rakitin.archive_service.exception.ArchiveTaskInstanceNotFoundException;
import ru.spbstu.rakitin.archive_service.model.ArchiveTaskInstance;
import ru.spbstu.rakitin.dto.TaskStatus;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ArchiveTaskInstanceService {

    long resume(long configId, Authentication authentication) throws Exception;

    void suspendTask(long configId, Authentication authentication) throws ArchiveTaskInstanceNotFoundException, ArchiveStatusWontChangedException;

    void forceChangeArchiveInstanceStatus(long instanceId, TaskStatus taskStatus) throws ArchiveTaskInstanceNotFoundException, ArchiveStatusWontChangedException;

    List<ArchiveTaskInstance> findAllTaskInstancesWithStatus(TaskStatus taskStatus);

    ArchiveTaskInstance findByConfigId(long configId) throws ArchiveTaskInstanceNotFoundException;

    Optional<ArchiveTaskInstance> findByConfigIdOptionally(long configId);

    ArchiveTaskInstance findById(long id) throws ArchiveTaskInstanceNotFoundException;

    void removeInstance(long id, Authentication authentication) throws ArchiveTaskInstanceNotFoundException, ArchiveStatusWontChangedException, IOException;

    void saveInstance(ArchiveTaskInstance archiveTaskInstance);

    void update(long configId, Authentication authentication) throws Exception;
}
