package ru.spbstu.rakitin.monitoring_service.service;

import org.springframework.security.core.Authentication;
import ru.spbstu.rakitin.monitoring_service.exception.MonitoringTaskConfigNotFoundException;
import ru.spbstu.rakitin.monitoring_service.exception.MonitoringTaskResumeException;

public interface MonitoringTaskInstanceService {
    long resume(long configId, Authentication authentication) throws MonitoringTaskConfigNotFoundException, MonitoringTaskResumeException;

    void suspendTask(long configId, Authentication authentication);
}
