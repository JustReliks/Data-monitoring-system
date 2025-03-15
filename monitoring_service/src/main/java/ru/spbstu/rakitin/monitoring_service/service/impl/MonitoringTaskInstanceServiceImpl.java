package ru.spbstu.rakitin.monitoring_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.spbstu.rakitin.commonstarter.dto.TaskStatus;
import ru.spbstu.rakitin.monitoring_service.engine.InfluxDBManager;
import ru.spbstu.rakitin.monitoring_service.exception.MonitoringTaskConfigNotFoundException;
import ru.spbstu.rakitin.monitoring_service.exception.MonitoringTaskResumeException;
import ru.spbstu.rakitin.monitoring_service.model.MonitoringTaskConfig;
import ru.spbstu.rakitin.monitoring_service.model.MonitoringTaskInstance;
import ru.spbstu.rakitin.monitoring_service.repository.MonitoringTaskInstanceRepository;
import ru.spbstu.rakitin.monitoring_service.service.MonitoringTaskConfigService;
import ru.spbstu.rakitin.monitoring_service.service.MonitoringTaskInstanceService;

@RequiredArgsConstructor
@Service
public class MonitoringTaskInstanceServiceImpl implements MonitoringTaskInstanceService {

    private final MonitoringTaskInstanceRepository monitoringTaskInstanceRepository;
    private final MonitoringTaskConfigService monitoringTaskConfigService;
    private final InfluxDBManager influxDBManager;

    @Override
    public long resume(long configId, Authentication authentication) throws MonitoringTaskConfigNotFoundException, MonitoringTaskResumeException {
        MonitoringTaskConfig config = monitoringTaskConfigService.getConfig(configId, authentication);
        MonitoringTaskInstance instance = monitoringTaskInstanceRepository.findFirstByConfigId(configId).orElseGet(() -> {
            MonitoringTaskInstance monitoringTaskInstance = new MonitoringTaskInstance();
            monitoringTaskInstance.setTaskStatus(TaskStatus.CREATED);
            monitoringTaskInstance.setConfig(config);

            return monitoringTaskInstanceRepository.save(monitoringTaskInstance);
        });

        if (instance.getTaskStatus() == TaskStatus.RUNNING) {
            throw new MonitoringTaskResumeException(String.format("Monitoring task for config with id %s already running with id %s.", config, instance.getId()));
        }
        if (instance.getTaskStatus() == TaskStatus.CREATED || instance.getTaskStatus() == TaskStatus.INITIATION_FAILED) {
            try {
                influxDBManager.initiateMonitoringTask(config);

            } catch (Exception e) {
                instance.setTaskStatus(TaskStatus.INITIATION_FAILED);
                monitoringTaskInstanceRepository.save(instance);
                throw e;
            }
            instance.setTaskStatus(TaskStatus.INITIATED);
        }

        try {
            //run

        } catch (Exception e) {
            instance.setTaskStatus(TaskStatus.FAILED);
            monitoringTaskInstanceRepository.save(instance);
            throw e;
        }

        instance.setTaskStatus(TaskStatus.RUNNING);
        return monitoringTaskInstanceRepository.save(instance).getId();

    }

    @Override
    public void suspendTask(long configId, Authentication authentication) {

    }
}
