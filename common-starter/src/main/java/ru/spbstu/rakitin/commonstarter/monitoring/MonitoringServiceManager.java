package ru.spbstu.rakitin.commonstarter.monitoring;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.spbstu.rakitin.commonstarter.discovery.AdminUserService;
import ru.spbstu.rakitin.commonstarter.discovery.InnerServiceRequestFactory;
import ru.spbstu.rakitin.commonstarter.utils.Utils;
import ru.spbstu.rakitin.dto.ServiceName;
import ru.spbstu.rakitin.dto.monitoring.*;

import java.util.List;

import static ru.spbstu.rakitin.dto.ParametrizedTypes.*;

@Service
@RequiredArgsConstructor
public class MonitoringServiceManager {

    private static final String CHANGE_STATUS = "/api/v1/internal/monitoring/instance/%s/status/%s";
    private static final String FIND_ALL_BY_STATUS = "/api/v1/internal/monitoring/instance/status/%s";
    private static final String CREATE_CONFIG = "/api/v1//monitoring/config/create";
    private static final String REMOVE_CONFIG = "/api/v1/monitoring/config/%s/delete?forceDelete=%s";
    private static final String UPDATE_CONFIG = "/api/v1/monitoring/config/%s/update";
    private static final String LIST_CONFIG = "/api/v1/monitoring/config/list?projects=%s";
    private static final String RESUME = "/api/v1/monitoring/instance/resume/%s";
    private static final String SUSPEND = "/api/v1/monitoring/instance/suspend/%s";
    private static final String UPDATE = "/api/v1/monitoring/instance/update/%s";
    private static final String CREATE_API_KEY = "/api/v1/monitoring/key/create";
    private static final String FIND_BY_ID = "/api/v1/monitoring/config/%s";
    private static final String FIND_BY_NAME = "/api/v1/monitoring/config/name/%s?projects=%s";


    private final InnerServiceRequestFactory requestFactory;
    private final AdminUserService adminUserService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void changeTaskStatus(long taskId, String taskStatus) {
        requestFactory.doPost(ServiceName.MONITORING, adminUserService.getJwt(), String.format(CHANGE_STATUS, taskId, taskStatus), null, VOID_TYPE);
    }

    public List<MonitoringJobDto> findAllByStatus(String taskStatus) {
        String rawResult = requestFactory.doGet(ServiceName.MONITORING, adminUserService.getJwt(), String.format(FIND_ALL_BY_STATUS, taskStatus), STRING_TYPE);
        try {
            return objectMapper.readValue(rawResult, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public long createConfig(MonitoringTaskConfigDto configDto, Authentication authentication) {
        return requestFactory.doPost(ServiceName.MONITORING, authentication, String.format(CREATE_CONFIG), configDto, LONG_TYPE);
    }

    public List<MonitoringTaskResponse> list(List<Long> projects, Authentication authentication) {
        return requestFactory.doGet(ServiceName.MONITORING, authentication, String.format(LIST_CONFIG, Utils.getParamsStringFromArray(projects)), LIST_MONITORING_TASK_RESPONSE_TYPE_REFERENCE);
    }

    public void removeConfig(Long configId, boolean forceDelete, Authentication authentication) {
        requestFactory.doDelete(ServiceName.MONITORING, authentication, String.format(REMOVE_CONFIG, configId, forceDelete), null, VOID_TYPE);
    }

    public void updateConfig(long configId, MonitoringTaskConfigDto configDto, Authentication authentication) {
        requestFactory.doPut(ServiceName.MONITORING, authentication, String.format(UPDATE_CONFIG, configId), configDto, VOID_TYPE);
    }

    public long resume(long configId, Authentication authentication) {
        return requestFactory.doPost(ServiceName.MONITORING, authentication, String.format(RESUME, configId), null, LONG_TYPE);
    }

    public void suspendTask(long configId, Authentication authentication) {
        requestFactory.doPost(ServiceName.MONITORING, authentication, String.format(SUSPEND, configId), null, VOID_TYPE);
    }

    public void update(long configId, Authentication authentication) {
        requestFactory.doPost(ServiceName.MONITORING, authentication, String.format(UPDATE, configId), null, VOID_TYPE);
    }

    public ApiKeyDto createApiKey(CreateReadApiKeyDto createReadApiKeyDto, Authentication authentication) {
        return requestFactory.doPost(ServiceName.MONITORING, authentication, CREATE_API_KEY, createReadApiKeyDto, API_KEY_REFERENCE);
    }

    public MonitoringTaskResponse findById(long taskId, Authentication authentication) {
        return requestFactory.doGet(ServiceName.MONITORING, authentication, String.format(FIND_BY_ID, taskId), MONITORING_TASK_RESPONSE);
    }

    public MonitoringTaskResponse findByName(String taskName, Long projectId, Authentication authentication) {
        return requestFactory.doGet(ServiceName.MONITORING, authentication, String.format(FIND_BY_NAME, taskName, projectId), MONITORING_TASK_RESPONSE);
    }
}
