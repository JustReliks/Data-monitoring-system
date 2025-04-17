package ru.spbstu.rakitin.monitoring_service.api;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.spbstu.rakitin.commonstarter.admin.aspect.LogController;
import ru.spbstu.rakitin.commonstarter.admin.aspect.ProjectIdContainer;
import ru.spbstu.rakitin.dto.monitoring.ApiKeyDto;
import ru.spbstu.rakitin.dto.monitoring.CreateReadApiKeyDto;
import ru.spbstu.rakitin.monitoring_service.exception.OrganizationNotFoundException;
import ru.spbstu.rakitin.monitoring_service.service.ApiKeyService;

@RestController
@RequestMapping("/api/v1/monitoring/key")
@RequiredArgsConstructor
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    @PostMapping("/create")
    @LogController
    public ApiKeyDto createReadApiKey(@RequestBody @ProjectIdContainer(innerFieldName = "projectId") CreateReadApiKeyDto readApiKeyDto, Authentication authentication) throws OrganizationNotFoundException {
        return new ApiKeyDto(apiKeyService.createApiKey(readApiKeyDto, authentication));
    }

}
