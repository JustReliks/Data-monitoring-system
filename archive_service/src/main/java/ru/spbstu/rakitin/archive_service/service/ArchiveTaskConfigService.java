package ru.spbstu.rakitin.archive_service.service;

import ru.spbstu.rakitin.archive_service.model.ArchiveTaskConfig;
import ru.spbstu.rakitin.commonstarter.exception.ConfigAlreadyExists;
import ru.spbstu.rakitin.commonstarter.exception.InvalidSchemaException;
import ru.spbstu.rakitin.commonstarter.exception.QuotaExceededException;
import ru.spbstu.rakitin.commonstarter.exception.UnavailableTopicException;

public interface ArchiveTaskConfigService {

    long createArchiveTaskConfig(ArchiveTaskConfig archiveTaskConfig) throws InvalidSchemaException, UnavailableTopicException, ConfigAlreadyExists, QuotaExceededException;

}
