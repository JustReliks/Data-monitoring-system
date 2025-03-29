package ru.spbstu.rakitin.monitoring_service.service;

import org.springframework.security.core.Authentication;
import ru.spbstu.rakitin.commonstarter.exception.ConfigAlreadyExists;
import ru.spbstu.rakitin.commonstarter.exception.InvalidSchemaException;
import ru.spbstu.rakitin.commonstarter.exception.QuotaExceededException;
import ru.spbstu.rakitin.commonstarter.exception.UnavailableTopicException;
import ru.spbstu.rakitin.monitoring_service.exception.*;
import ru.spbstu.rakitin.monitoring_service.model.MonitoringTaskConfig;

import java.util.Collection;
import java.util.List;

public interface MonitoringTaskConfigService {
    long createConfig(MonitoringTaskConfig monitoringTaskConfig, Authentication authentication) throws ConfigAlreadyExists, QuotaExceededException, UnavailableTopicException, InvalidSchemaException;

    MonitoringTaskConfig getConfig(long id, Authentication authentication) throws MonitoringTaskConfigNotFoundException;

    List<MonitoringTaskConfig> findForProjects(List<Long> projects, Authentication authentication);

    void removeConfig(Long configId, boolean forceDelete, Authentication authentication) throws MonitoringTaskConfigNotFoundException, MonitoringConfigDeletionForbiddenException, MonitoringStatusWontChangedException, MonitoringTaskInstanceNotFoundException;

    MonitoringTaskConfig findById(Long configId, Authentication authentication) throws MonitoringTaskConfigNotFoundException;

    void updateConfig(long configId, MonitoringTaskConfig monitoringTaskConfig, Authentication authentication) throws MonitoringTaskConfigNotFoundException, InvalidSchemaException, MonitoringConfigUpdateException;
}
