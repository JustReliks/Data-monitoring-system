package ru.spbstu.rakitin.user_api_service.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.spbstu.rakitin.commonstarter.datamanagement.DataManagementManager;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/kafka")
@RequiredArgsConstructor
@Tag(name = "12. Операции с конфигурацией Kafka")
public class KafkaController {

    private final DataManagementManager dataManagementManager;

    @GetMapping("/properties")
    @Operation(description = "Получение публичной конфигурации Kafka кластера")
    public Map<String, Object> getKafkaProperties(Authentication authentication) {
        return dataManagementManager.getPublicProperties(authentication);
    }


}
