package ru.spbstu.rakitin.management.service.impl;

import org.apache.kafka.streams.processor.api.Processor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionManager;
import ru.spbstu.rakitin.commonstarter.admin.AdminManager;
import ru.spbstu.rakitin.dto.TaskType;
import ru.spbstu.rakitin.dto.monitoring.MonitoringJobDto;
import ru.spbstu.rakitin.commonstarter.monitoring.MonitoringServiceManager;
import ru.spbstu.rakitin.dto.MapJson;
import ru.spbstu.rakitin.management.engine.influxdb.InfluxDbClientFactory;
import ru.spbstu.rakitin.management.engine.processors.monitoring.MonitoringJobProcessor;
import ru.spbstu.rakitin.management.service.KafkaService;

import java.util.List;

@Service
@ConditionalOnProperty(name = "mds.monitoring.enabled", havingValue = "true", matchIfMissing = true)
public class MonitoringJobService extends AbstractJobService<MonitoringJobDto> {

    private final InfluxDbClientFactory influxDbClientFactory;
    private final MonitoringServiceManager monitoringServiceManager;

    public MonitoringJobService(AdminManager adminManager, BeanFactory beanFactory, KafkaService kafkaService, TransactionManager transactionManager, InfluxDbClientFactory influxDbClientFactory, MonitoringServiceManager monitoringServiceManager) {
        super(adminManager, beanFactory, kafkaService, transactionManager);
        this.influxDbClientFactory = influxDbClientFactory;
        this.monitoringServiceManager = monitoringServiceManager;
    }


    @Override
    protected List<MonitoringJobDto> fetchRunningTasks() {
        return monitoringServiceManager.findAllByStatus("RUNNING");
    }

    @Override
    protected void changeTaskStatus(long taskId, String status) {
        monitoringServiceManager.changeTaskStatus(taskId, status);
    }

    @Override
    protected Processor<String, MapJson, String, String> getTaskProcessor(MonitoringJobDto job, String taskName) {
        return new MonitoringJobProcessor(job, taskName, influxDbClientFactory);
    }

    @Override
    protected String getServiceName() {
        return "monitoring";
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.MONITORING;
    }
}
