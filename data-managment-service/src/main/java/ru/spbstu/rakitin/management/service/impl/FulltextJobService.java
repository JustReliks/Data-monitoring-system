package ru.spbstu.rakitin.management.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import ru.spbstu.rakitin.commonstarter.admin.AdminManager;
import ru.spbstu.rakitin.commonstarter.dto.TaskType;
import ru.spbstu.rakitin.commonstarter.dto.fulltext.FulltextJobDto;
import ru.spbstu.rakitin.commonstarter.fulltext.FulltextServiceManager;
import ru.spbstu.rakitin.commonstarter.utils.MapJson;
import ru.spbstu.rakitin.management.configuration.FulltextJobProperties;
import ru.spbstu.rakitin.management.engine.processors.fulltext.FulltextJobProcessor;
import ru.spbstu.rakitin.management.service.KafkaService;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@ConditionalOnProperty(name = "mds.fulltext.enabled", havingValue = "true", matchIfMissing = true)
public class FulltextJobService extends AbstractJobService<FulltextJobDto> {

    private static final String ID_FIELD = "id";

    private final FulltextServiceManager fulltextServiceManager;

    private final CloudSolrClient cloudSolrClient;

    private final FulltextJobProperties fulltextJobProperties;

    public FulltextJobService(AdminManager adminManager, BeanFactory beanFactory,
                              KafkaService kafkaService,
                              FulltextServiceManager fulltextServiceManager,
                              CloudSolrClient cloudSolrClient, FulltextJobProperties fulltextJobProperties) {
        super(adminManager, beanFactory, kafkaService);
        this.fulltextServiceManager = fulltextServiceManager;
        this.cloudSolrClient = cloudSolrClient;
        this.fulltextJobProperties = fulltextJobProperties;
    }

    @Override
    protected List<FulltextJobDto> fetchRunningTasks() {
        return fulltextServiceManager.findAllByStatus("RUNNING");
    }

    @Override
    protected void changeTaskStatus(long taskId, String status) {
        fulltextServiceManager.changeTaskStatus(taskId, status);
    }

    @Override
    protected Processor<String, MapJson, String, String> getTaskProcessor(FulltextJobDto job, String taskName) {
        return new FulltextJobProcessor(job, cloudSolrClient, taskName);
    }

    @Override
    protected KStream<String, MapJson> decorateStream(FulltextJobDto job, String taskName, KStream<String, MapJson> stream) {
        stream = super.decorateStream(job, taskName, stream);
        return stream.peek((key, value) -> value.put(ID_FIELD, UUID.randomUUID().toString()));
    }

    @Override
    protected String getServiceName() {
        return "fulltext";
    }

    @Override
    public long getFetchTasksRetryTimeoutMillis() {
        return fulltextJobProperties.getFetchTasksTimeoutMs();
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.FULLTEXT;
    }
}
