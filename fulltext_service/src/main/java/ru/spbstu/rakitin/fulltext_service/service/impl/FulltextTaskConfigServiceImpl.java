package ru.spbstu.rakitin.fulltext_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.spbstu.rakitin.commonstarter.admin.exception.ForbiddenRequestException;
import ru.spbstu.rakitin.commonstarter.exception.InvalidSchemaException;
import ru.spbstu.rakitin.commonstarter.service.SchemaValidationService;
import ru.spbstu.rakitin.fulltext_service.dto.FulltextTaskConfigMapper;
import ru.spbstu.rakitin.fulltext_service.exception.ConfigAlreadyExists;
import ru.spbstu.rakitin.fulltext_service.exception.FulltextConfigNotFoundException;
import ru.spbstu.rakitin.fulltext_service.exception.FulltextQuotaExceededException;
import ru.spbstu.rakitin.fulltext_service.exception.UnavailableTopicException;
import ru.spbstu.rakitin.fulltext_service.model.FulltextTaskConfig;
import ru.spbstu.rakitin.fulltext_service.repository.FulltextTaskConfigRepository;
import ru.spbstu.rakitin.fulltext_service.service.FulltextTaskConfigService;

@Service
@RequiredArgsConstructor
public class FulltextTaskConfigServiceImpl implements FulltextTaskConfigService {

    private final SchemaValidationService schemaValidationService;
    private final FulltextTaskConfigRepository fulltextTaskConfigRepository;
    private final FulltextTaskConfigMapper fulltextTaskConfigMapper;

    @Override
    public void createConfig(FulltextTaskConfig config, Authentication authentication) throws ConfigAlreadyExists, FulltextQuotaExceededException, ForbiddenRequestException, UnavailableTopicException, InvalidSchemaException {
        if (fulltextTaskConfigRepository.existsByNameAndProjectId(config.getName(), config.getProject().getId())) {
            throw new ConfigAlreadyExists(String.format("Config with name %s already exists in project %s", config.getName(), config.getProject().getProjectName()));
        }
        if (config.getProject().getFulltextQuota() <= fulltextTaskConfigRepository.countByProjectId(config.getProject().getId())) {
            throw new FulltextQuotaExceededException("Fulltext quota exceeded for project " + config.getProject().getProjectName());
        }
        if (!config.getTopic().getProject().getId().equals(config.getProject().getId())) {
            throw new UnavailableTopicException("The topic for the task must be from the same project as the task itself.");
        }
        try {
            schemaValidationService.validateSchema(fulltextTaskConfigMapper.mapFulltextSchemaToSchemaDto(config.getSchema()));
        } catch (InvalidSchemaException invalidSchemaException) {
            throw new InvalidSchemaException(String.format("Unable to create config for fulltext task %s because schema is invalid!", config.getName()), invalidSchemaException);
        }
        fulltextTaskConfigRepository.save(config);

    }


    @Override
    public FulltextTaskConfig findById(long id) throws FulltextConfigNotFoundException {
        return fulltextTaskConfigRepository.findById(id).orElseThrow(() -> new FulltextConfigNotFoundException("Fulltext config with id %s not found!".formatted(id)));
    }

}
