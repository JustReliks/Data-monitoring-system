package ru.spbstu.rakitin.management.engine.processors;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.spbstu.rakitin.commonstarter.dto.fulltext.SchemaFieldDto;

import java.io.IOException;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public class FulltextJobProcessor implements Processor<String, String, String, String> {

    public static final int EXECUTE_PERIOD_SEC = 15;
    private final FulltextJobDto fulltextJobDto;
    private final CloudSolrClient cloudSolrClient;
    final LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>(100);
    private final ObjectMapper objectMapper = new ObjectMapper();

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
    public void process(Record<String, String> record) {
        String value = record.value();
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
        Collection<String> collection = new ArrayList<>();
        queue.drainTo(collection);
        List<SolrInputDocument> solrInputDocuments = getSolrInputDocuments(collection);
        if (solrInputDocuments.size() != 0) {
            log.info("Sending {} documasdents to {}", solrInputDocuments.size(), fulltextJobDto.getCollectionName());
            UpdateRequest request = new UpdateRequest();
            request.add(solrInputDocuments);
            request.setBasicAuthCredentials("solr", "SolrRocks");
            request.commit(cloudSolrClient, fulltextJobDto.getCollectionName());
        }
    }

    private List<SolrInputDocument> getSolrInputDocuments(Collection<String> collection) {
        return collection.stream().map(value -> {
                    try {
                        Map<String, Object> mapDoc = objectMapper.readValue(value, Map.class);
                        return mapDoc;
                    } catch (Exception e) {
                        log.error("[{}] Unable to parse [{}] to solr document!", taskName, value, e);
                    }
                    return null;
                }).filter(Objects::nonNull)
                .map(this::mapToDocument)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    private Optional<SolrInputDocument> mapToDocument(Map<String, Object> map) {
        SolrInputDocument solrInputDocument = new SolrInputDocument();
        map.forEach((k, v) -> {
            Optional<SchemaFieldDto> schemaField = fulltextJobDto.getSchema().getFields().stream().filter(field -> field.getFieldName().equals(k))
                    .findFirst();
            if (schemaField.isPresent() && schemaField.get().getFieldType().isValueCompatible(v.toString(), schemaField.get())) {
                solrInputDocument.addField(k, v);
            } else {
                log.error("[{}] Unable to find compatible field in task schema for value field [{}:{}]!", taskName, k, v);
            }
        });
        if (fulltextJobDto.getSchema().getTimestampField().isUseInsertionDate()) {
            solrInputDocument.setField(fulltextJobDto.getSchema().getTimestampField().getFieldName(), DateTimeFormatter.ISO_INSTANT.format(ZonedDateTime.now()));
        }
        solrInputDocument.setField("id", UUID.randomUUID().toString());
        Optional<String> notFoundField = fulltextJobDto.getSchema().getFields().stream().map(SchemaFieldDto::getFieldName)
                .filter(s -> !solrInputDocument.containsKey(s)).findAny();

        if (notFoundField.isPresent()) {
            log.error("[{}] Unable to find field {} in input document {}. Skip it.", taskName, notFoundField.get(), map);
            return Optional.empty();
        }
        return Optional.of(solrInputDocument);

    }

}
