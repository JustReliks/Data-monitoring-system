package ru.spbstu.rakitin.monitoring_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.spbstu.rakitin.commonentites.model.PermissionTypeEnum;
import ru.spbstu.rakitin.commonstarter.admin.AdminManager;
import ru.spbstu.rakitin.commonstarter.datamanagement.DataManagementManager;
import ru.spbstu.rakitin.commonstarter.dto.JobNameDto;
import ru.spbstu.rakitin.commonstarter.dto.TaskStatus;
import ru.spbstu.rakitin.monitoring_service.dto.MonitoringTaskConfigMapper;
import ru.spbstu.rakitin.monitoring_service.engine.InfluxDBManager;
import ru.spbstu.rakitin.monitoring_service.exception.MonitoringStatusWontChangedException;
import ru.spbstu.rakitin.monitoring_service.exception.MonitoringTaskConfigNotFoundException;
import ru.spbstu.rakitin.monitoring_service.exception.MonitoringTaskInstanceNotFoundException;
import ru.spbstu.rakitin.monitoring_service.exception.MonitoringTaskResumeException;
import ru.spbstu.rakitin.monitoring_service.model.MonitoringTaskConfig;
import ru.spbstu.rakitin.monitoring_service.model.MonitoringTaskInstance;
import ru.spbstu.rakitin.monitoring_service.repository.MonitoringTaskInstanceRepository;
import ru.spbstu.rakitin.monitoring_service.service.MonitoringTaskConfigService;
import ru.spbstu.rakitin.monitoring_service.service.MonitoringTaskInstanceService;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class MonitoringTaskInstanceServiceImpl implements MonitoringTaskInstanceService {

    private final MonitoringTaskInstanceRepository monitoringTaskInstanceRepository;
    private final MonitoringTaskConfigService monitoringTaskConfigService;
    private final InfluxDBManager influxDBManager;
    private final DataManagementManager dataManagementManager;
    private final MonitoringTaskConfigMapper monitoringTaskConfigMapper;
    private final AdminManager adminManager;

    @Override
    public long resume(long configId, Authentication authentication) throws Exception {
        MonitoringTaskConfig config = monitoringTaskConfigService.getConfig(configId, authentication);
        MonitoringTaskInstance instance = monitoringTaskInstanceRepository.findFirstByConfigId(configId).orElseGet(() -> {
            MonitoringTaskInstance monitoringTaskInstance = new MonitoringTaskInstance();
            monitoringTaskInstance.setTaskStatus(TaskStatus.CREATED);
            monitoringTaskInstance.setConfig(config);

            return monitoringTaskInstanceRepository.save(monitoringTaskInstance);
        });

        if (instance.getTaskStatus() == TaskStatus.RUNNING) {
            throw new MonitoringTaskResumeException(String.format("Monitoring task for config with id %s already running with id %s.", config.getId(), instance.getId()));
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
            dataManagementManager.startMonitoringJob(monitoringTaskConfigMapper.mapMonitoringTaskConfigToJobDto(instance), authentication);
        } catch (Exception e) {
            instance.setTaskStatus(TaskStatus.FAILED);
            monitoringTaskInstanceRepository.save(instance);
            throw e;
        }

        instance.setTaskStatus(TaskStatus.RUNNING);
        return monitoringTaskInstanceRepository.save(instance).getId();

    }

    @Override
    public void suspendTask(long configId, Authentication authentication) throws MonitoringTaskConfigNotFoundException, MonitoringTaskInstanceNotFoundException, MonitoringStatusWontChangedException {
        MonitoringTaskInstance instance = monitoringTaskInstanceRepository.findFirstByConfigId(configId)
                .orElseThrow(() -> new MonitoringTaskInstanceNotFoundException("Task instance with id %s not found!"));
        MonitoringTaskConfig config = instance.getConfig();
        adminManager.checkAccessThrowable(authentication, config.getProject().getId(), PermissionTypeEnum.ARCHIVE_MANAGE_TASK);
        if (instance.getTaskStatus() != TaskStatus.RUNNING) {
            throw new MonitoringStatusWontChangedException(String.format("Can't change status for task with id %s to %s because its status already equal to it", instance.getId(), TaskStatus.SUSPENDED));
        }
        dataManagementManager.stopArchiveJob(JobNameDto.builder()
                .projectName(config.getProject().getProjectName())
                .taskName(config.getName()).build(), authentication);
        forceChangeMonitoringInstanceStatus(instance.getId(), TaskStatus.SUSPENDED);

    }

    @Override
    public List<MonitoringTaskInstance> findAllTaskInstancesWithStatus(TaskStatus status) {
        return monitoringTaskInstanceRepository.findAllByTaskStatus(status);
    }

    @Override
    public void forceChangeMonitoringInstanceStatus(long taskId, TaskStatus status) throws MonitoringTaskInstanceNotFoundException, MonitoringStatusWontChangedException {
        MonitoringTaskInstance instance = monitoringTaskInstanceRepository.findById(taskId).orElseThrow(() -> new MonitoringTaskInstanceNotFoundException(String.format("Fulltext task instance with id %s not found!", taskId)));
        if (instance.getTaskStatus() == status) {
            throw new MonitoringStatusWontChangedException(String.format("Can't change status for task with id %s to %s because its status already equal to it", taskId, status));
        }
        log.warn("Force change status of fulltext task with id {} from {} to {}",
                taskId,
                instance.getTaskStatus(),
                status);
        instance.setTaskStatus(status);
        monitoringTaskInstanceRepository.save(instance);

    }

    @Override
    public List<MonitoringTaskInstance> findAllByConfigIds(List<Long> configIds) {
        return monitoringTaskInstanceRepository.findByConfigIdIn(configIds);
    }
}
