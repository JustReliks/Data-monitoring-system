package ru.spbstu.rakitin.archive_service.service;

import org.springframework.security.core.Authentication;
import ru.spbstu.rakitin.archive_service.exception.*;
import ru.spbstu.rakitin.archive_service.model.ArchiveTaskConfig;
import ru.spbstu.rakitin.archive_service.model.ArchiveTaskInstance;
import ru.spbstu.rakitin.commonstarter.exception.ConfigAlreadyExists;
import ru.spbstu.rakitin.commonstarter.exception.InvalidSchemaException;
import ru.spbstu.rakitin.commonstarter.exception.QuotaExceededException;
import ru.spbstu.rakitin.commonstarter.exception.UnavailableTopicException;

import java.io.IOException;
import java.util.List;

public interface ArchiveTaskConfigService {

    long createArchiveTaskConfig(ArchiveTaskConfig archiveTaskConfig) throws InvalidSchemaException, UnavailableTopicException, ConfigAlreadyExists, QuotaExceededException;

    void deleteArchiveTaskConfig(Authentication authentication, long configId, boolean forceDelete) throws ArchiveConfigNotFoundException, ArchiveConfigDeletionForbiddenException, ArchiveStatusWontChangedException, ArchiveTaskInstanceNotFoundException, IOException;

    ArchiveTaskConfig findById(long configId, Authentication authentication) throws ArchiveConfigNotFoundException;

    List<ArchiveTaskConfig> findForProjects(List<Long> projectIds, Authentication authentication);

    ArchiveTaskConfig updateArchiveTaskConfig(long configId, ArchiveTaskConfig archiveTaskConfig, Authentication authentication) throws ArchiveConfigNotFoundException, ArchiveTaskInstanceNotFoundException, ArchiveConfigUpdateException, InvalidSchemaException;

    ArchiveTaskConfig findByName(Long projectId, String taskName, Authentication authentication) throws ArchiveConfigNotFoundException;
}
