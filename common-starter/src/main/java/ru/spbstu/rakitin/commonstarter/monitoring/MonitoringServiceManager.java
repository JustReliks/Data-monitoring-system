package ru.spbstu.rakitin.commonstarter.monitoring;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.spbstu.rakitin.commonstarter.discovery.AdminUserService;
import ru.spbstu.rakitin.commonstarter.discovery.InnerServiceRequestFactory;
import ru.spbstu.rakitin.commonstarter.discovery.ServiceName;
import ru.spbstu.rakitin.commonstarter.dto.monitoring.MonitoringJobDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MonitoringServiceManager {

    private static final String CHANGE_STATUS = "/api/v1/internal/monitoring/instance/%s/status/%s";
    private static final String FIND_ALL_BY_STATUS = "/api/v1/internal/monitoring/instance/status/%s";

    private final InnerServiceRequestFactory requestFactory;
    private final AdminUserService adminUserService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void changeTaskStatus(long taskId, String taskStatus) {
        requestFactory.doPost(ServiceName.MONITORING, adminUserService.getJwt(), String.format(CHANGE_STATUS, taskId, taskStatus), null, Void.TYPE);
    }

    public List<MonitoringJobDto> findAllByStatus(String taskStatus) {
        String rawResult = requestFactory.doGet(ServiceName.MONITORING, adminUserService.getJwt(), String.format(FIND_ALL_BY_STATUS, taskStatus), String.class);
        try {
            return objectMapper.readValue(rawResult, new TypeReference<List<MonitoringJobDto>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
