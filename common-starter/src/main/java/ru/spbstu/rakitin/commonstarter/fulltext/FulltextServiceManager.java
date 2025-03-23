package ru.spbstu.rakitin.commonstarter.fulltext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.spbstu.rakitin.commonstarter.discovery.AdminUserService;
import ru.spbstu.rakitin.commonstarter.discovery.InnerServiceRequestFactory;
import ru.spbstu.rakitin.commonstarter.discovery.ServiceName;
import ru.spbstu.rakitin.commonstarter.dto.fulltext.FulltextJobDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FulltextServiceManager {

    private static final String CHANGE_STATUS = "/api/v1/internal/fulltext/instance/%s/status/%s";
    private static final String FIND_ALL_BY_STATUS = "/api/v1/internal/fulltext/instance/status/%s";
    private static final String RESUME = "/api/v1/fulltext/instance/resume/%s";
    private static final String SUSPEND = "/api/v1/fulltext/instance/suspend/%s";
    private static final String UPDATE = "/api/v1/fulltext/instance/update/%s";

    private final InnerServiceRequestFactory requestFactory;
    private final AdminUserService adminUserService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void changeTaskStatus(long taskId, String taskStatus) {
        requestFactory.doPost(ServiceName.FULL_TEXT, adminUserService.getJwt(), String.format(CHANGE_STATUS, taskId, taskStatus), null, Void.TYPE);
    }

    public List<FulltextJobDto> findAllByStatus(String taskStatus) {
        String rawResult = requestFactory.doGet(ServiceName.FULL_TEXT, adminUserService.getJwt(), String.format(FIND_ALL_BY_STATUS, taskStatus), String.class);
        try {
            return objectMapper.readValue(rawResult, new TypeReference<List<FulltextJobDto>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public long resume(long configId, Authentication authentication) {
        return requestFactory.doPost(ServiceName.FULL_TEXT, authentication, String.format(RESUME, configId), null, Long.class);
    }

    public void suspendTask(long configId, Authentication authentication) {
        requestFactory.doPost(ServiceName.FULL_TEXT, authentication, String.format(SUSPEND, configId), null, Void.TYPE);
    }

    public void update(long configId, Authentication authentication) {
        requestFactory.doPost(ServiceName.FULL_TEXT, authentication, String.format(UPDATE, configId), null, Void.TYPE);
    }
}
