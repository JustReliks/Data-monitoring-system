package ru.spbstu.rakitin.archive_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.spbstu.rakitin.archive_service.dto.ArchiveTaskConfigMapper;
import ru.spbstu.rakitin.archive_service.model.ArchiveTaskConfig;
import ru.spbstu.rakitin.archive_service.repository.ArchiveTaskConfigRepository;
import ru.spbstu.rakitin.archive_service.service.ArchiveTaskConfigService;
import ru.spbstu.rakitin.commonstarter.dto.archive.ArchiveTaskSchemaDto;
import ru.spbstu.rakitin.commonstarter.exception.ConfigAlreadyExists;
import ru.spbstu.rakitin.commonstarter.exception.InvalidSchemaException;
import ru.spbstu.rakitin.commonstarter.exception.QuotaExceededException;
import ru.spbstu.rakitin.commonstarter.exception.UnavailableTopicException;
import ru.spbstu.rakitin.commonstarter.service.SchemaValidationService;

@Service
@RequiredArgsConstructor
public class ArchiveTaskConfigServiceImpl implements ArchiveTaskConfigService {

    private final ArchiveTaskConfigRepository archiveTaskConfigRepository;
    private final SchemaValidationService<ArchiveTaskSchemaDto> schemaValidationService;
    private final ArchiveTaskConfigMapper archiveTaskConfigMapper;


    @Transactional
    @Override
    public long createArchiveTaskConfig(ArchiveTaskConfig config) throws InvalidSchemaException, UnavailableTopicException, ConfigAlreadyExists, QuotaExceededException {

        if (archiveTaskConfigRepository.existsByNameAndProjectId(config.getName(), config.getProject().getId())) {
            throw new ConfigAlreadyExists(String.format("Config with name %s already exists in project %s", config.getName(), config.getProject().getProjectName()));
        }
        if (config.getProject().getArchiveQuota() <= archiveTaskConfigRepository.countByProjectId(config.getProject().getId())) {
            throw new QuotaExceededException("Archive quota exceeded for project " + config.getProject().getProjectName());
        }
        if (!config.getTopic().getProject().getId().equals(config.getProject().getId())) {
            throw new UnavailableTopicException("The topic for the task must be from the same project as the task itself.");
        }


        schemaValidationService.validateSchema(archiveTaskConfigMapper.mapArchiveTaskSchemaToArchiveTaskSchemaDto(config.getSchema()));

        return archiveTaskConfigRepository.save(config).getId();
    }
}
