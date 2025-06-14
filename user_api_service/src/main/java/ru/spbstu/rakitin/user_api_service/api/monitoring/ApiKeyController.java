package ru.spbstu.rakitin.user_api_service.api.monitoring;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.spbstu.rakitin.dto.monitoring.ApiKeyDto;
import ru.spbstu.rakitin.dto.monitoring.CreateReadApiKeyDto;
import ru.spbstu.rakitin.commonstarter.monitoring.MonitoringServiceManager;

@RestController
@RequestMapping("/api/v1/monitoring/key")
@Tag(name = "7. Операции с апи ключами доступа к данным задач мониторинга")
@RequiredArgsConstructor
public class ApiKeyController {

    private final MonitoringServiceManager monitoringServiceManager;

    @PostMapping("/create")
    @Operation(description = "Создание API ключа для доступа к данным задачи мониторинга")
    public ApiKeyDto createApiKey(@RequestBody  CreateReadApiKeyDto createReadApiKeyDto, Authentication authentication) {
        return monitoringServiceManager.createApiKey(createReadApiKeyDto, authentication);
    }

}
