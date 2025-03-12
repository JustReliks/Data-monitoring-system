package ru.spbstu.rakitin.archive_service.service;

import org.springframework.security.core.Authentication;
import ru.spbstu.rakitin.archive_service.exception.ArchiveStatusWontChangedException;
import ru.spbstu.rakitin.archive_service.exception.ArchiveTaskInstanceNotFoundException;
import ru.spbstu.rakitin.archive_service.model.ArchiveTaskInstance;
import ru.spbstu.rakitin.commonstarter.dto.TaskStatus;

import java.util.List;

public interface ArchiveTaskInstanceService {

    long resume(long configId, Authentication authentication) throws Exception;

    void suspendTask(long configId, Authentication authentication) throws ArchiveTaskInstanceNotFoundException, ArchiveStatusWontChangedException;

    void forceChangeArchiveInstanceStatus(long instanceId, TaskStatus taskStatus) throws ArchiveTaskInstanceNotFoundException, ArchiveStatusWontChangedException;

    List<ArchiveTaskInstance> findAllTaskInstancesWithStatus(TaskStatus taskStatus);

    ArchiveTaskInstance findByConfigId(long configId) throws ArchiveTaskInstanceNotFoundException;
}
