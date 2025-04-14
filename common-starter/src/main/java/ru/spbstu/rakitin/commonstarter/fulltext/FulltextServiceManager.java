package ru.spbstu.rakitin.commonstarter.fulltext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.spbstu.rakitin.commonstarter.discovery.AdminUserService;
import ru.spbstu.rakitin.commonstarter.discovery.InnerServiceRequestFactory;
import ru.spbstu.rakitin.commonstarter.utils.Utils;
import ru.spbstu.rakitin.dto.MapJson;
import ru.spbstu.rakitin.dto.ServiceName;
import ru.spbstu.rakitin.dto.fulltext.FulltextJobDto;
import ru.spbstu.rakitin.dto.fulltext.FulltextTaskConfigDto;
import ru.spbstu.rakitin.dto.fulltext.FulltextTaskResponse;
import ru.spbstu.rakitin.dto.fulltext.SolrQueryDto;

import java.util.List;

import static ru.spbstu.rakitin.dto.ParametrizedTypes.*;

@Service
@RequiredArgsConstructor
public class FulltextServiceManager {

    private static final String CHANGE_STATUS = "/api/v1/internal/fulltext/instance/%s/status/%s";
    private static final String FIND_ALL_BY_STATUS = "/api/v1/internal/fulltext/instance/status/%s";
    private static final String RESUME = "/api/v1/fulltext/instance/resume/%s";
    private static final String SUSPEND = "/api/v1/fulltext/instance/suspend/%s";
    private static final String UPDATE = "/api/v1/fulltext/instance/update/%s";
    private static final String CREATE_CONFIG = "/api/v1//fulltext/config/create";
    private static final String REMOVE_CONFIG = "/api/v1/fulltext/config/%s/delete?forceDelete=%s";
    private static final String UPDATE_CONFIG = "/api/v1/fulltext/config/%s/update";
    private static final String LIST_CONFIG = "/api/v1/fulltext/config/list?projects=%s";
    private static final String QUERY = "/api/v1/fulltext/query/%s";
    private static final String FIND_BY_ID = "/api/v1/fulltext/config/%s";
    private static final String FIND_BY_NAME = "/api/v1/fulltext/config/name/%s?projectId=%s";

    private final InnerServiceRequestFactory requestFactory;
    private final AdminUserService adminUserService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void changeTaskStatus(long taskId, String taskStatus) {
        requestFactory.doPost(ServiceName.FULL_TEXT, adminUserService.getJwt(), String.format(CHANGE_STATUS, taskId, taskStatus), null, VOID_TYPE);
    }

    public List<FulltextJobDto> findAllByStatus(String taskStatus) {
        String rawResult = requestFactory.doGet(ServiceName.FULL_TEXT, adminUserService.getJwt(), String.format(FIND_ALL_BY_STATUS, taskStatus), STRING_TYPE);
        try {
            return objectMapper.readValue(rawResult, new TypeReference<List<FulltextJobDto>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public long resume(long configId, Authentication authentication) {
        return requestFactory.doPost(ServiceName.FULL_TEXT, authentication, String.format(RESUME, configId), null, LONG_TYPE);
    }

    public void suspendTask(long configId, Authentication authentication) {
        requestFactory.doPost(ServiceName.FULL_TEXT, authentication, String.format(SUSPEND, configId), null, VOID_TYPE);
    }

    public void update(long configId, Authentication authentication) {
        requestFactory.doPut(ServiceName.FULL_TEXT, authentication, String.format(UPDATE, configId), null, VOID_TYPE);
    }

    public long createConfig(FulltextTaskConfigDto configDto, Authentication authentication) {
        return requestFactory.doPost(ServiceName.FULL_TEXT, authentication, String.format(CREATE_CONFIG), configDto, LONG_TYPE);
    }

    public List<FulltextTaskResponse> list(List<Long> projects, Authentication authentication) {
        return requestFactory.doGet(ServiceName.FULL_TEXT, authentication, String.format(LIST_CONFIG, Utils.getParamsStringFromArray(projects)), LIST_FULLTEXT_TASK_RESPONSE_TYPE_REFERENCE);
    }

    public void removeConfig(Long configId, boolean forceDelete, Authentication authentication) {
        requestFactory.doDelete(ServiceName.FULL_TEXT, authentication, String.format(REMOVE_CONFIG, configId, forceDelete), null, VOID_TYPE);
    }

    public void updateConfig(long configId, FulltextTaskConfigDto configDto, Authentication authentication) {
        requestFactory.doPut(ServiceName.FULL_TEXT, authentication, String.format(UPDATE_CONFIG, configId), configDto, VOID_TYPE);
    }


    public List<MapJson> query(SolrQueryDto solrQuery, long taskId, Authentication authentication) {
        return requestFactory.doPut(ServiceName.FULL_TEXT, authentication, String.format(QUERY, taskId), solrQuery, MAP_JSON_LIST);
    }

    public FulltextTaskResponse findById(long taskId, Authentication authentication) {
        return requestFactory.doGet(ServiceName.FULL_TEXT, authentication, String.format(FIND_BY_ID, taskId), FULLTEXT_TASK_RESPONSE);
    }

    public FulltextTaskResponse findByName(String taskName, Long projectId, Authentication authentication) {
        return requestFactory.doGet(ServiceName.FULL_TEXT, authentication, String.format(FIND_BY_NAME, taskName, projectId), FULLTEXT_TASK_RESPONSE);
    }
}
