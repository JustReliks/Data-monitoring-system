package ru.spbstu.rakitin.management.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.stereotype.Service;
import ru.spbstu.rakitin.commonstarter.admin.AdminManager;
import ru.spbstu.rakitin.commonstarter.dto.fulltext.FulltextJobDto;
import ru.spbstu.rakitin.commonstarter.fulltext.FulltextServiceManager;
import ru.spbstu.rakitin.management.engine.processors.FulltextJobProcessor;
import ru.spbstu.rakitin.management.service.KafkaService;

import java.util.List;

@Service
@Slf4j
public class FulltextJobService extends AbstractJobService<FulltextJobDto> {

    private final FulltextServiceManager fulltextServiceManager;

    private final CloudSolrClient cloudSolrClient;

    public FulltextJobService(AdminManager adminManager, BeanFactory beanFactory,
                              KafkaService kafkaService,
                              FulltextServiceManager fulltextServiceManager,
                              CloudSolrClient cloudSolrClient) {
        super(adminManager, beanFactory, kafkaService);
        this.fulltextServiceManager = fulltextServiceManager;
        this.cloudSolrClient = cloudSolrClient;
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
    protected Processor<String, String, String, String> getTaskProcessor(FulltextJobDto job, String taskName) {
        return new FulltextJobProcessor(job, cloudSolrClient, taskName);
    }

}
