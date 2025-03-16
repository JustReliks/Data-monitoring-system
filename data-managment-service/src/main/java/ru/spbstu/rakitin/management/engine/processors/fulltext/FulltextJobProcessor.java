package ru.spbstu.rakitin.management.engine.processors.fulltext;

import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.common.SolrInputDocument;
import ru.spbstu.rakitin.commonstarter.dto.fulltext.FulltextJobDto;
import ru.spbstu.rakitin.commonstarter.utils.MapJson;
import ru.spbstu.rakitin.management.engine.AbstractJsonQueueProcessor;
import ru.spbstu.rakitin.management.engine.processors.AbstractQueueProcessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public class FulltextJobProcessor extends AbstractJsonQueueProcessor<FulltextJobDto> {

    private final FulltextJobDto fulltextJobDto;
    private final CloudSolrClient cloudSolrClient;

    private final String taskName;

    public FulltextJobProcessor(FulltextJobDto fulltextJobDto, CloudSolrClient cloudSolrClient, String taskName) {
        super(fulltextJobDto, taskName);
        this.fulltextJobDto = fulltextJobDto;
        this.cloudSolrClient = cloudSolrClient;
        this.taskName = taskName;
    }


    @Override
    protected void processQueue(LinkedBlockingQueue<MapJson> queue) throws Exception {
        Collection<MapJson> collection = new ArrayList<>();
        queue.drainTo(collection);
        List<SolrInputDocument> solrInputDocuments = getSolrInputDocuments(collection);
        if (!solrInputDocuments.isEmpty()) {
            log.info("[{}] Sending {} documents to {}", taskName, solrInputDocuments.size(), fulltextJobDto.getCollectionName());
            UpdateRequest request = new UpdateRequest();
            request.add(solrInputDocuments);
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

    @Override
    public void close() {
        super.close();
        try {
            cloudSolrClient.close();
        } catch (IOException e) {
            log.error("Unable to close cloud solr client", e);
            throw new RuntimeException(e);
        }
    }
}
