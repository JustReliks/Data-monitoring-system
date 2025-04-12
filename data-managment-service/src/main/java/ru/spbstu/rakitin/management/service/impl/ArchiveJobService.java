package ru.spbstu.rakitin.management.service.impl;

import org.apache.hadoop.fs.FileSystem;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.processor.api.Processor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import ru.spbstu.rakitin.commonstarter.admin.AdminManager;
import ru.spbstu.rakitin.commonstarter.archive.ArchiveServiceManager;
import ru.spbstu.rakitin.dto.TaskStatus;
import ru.spbstu.rakitin.dto.TaskType;
import ru.spbstu.rakitin.dto.archive.ArchiveJobDto;
import ru.spbstu.rakitin.dto.MapJson;
import ru.spbstu.rakitin.management.engine.hdfs.HdfsConfigurationProperties;
import ru.spbstu.rakitin.management.engine.processors.archive.ArchiveJobProcessor;
import ru.spbstu.rakitin.management.engine.processors.archive.ValidateFilenameFilter;
import ru.spbstu.rakitin.management.service.KafkaService;

import java.util.List;

@Service
@ConditionalOnProperty(name = "mds.archive.enabled", havingValue = "true", matchIfMissing = true)
public class ArchiveJobService extends AbstractJobService<ArchiveJobDto> {

    private final FileSystem fileSystem;
    private final HdfsConfigurationProperties hdfsConfigurationProperties;
    private final ArchiveServiceManager archiveServiceManager;

    public ArchiveJobService(AdminManager adminManager, BeanFactory beanFactory, KafkaService kafkaService, FileSystem fileSystem, HdfsConfigurationProperties hdfsConfigurationProperties, ArchiveServiceManager archiveServiceManager) {
        super(adminManager, beanFactory, kafkaService);
        this.fileSystem = fileSystem;
        this.hdfsConfigurationProperties = hdfsConfigurationProperties;
        this.archiveServiceManager = archiveServiceManager;
    }

    @Override
    protected List<ArchiveJobDto> fetchRunningTasks() {
        return archiveServiceManager.findAllByStatus(TaskStatus.RUNNING.name());
    }

    @Override
    protected void changeTaskStatus(long taskId, String status) {
        archiveServiceManager.changeTaskStatus(taskId, status);
    }

    @Override
    protected Processor<String, MapJson, String, String> getTaskProcessor(ArchiveJobDto job, String taskName) {
        return new ArchiveJobProcessor(fileSystem, taskName, job, hdfsConfigurationProperties);
    }

    @Override
    protected KStream<String, MapJson> decorateStream(ArchiveJobDto job, String taskName, KStream<String, MapJson> stream) {
        stream = super.decorateStream(job, taskName, stream)
                .filter(new ValidateFilenameFilter(job));

        return stream;
    }

    @Override
    protected String getServiceName() {
        return "archive";
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.ARCHIVE;
    }
}
