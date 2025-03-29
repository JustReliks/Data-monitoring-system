package ru.spbstu.rakitin.monitoring_service.service;

import org.springframework.security.core.Authentication;
import ru.spbstu.rakitin.commonstarter.dto.monitoring.CreateReadApiKeyDto;
import ru.spbstu.rakitin.monitoring_service.exception.OrganizationNotFoundException;

public interface ApiKeyService {

    String createApiKey(CreateReadApiKeyDto readApiKeyDto, Authentication authentication) throws OrganizationNotFoundException;

}
