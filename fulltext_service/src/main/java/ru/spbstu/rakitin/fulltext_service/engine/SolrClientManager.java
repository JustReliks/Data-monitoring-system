package ru.spbstu.rakitin.fulltext_service.engine;

import lombok.RequiredArgsConstructor;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrResponse;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import org.apache.solr.client.solrj.request.ConfigSetAdminRequest;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.util.ContentStreamBase;
import org.springframework.stereotype.Component;
import ru.spbstu.rakitin.commonstarter.sequence.engine.SequentialEngine;
import ru.spbstu.rakitin.commonstarter.sequence.engine.SequentialTask;
import ru.spbstu.rakitin.commonstarter.utils.MapJson;
import ru.spbstu.rakitin.fulltext_service.dto.SolrQueryDto;
import ru.spbstu.rakitin.fulltext_service.engine.schema.SolrSchema;
import ru.spbstu.rakitin.fulltext_service.engine.utils.SolrUtils;
import ru.spbstu.rakitin.fulltext_service.model.FulltextTaskConfig;
import ru.spbstu.rakitin.fulltext_service.service.SchemaService;

import java.io.IOException;
import java.util.*;

@Component
@RequiredArgsConstructor
public class SolrClientManager {

    public static final String DEFAULT_SCHEMA = "_default";

    private final SchemaService schemaService;
    private final CloudSolrClient solrClient;

    private final SequentialEngine sequentialEngine;

    public void initiateFulltextInstance(FulltextTaskConfig fulltextTaskConfig) throws Exception {
        SolrSchema schema = schemaService.createSolrSchema(fulltextTaskConfig.getSchema());
        String collectionName = SolrUtils.buildCollectionName(fulltextTaskConfig);
        String basePath = fulltextTaskConfig.getProject().getProjectName() + "." + fulltextTaskConfig.getName();
        Queue<SequentialTask> tasks = getSequentialTasksForInitiateFulltextInstance(fulltextTaskConfig, collectionName, basePath, schema);
        sequentialEngine.performSequential(tasks);
    }

    public List<MapJson> query(FulltextTaskConfig fulltextTaskConfig, SolrQueryDto solrQueryDto) throws SolrServerException, IOException {
        QueryRequest request = new QueryRequest(getSorlQueryFromDto(solrQueryDto));
        QueryResponse response = this.sendRequest(request, SolrUtils.buildReadCollectionName(fulltextTaskConfig.getProject().getProjectName(), fulltextTaskConfig.getName()));
        return response.getResults().stream().map(entries -> {
            MapJson mapJson = new MapJson();
            mapJson.putAll(entries);
            return mapJson;
        }).toList();
    }

    private static SolrQuery getSorlQueryFromDto(SolrQueryDto solrQueryDto) {
        SolrQuery query = new SolrQuery(solrQueryDto.getQuery());
        if (solrQueryDto.getSort() != null) {
            solrQueryDto.getSort().forEach(solrSort -> query.setSort(solrSort.getField(), solrSort.getOrder()));
        }
        if (solrQueryDto.getFilters() != null) {
            query.setFilterQueries(solrQueryDto.getFilters().toArray(new String[0]));
        }
        if(solrQueryDto.getReqFields() != null) {
            query.setFields(solrQueryDto.getReqFields().toArray(new String[0]));
        }
        return query;
    }


    private Queue<SequentialTask> getSequentialTasksForInitiateFulltextInstance(FulltextTaskConfig fulltextTaskConfig, String collectionName, String basePath, SolrSchema schema) {
        Queue<SequentialTask> tasks = new LinkedList<>();
        tasks.add(new SequentialTask() {
            @Override
            public void perform(Map<String, String> context) throws SolrServerException, IOException {
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
            public void rollback(Map<String, String> context) throws SolrServerException, IOException {
                ConfigSetAdminRequest.Delete delete = new ConfigSetAdminRequest.Delete();
                delete.setConfigSetName(basePath);
                sendRequest(delete);

            }
        });

        tasks.add(new SequentialTask() {
            @Override
            public void perform(Map<String, String> context) throws SolrServerException, IOException {
                ConfigSetAdminRequest.Upload uploadConfigSet = new ConfigSetAdminRequest.Upload();
                uploadConfigSet.setFilePath("managed-schema.xml");
                uploadConfigSet.setOverwrite(true);
                uploadConfigSet.setUploadFile(schema.getDefaultSchemaFile(), ContentStreamBase.TEXT_XML);
                uploadConfigSet.setConfigSetName(basePath);
                sendRequest(uploadConfigSet);
            }

            @Override
            public void rollback(Map<String, String> context) {
                // nothing to do
            }
        });


        tasks.add(new SequentialTask() {
            @Override
            public void perform(Map<String, String> context) throws SolrServerException, IOException {
                CollectionAdminRequest.Create collection = CollectionAdminRequest.createCollection(collectionName, basePath, fulltextTaskConfig.getShardsCount(), fulltextTaskConfig.getReplicationFactor());

                sendRequest(collection);

            }

            @Override
            public void rollback(Map<String, String> context) throws SolrServerException, IOException {
                CollectionAdminRequest.Delete deleteCollection = CollectionAdminRequest.deleteCollection(collectionName);
                sendRequest(deleteCollection);
            }
        });

        tasks.add(new SequentialTask() {
            @Override
            public void perform(Map<String, String> context) throws SolrServerException, IOException {
                for (Map<String, Object> fieldDesc : schema.getFieldsDescriptions()) {
                    SchemaRequest.AddField addField = new SchemaRequest.AddField(fieldDesc);
                    sendRequest(addField, collectionName);
                }
            }

            @Override
            public void rollback(Map<String, String> context) {
                // nothing to do
            }
        });

        tasks.add(new SequentialTask() {
            @Override
            public void perform(Map<String, String> context) throws SolrServerException, IOException {
                String readAliasName = basePath + "_READ";
                CollectionAdminRequest.CreateAlias createAlias = CollectionAdminRequest.createAlias(readAliasName, collectionName);
                sendRequest(createAlias);

            }

            @Override
            public void rollback(Map<String, String> context) throws SolrServerException, IOException {
                CollectionAdminRequest.DeleteAlias deleteAlias = CollectionAdminRequest.deleteAlias(basePath + "_READ");
                sendRequest(deleteAlias);
            }
        });

        tasks.add(new SequentialTask() {
            @Override
            public void perform(Map<String, String> context) throws SolrServerException, IOException {
                String readAliasName = basePath + "_WRITE";
                CollectionAdminRequest.CreateAlias createAlias = CollectionAdminRequest.createAlias(readAliasName, collectionName);
                sendRequest(createAlias);
            }

            @Override
            public void rollback(Map<String, String> context) throws SolrServerException, IOException {
                CollectionAdminRequest.DeleteAlias deleteAlias = CollectionAdminRequest.deleteAlias(basePath + "_WRITE");
                sendRequest(deleteAlias);
            }
        });
        return tasks;
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
