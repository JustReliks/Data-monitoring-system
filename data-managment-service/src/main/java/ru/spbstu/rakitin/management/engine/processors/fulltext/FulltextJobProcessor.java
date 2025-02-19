package ru.spbstu.rakitin.management.engine.processors.fulltext;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.common.SolrInputDocument;
import ru.spbstu.rakitin.commonstarter.dto.fulltext.FulltextJobDto;
import ru.spbstu.rakitin.commonstarter.utils.MapJson;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public class FulltextJobProcessor implements Processor<String, MapJson, String, String> {

    public static final int EXECUTE_PERIOD_SEC = 15;
    private final FulltextJobDto fulltextJobDto;
    private final CloudSolrClient cloudSolrClient;
    private final LinkedBlockingQueue<MapJson> queue = new LinkedBlockingQueue<>(100);

    private final String taskName;

    public FulltextJobProcessor(FulltextJobDto fulltextJobDto, CloudSolrClient cloudSolrClient, String taskName) {
        this.fulltextJobDto = fulltextJobDto;
        this.cloudSolrClient = cloudSolrClient;
        this.taskName = taskName;
    }

    @Override
    public void init(ProcessorContext<String, String> context) {
        Processor.super.init(context);
        context.schedule(Duration.of(EXECUTE_PERIOD_SEC, ChronoUnit.SECONDS), PunctuationType.WALL_CLOCK_TIME, timestamp -> {
            try {
                processQueue();
            } catch (SolrServerException | IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void process(Record<String, MapJson> record) {
        MapJson value = record.value();
        try {
            queue.put(value);
            if (queue.remainingCapacity() == 0) {
                processQueue();
            }
        } catch (InterruptedException | SolrServerException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void processQueue() throws SolrServerException, IOException {
        Collection<MapJson> collection = new ArrayList<>();
        queue.drainTo(collection);
        List<SolrInputDocument> solrInputDocuments = getSolrInputDocuments(collection);
        if (solrInputDocuments.size() != 0) {
            log.info("[{}] Sending {} documents to {}", taskName, solrInputDocuments.size(), fulltextJobDto.getCollectionName());
            UpdateRequest request = new UpdateRequest();
            request.add(solrInputDocuments);
            request.setBasicAuthCredentials("solr", "SolrRocks");
            request.commit(cloudSolrClient, fulltextJobDto.getCollectionName());
        }
    }

    private List<SolrInputDocument> getSolrInputDocuments(Collection<MapJson> collection) {
        return collection.stream()
                .map(this::mapToDocument)
                .toList();
    }

    private SolrInputDocument mapToDocument(Map<String, Object> map) {
        SolrInputDocument solrInputDocument = new SolrInputDocument();
        map.forEach(solrInputDocument::addField);
        return solrInputDocument;
    }

}
