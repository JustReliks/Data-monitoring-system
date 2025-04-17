package ru.spbstu.rakitin.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.spbstu.rakitin.client.MdsClient;
import ru.spbstu.rakitin.client.MdsResponse;
import ru.spbstu.rakitin.dto.TaskClientDto;
import ru.spbstu.rakitin.dto.monitoring.ApiKeyDto;
import ru.spbstu.rakitin.model.MonitoringToken;
import ru.spbstu.rakitin.repository.MonitoringTokenRepository;
import ru.spbstu.rakitin.requests.monitoring.CreateApiKey;
import ru.spbstu.rakitin.service.MonitoringTokenService;

import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
public class MonitoringTokenServiceImpl implements MonitoringTokenService {

    private final MonitoringTokenRepository monitoringTokenRepository;
    private final MdsClient mdsClient;
    private final TaskClientDto taskClientDto;

    public MonitoringTokenServiceImpl(MonitoringTokenRepository monitoringTokenRepository,
                                      MdsClient mdsClient,
                                      @Qualifier("taskClientDtoMonitoring") TaskClientDto taskClientDto) {
        this.monitoringTokenRepository = monitoringTokenRepository;
        this.mdsClient = mdsClient;
        this.taskClientDto = taskClientDto;
    }

    @PostConstruct
    public void onInit() {
        Optional<MonitoringToken> any = monitoringTokenRepository.findFirstByOrderByCreatedAtDesc();
        if (any.isPresent()) {
            log.info("Found monitoring token: {}", any.get());
        } else {
            log.info("Creating monitoring token");
            MdsResponse<ApiKeyDto> apiTokenForLibrary = mdsClient.sendRequest(new CreateApiKey(taskClientDto, "Api token for library"));
            MonitoringToken monitoringToken = new MonitoringToken();
            monitoringToken.setCreatedAt(new Date());
            monitoringToken.setToken(apiTokenForLibrary.getResponse().get().getApiKey());
            monitoringTokenRepository.save(monitoringToken);
            log.info("Created monitoring token: {}", monitoringToken);
        }
    }

}
