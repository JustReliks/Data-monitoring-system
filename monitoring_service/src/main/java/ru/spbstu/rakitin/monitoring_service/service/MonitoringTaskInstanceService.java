package ru.spbstu.rakitin.monitoring_service.service;

import org.springframework.security.core.Authentication;
import ru.spbstu.rakitin.commonstarter.dto.TaskStatus;
import ru.spbstu.rakitin.monitoring_service.exception.MonitoringStatusWontChangedException;
import ru.spbstu.rakitin.monitoring_service.exception.MonitoringTaskConfigNotFoundException;
import ru.spbstu.rakitin.monitoring_service.exception.MonitoringTaskInstanceNotFoundException;
import ru.spbstu.rakitin.monitoring_service.exception.MonitoringTaskResumeException;
import ru.spbstu.rakitin.monitoring_service.model.MonitoringTaskInstance;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MonitoringTaskInstanceService {
    long resume(long configId, Authentication authentication) throws Exception;

    void suspendTask(long configId, Authentication authentication) throws MonitoringTaskConfigNotFoundException, MonitoringTaskInstanceNotFoundException, MonitoringStatusWontChangedException;

    void update(long configId, Authentication authentication) throws Exception;

    MonitoringTaskInstance findByConfigId(long configId) throws MonitoringTaskInstanceNotFoundException;

    List<MonitoringTaskInstance> findAllTaskInstancesWithStatus(TaskStatus status);

    void forceChangeMonitoringInstanceStatus(long taskId, TaskStatus status) throws MonitoringTaskInstanceNotFoundException, MonitoringStatusWontChangedException;
    List<MonitoringTaskInstance> findAllByConfigIds(List<Long> configIds);

    Optional<MonitoringTaskInstance> findByConfigIdOptionally(Long id);

    void removeInstance(Long id, Authentication authentication) throws MonitoringStatusWontChangedException, MonitoringTaskConfigNotFoundException, MonitoringTaskInstanceNotFoundException;

    MonitoringTaskInstance findById(Long id) throws MonitoringTaskInstanceNotFoundException;

    void saveInstance(MonitoringTaskInstance monitoringTaskInstance);
}

