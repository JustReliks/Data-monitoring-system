package ru.spbstu.rakitin.fulltext_service.engine;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrResponse;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import org.apache.solr.client.solrj.request.ConfigSetAdminRequest;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.client.solrj.response.CollectionAdminResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.util.ContentStreamBase;
import org.springframework.stereotype.Component;
import ru.spbstu.rakitin.commonstarter.sequence.engine.SequentialEngine;
import ru.spbstu.rakitin.commonstarter.sequence.engine.SequentialTask;
import ru.spbstu.rakitin.dto.MapJson;
import ru.spbstu.rakitin.dto.fulltext.SolrQueryDto;
import ru.spbstu.rakitin.dto.fulltext.SolrQueryResponseDto;
import ru.spbstu.rakitin.fulltext_service.engine.schema.SolrSchema;
import ru.spbstu.rakitin.fulltext_service.engine.utils.SolrUtils;
import ru.spbstu.rakitin.fulltext_service.model.FulltextTaskConfig;
import ru.spbstu.rakitin.fulltext_service.service.SolrSchemaService;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class SolrClientManager {

    public static final String DEFAULT_SCHEMA = "_default";
    public static final String COLLECTIONS_CONTEXT_KEY = "collections";

    private final SolrSchemaService solrSchemaService;
    private final CloudSolrClient solrClient;

    private final SequentialEngine sequentialEngine;

    public void initiateFulltextInstance(FulltextTaskConfig fulltextTaskConfig) throws Exception {
        SolrSchema schema = solrSchemaService.createSolrSchema(fulltextTaskConfig.getSchema());
        String collectionName = SolrUtils.buildCollectionName(fulltextTaskConfig);
        String basePath = getTaskFullName(fulltextTaskConfig);
        Queue<SequentialTask> tasks = getSequentialTasksForInitiateFulltextInstance(fulltextTaskConfig, collectionName, basePath, schema);
        sequentialEngine.performSequential(tasks);
    }

    public SolrQueryResponseDto query(FulltextTaskConfig fulltextTaskConfig, SolrQueryDto solrQueryDto) throws SolrServerException, IOException {
        QueryRequest request = new QueryRequest(getSolrQueryFromDto(solrQueryDto));
        QueryResponse response = this.sendRequest(request, SolrUtils.buildReadCollectionName(fulltextTaskConfig.getProject().getProjectName(), fulltextTaskConfig.getName()));
        SolrQueryResponseDto responseDto = new SolrQueryResponseDto();
        List<MapJson> list = response.getResults().stream().map(entries -> {
            MapJson mapJson = new MapJson();
            mapJson.putAll(entries);
            return mapJson;
        }).toList();

        responseDto.setResponseSize(response.getResults().size());
        responseDto.setQTime(response.getQTime());
        responseDto.setResponse(list);
        return responseDto;
    }


    public void removeFulltextTaskInstance(FulltextTaskConfig config) throws SolrServerException, IOException {
        CollectionAdminRequest.ListAliases listAliases = new CollectionAdminRequest.ListAliases();
        String readAlias = SolrUtils.buildReadCollectionName(config.getProject().getProjectName(), config.getName());
        String writeAlias = SolrUtils.buildWriteCollectionName(config.getProject().getProjectName(), config.getName());

        log.info("Deleting fulltext task instance in solr: {}.{}", config.getProject().getProjectName(), config.getName());
        CollectionAdminResponse collectionAdminResponse = sendRequest(listAliases);
        Set<String> collections = collectionAdminResponse.getAliasesAsLists()
                .entrySet().stream().filter(alias -> alias.getKey().equals(readAlias) || alias.getKey().equals(writeAlias))
                .flatMap(alias -> alias.getValue().stream())
                .collect(Collectors.toSet());

        log.info("Deleting aliases: [{}, {}]", readAlias, writeAlias);
        sendRequest(CollectionAdminRequest.deleteAlias(writeAlias));
        sendRequest(CollectionAdminRequest.deleteAlias(readAlias));

        log.info("Deleting collections: [{}]", collections);
        for (String collection : collections) {
            CollectionAdminRequest.Delete delete = CollectionAdminRequest.deleteCollection(collection);
            sendRequest(delete);
        }

    }

    public void updateFulltextTaskInstance(FulltextTaskConfig config) throws Exception {
        SolrSchema schema = solrSchemaService.createSolrSchema(config.getSchema());
        String collectionName = SolrUtils.buildCollectionName(config);
        String basePath = getTaskFullName(config);
        Queue<SequentialTask> tasks = new LinkedList<>();
        tasks.add(createSchemaFolder(collectionName));
        tasks.add(uploadSchemaToFolder(collectionName, schema));
        tasks.add(createCollection(config, collectionName));
        tasks.add(deleteAlias(basePath + "_READ"));
        tasks.add(createReadAlias(List.of(collectionName), basePath, COLLECTIONS_CONTEXT_KEY));
        tasks.add(addFieldsToSchema(collectionName, schema));
        tasks.add(deleteAlias(basePath + "_WRITE"));
        tasks.add(createWriteAlias(collectionName, basePath));

        sequentialEngine.performSequential(tasks);

    }

    private Queue<SequentialTask> getSequentialTasksForInitiateFulltextInstance(FulltextTaskConfig fulltextTaskConfig, String collectionName, String basePath, SolrSchema schema) {
        Queue<SequentialTask> tasks = new LinkedList<>();

        tasks.add(createSchemaFolder(collectionName));
        tasks.add(uploadSchemaToFolder(collectionName, schema));
        tasks.add(createCollection(fulltextTaskConfig, collectionName));
        tasks.add(addFieldsToSchema(collectionName, schema));
        tasks.add(createReadAlias(List.of(collectionName), basePath));
        tasks.add(createWriteAlias(collectionName, basePath));
        return tasks;
    }


    private SequentialTask deleteAlias(String aliasName) {
        return new SequentialTask() {
            @Override
            public void perform(Map<String, Object> context) throws Exception {
                List<String> collections = deleteAliasAndReturnCollections(aliasName);
                context.put(COLLECTIONS_CONTEXT_KEY, collections);
            }

            @Override
            public void rollback(Map<String, Object> context) throws Exception {
                if (context.containsKey(COLLECTIONS_CONTEXT_KEY)) {
                    List<String> collections = (List<String>) context.get(COLLECTIONS_CONTEXT_KEY);
                    String collectionsStr = getStringFromList(collections);
                    CollectionAdminRequest.CreateAlias createAlias = CollectionAdminRequest.createAlias(aliasName, collectionsStr);
                    sendRequest(createAlias);
                }
            }
        };
    }


    private static String getStringFromList(List<String> collections) {
        return new StringBuilder(collections.toString()).substring(1, collections.toString().length() - 1).toString();
    }

    private List<String> deleteAliasAndReturnCollections(String aliasName) throws SolrServerException, IOException {
        List<String> collections = findCollectionsForAlias(aliasName);
        CollectionAdminRequest.DeleteAlias deleteAlias = CollectionAdminRequest.deleteAlias(aliasName);
        sendRequest(deleteAlias);
        return collections;
    }


    private SequentialTask createWriteAlias(String collectionName, String basePath) {
        return new SequentialTask() {
            @Override
            public void perform(Map<String, Object> context) throws SolrServerException, IOException {
                String readAliasName = basePath + "_WRITE";
                CollectionAdminRequest.CreateAlias createAlias = CollectionAdminRequest.createAlias(readAliasName, collectionName);
                sendRequest(createAlias);
            }

            @Override
            public void rollback(Map<String, Object> context) throws SolrServerException, IOException {
                CollectionAdminRequest.DeleteAlias deleteAlias = CollectionAdminRequest.deleteAlias(basePath + "_WRITE");
                sendRequest(deleteAlias);
            }
        };
    }


    private SequentialTask createReadAlias(List<String> collectionName, String basePath, String contextPropertyWithCollectionNames) {
        return new SequentialTask() {
            @Override
            public void perform(Map<String, Object> context) throws SolrServerException, IOException {
                List<String> collections = new ArrayList<>(collectionName);
                if (contextPropertyWithCollectionNames != null && context.containsKey(contextPropertyWithCollectionNames)) {
                    List<String> contextCollections = (List<String>) context.get(contextPropertyWithCollectionNames);
                    if (contextCollections != null) {
                        collections.addAll(contextCollections);
                    }
                }

                String readAliasName = basePath + "_READ";
                String collectionsStr = getStringFromList(collections);
                CollectionAdminRequest.CreateAlias createAlias = CollectionAdminRequest.createAlias(readAliasName, collectionsStr);
                sendRequest(createAlias);

            }

            @Override
            public void rollback(Map<String, Object> context) throws SolrServerException, IOException {
                CollectionAdminRequest.DeleteAlias deleteAlias = CollectionAdminRequest.deleteAlias(basePath + "_READ");
                sendRequest(deleteAlias);
            }
        };
    }

    private SequentialTask createReadAlias(List<String> collectionName, String basePath) {
        return createReadAlias(collectionName, basePath, null);
    }


    private SequentialTask addFieldsToSchema(String collectionName, SolrSchema schema) {
        return new SequentialTask() {
            @Override
            public void perform(Map<String, Object> context) throws SolrServerException, IOException {
                for (Map<String, Object> fieldDesc : schema.getFieldsDescriptions()) {
                    SchemaRequest.AddField addField = new SchemaRequest.AddField(fieldDesc);
                    sendRequest(addField, collectionName);
                }
            }

            @Override
            public void rollback(Map<String, Object> context) {
                // nothing to do
            }
        };
    }


    private SequentialTask createCollection(FulltextTaskConfig fulltextTaskConfig, String collectionName) {
        return new SequentialTask() {
            @Override
            public void perform(Map<String, Object> context) throws SolrServerException, IOException {
                CollectionAdminRequest.Create collection = CollectionAdminRequest.createCollection(collectionName, collectionName, fulltextTaskConfig.getShardsCount(), fulltextTaskConfig.getReplicationFactor());
                sendRequest(collection);

            }

            @Override
            public void rollback(Map<String, Object> context) throws SolrServerException, IOException {
                CollectionAdminRequest.Delete deleteCollection = CollectionAdminRequest.deleteCollection(collectionName);
                sendRequest(deleteCollection);
            }
        };
    }


    private SequentialTask uploadSchemaToFolder(String basePath, SolrSchema schema) {
        return new SequentialTask() {
            @Override
            public void perform(Map<String, Object> context) throws SolrServerException, IOException {
                ConfigSetAdminRequest.Upload uploadConfigSet = new ConfigSetAdminRequest.Upload();
                uploadConfigSet.setFilePath("managed-schema.xml");
                uploadConfigSet.setOverwrite(true);
                uploadConfigSet.setUploadFile(schema.getDefaultSchemaFile(), ContentStreamBase.TEXT_XML);
                uploadConfigSet.setConfigSetName(basePath);
                sendRequest(uploadConfigSet);
            }

            @Override
            public void rollback(Map<String, Object> context) {
                // nothing to do
            }
        };
    }


    private SequentialTask createSchemaFolder(String basePath) {
        return new SequentialTask() {
            @Override
            public void perform(Map<String, Object> context) throws SolrServerException, IOException {
                ConfigSetAdminRequest.Create create = new ConfigSetAdminRequest.Create();
                create.setBaseConfigSetName(DEFAULT_SCHEMA);
                create.setConfigSetName(basePath);
                Properties properties = new Properties();
                properties.put("immutable", "false");
                create.setNewConfigSetProperties(properties);
                create.getParams();
                sendRequest(create);
            }

            @Override
            public void rollback(Map<String, Object> context) throws SolrServerException, IOException {
                ConfigSetAdminRequest.Delete delete = new ConfigSetAdminRequest.Delete();
                delete.setConfigSetName(basePath);
                sendRequest(delete);

            }
        };
    }

    public List<String> findCollectionsForAlias(String alias) throws SolrServerException, IOException {
        CollectionAdminRequest.ListAliases listAliases = new CollectionAdminRequest.ListAliases();
        CollectionAdminResponse collectionAdminResponse = sendRequest(listAliases);
        return collectionAdminResponse.getAliasesAsLists().get(alias);
    }

    private static SolrQuery getSolrQueryFromDto(SolrQueryDto solrQueryDto) {
        SolrQuery query = new SolrQuery(solrQueryDto.getQuery());
        query.setRows(solrQueryDto.getRows() == null ? 10 : solrQueryDto.getRows());
        if (solrQueryDto.getSort() != null) {
            solrQueryDto.getSort().forEach(solrSort -> query.setSort(solrSort.getField(), SolrQuery.ORDER.valueOf(solrSort.getOrder().name())));
        }
        if (solrQueryDto.getFilters() != null) {
            query.setFilterQueries(solrQueryDto.getFilters().toArray(new String[0]));
        }
        if (solrQueryDto.getReqFields() != null) {
            query.setFields(solrQueryDto.getReqFields().toArray(new String[0]));
        }
        return query;
    }


    private static String getTaskFullName(FulltextTaskConfig fulltextTaskConfig) {
        return fulltextTaskConfig.getProject().getProjectName() + "." + fulltextTaskConfig.getName();
    }


    private <T extends SolrResponse> T sendRequest(SolrRequest<T> request) throws SolrServerException, IOException {
        return sendRequest(request, null);
    }

    private <T extends SolrResponse> T sendRequest(SolrRequest<T> request, String collection) throws SolrServerException, IOException {
        if (collection != null) {
            return request.process(solrClient, collection);
        }

        return request.process(solrClient);
    }


}
