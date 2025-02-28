package ru.spbstu.rakitin.fulltext_service.service;

import org.springframework.security.core.Authentication;
import ru.spbstu.rakitin.fulltext_service.exception.*;
import ru.spbstu.rakitin.fulltext_service.model.FulltextTaskInstance;
import ru.spbstu.rakitin.fulltext_service.model.TaskStatus;

import java.util.List;

public interface FulltextTaskInstanceService {

    void suspendTask(long taskId, Authentication authentication) throws FulltextTaskInstanceNotFoundException, FulltextStatusWontChangedException;

    void resume(long configId, Authentication authentication) throws FulltextConfigNotFoundException, IllegalAccessException, FulltextTaskInstanceAlreadyRunningException, InstanceInitiationFailedException;

    void forceChangeFulltextInstanceStatus(long instanceId, TaskStatus taskStatus) throws FulltextTaskInstanceNotFoundException, FulltextStatusWontChangedException;

    List<FulltextTaskInstance> findAllTaskInstancesWithStatus(TaskStatus taskStatus);

    FulltextTaskInstance findByConfigId(long configId) throws FulltextTaskInstanceNotFoundException;
}
