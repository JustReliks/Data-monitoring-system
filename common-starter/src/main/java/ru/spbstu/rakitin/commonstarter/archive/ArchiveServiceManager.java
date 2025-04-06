package ru.spbstu.rakitin.commonstarter.archive;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.spbstu.rakitin.commonstarter.discovery.AdminUserService;
import ru.spbstu.rakitin.commonstarter.discovery.InnerServiceRequestFactory;
import ru.spbstu.rakitin.commonstarter.discovery.ServiceName;
import ru.spbstu.rakitin.commonstarter.dto.archive.*;
import ru.spbstu.rakitin.commonstarter.utils.Utils;

import java.util.List;

import static ru.spbstu.rakitin.commonstarter.discovery.ParametrizedTypes.*;

@Service
@RequiredArgsConstructor
public class ArchiveServiceManager {

    private static final String CHANGE_STATUS = "/api/v1/internal/archive/instance/%s/status/%s";
    private static final String FIND_ALL_BY_STATUS = "/api/v1/internal/archive/instance/status/%s";
    private static final String CREATE_CONFIG = "/api/v1//archive/config/create";
    private static final String REMOVE_CONFIG = "/api/v1/archive/config/%s/delete?forceDelete=%s";
    private static final String UPDATE_CONFIG = "/api/v1/archive/config/%s/update";
    private static final String LIST_CONFIG = "/api/v1/archive/config/list?projects=%s";
    private static final String RESUME = "/api/v1/archive/instance/resume/%s";
    private static final String SUSPEND = "/api/v1/archive/instance/suspend/%s";
    private static final String UPDATE = "/api/v1/archive/instance/update/%s";
    private static final String FILE_LIST = "/api/v1/archive/query/%s/list";
    private static final String GET_FILE = "/api/v1/archive/query/%s/file/%s";

    private final InnerServiceRequestFactory requestFactory;
    private final AdminUserService adminUserService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void changeTaskStatus(long taskId, String taskStatus) {
        requestFactory.doPost(ServiceName.ARCHIVE, adminUserService.getJwt(), String.format(CHANGE_STATUS, taskId, taskStatus), null, VOID_TYPE);
    }

    public List<ArchiveJobDto> findAllByStatus(String taskStatus) {
        String rawResult = requestFactory.doGet(ServiceName.ARCHIVE, adminUserService.getJwt(), String.format(FIND_ALL_BY_STATUS, taskStatus), STRING_TYPE);
        try {
            return objectMapper.readValue(rawResult, new TypeReference<List<ArchiveJobDto>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public long createConfig(ArchiveTaskConfigDto configDto, Authentication authentication) {
        return requestFactory.doPost(ServiceName.ARCHIVE, authentication, String.format(CREATE_CONFIG), configDto, LONG_TYPE);
    }

    public List<ArchiveTaskResponse> list(List<Long> projects, Authentication authentication) {
        return requestFactory.doGet(ServiceName.ARCHIVE, authentication, String.format(LIST_CONFIG, Utils.getParamsStringFromArray(projects)), LIST_ARCHIVE_TASK_RESPONSE_TYPE_REFERENCE);
    }

    public void removeConfig(Long configId, boolean forceDelete, Authentication authentication) {
        requestFactory.doDelete(ServiceName.ARCHIVE, authentication, String.format(REMOVE_CONFIG, configId, forceDelete), null, VOID_TYPE);
    }

    public void updateConfig(long configId, ArchiveTaskConfigDto configDto, Authentication authentication) {
        requestFactory.doPut(ServiceName.ARCHIVE, authentication, String.format(UPDATE_CONFIG, configId), configDto, VOID_TYPE);
    }

    public long resume(long configId, Authentication authentication) {
        return requestFactory.doPost(ServiceName.ARCHIVE, authentication, String.format(RESUME, configId), null, LONG_TYPE);
    }

    public void suspendTask(long configId, Authentication authentication) {
        requestFactory.doPost(ServiceName.ARCHIVE, authentication, String.format(SUSPEND, configId), null, VOID_TYPE);
    }

    public void update(long configId, Authentication authentication) {
        requestFactory.doPut(ServiceName.ARCHIVE, authentication, String.format(UPDATE, configId), null, VOID_TYPE);
    }

    public List<FileInformationDto> getAllFilesForTask(long configId, Authentication authentication) {
        return requestFactory.doGet(ServiceName.ARCHIVE, authentication, String.format(FILE_LIST, configId), FILE_INFORMATION_LIST);
    }

    public FileDto getFile(long configId, String filename, Authentication authentication) {
        return requestFactory.doGet(ServiceName.ARCHIVE, authentication, String.format(GET_FILE, configId, filename), FILE);
    }
}
