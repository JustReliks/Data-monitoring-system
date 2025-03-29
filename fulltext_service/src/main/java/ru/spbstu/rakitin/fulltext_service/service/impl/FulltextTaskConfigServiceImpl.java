package ru.spbstu.rakitin.fulltext_service.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.spbstu.rakitin.commonentites.model.PermissionTypeEnum;
import ru.spbstu.rakitin.commonstarter.admin.AdminManager;
import ru.spbstu.rakitin.commonstarter.admin.exception.ForbiddenRequestException;
import ru.spbstu.rakitin.commonstarter.dto.TaskSchemaDto;
import ru.spbstu.rakitin.commonstarter.dto.TaskStatus;
import ru.spbstu.rakitin.commonstarter.exception.ConfigAlreadyExists;
import ru.spbstu.rakitin.commonstarter.exception.InvalidSchemaException;
import ru.spbstu.rakitin.commonstarter.exception.QuotaExceededException;
import ru.spbstu.rakitin.commonstarter.exception.UnavailableTopicException;
import ru.spbstu.rakitin.commonstarter.service.SchemaValidationService;
import ru.spbstu.rakitin.fulltext_service.dto.FulltextTaskConfigMapper;
import ru.spbstu.rakitin.fulltext_service.exception.*;
import ru.spbstu.rakitin.fulltext_service.model.FulltextTaskConfig;
import ru.spbstu.rakitin.fulltext_service.model.FulltextTaskInstance;
import ru.spbstu.rakitin.fulltext_service.repository.FulltextTaskConfigRepository;
import ru.spbstu.rakitin.fulltext_service.service.FulltextTaskConfigService;
import ru.spbstu.rakitin.fulltext_service.service.FulltextTaskInstanceService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional(isolation = Isolation.READ_COMMITTED)
public class FulltextTaskConfigServiceImpl implements FulltextTaskConfigService {

    private final SchemaValidationService<TaskSchemaDto> schemaValidationService;
    private final FulltextTaskConfigRepository fulltextTaskConfigRepository;
    private final FulltextTaskConfigMapper fulltextTaskConfigMapper;
    private final FulltextTaskInstanceService fulltextTaskInstanceService;
    private final AdminManager adminManager;

    public FulltextTaskConfigServiceImpl(SchemaValidationService<TaskSchemaDto> schemaValidationService, FulltextTaskConfigRepository fulltextTaskConfigRepository, FulltextTaskConfigMapper fulltextTaskConfigMapper, FulltextTaskInstanceService fulltextTaskInstanceService, AdminManager adminManager) {
        this.schemaValidationService = schemaValidationService;
        this.fulltextTaskConfigRepository = fulltextTaskConfigRepository;
        this.fulltextTaskConfigMapper = fulltextTaskConfigMapper;
        this.fulltextTaskInstanceService = fulltextTaskInstanceService;
        this.adminManager = adminManager;
    }


    @Override
    public long createConfig(FulltextTaskConfig config, Authentication authentication) throws ConfigAlreadyExists, QuotaExceededException, ForbiddenRequestException, UnavailableTopicException, InvalidSchemaException {
        validateName(config);
        validateQuota(config);
        validateConfig(config);
        return saveConfig(config);

    }

    @Override
    public long saveConfig(FulltextTaskConfig config) throws ConfigAlreadyExists, QuotaExceededException, UnavailableTopicException, InvalidSchemaException {
        validateConfig(config);
        return fulltextTaskConfigRepository.save(config).getId();
    }

    private void validateConfig(FulltextTaskConfig config) throws ConfigAlreadyExists, QuotaExceededException, UnavailableTopicException, InvalidSchemaException {
        if (!config.getTopic().getProject().getId().equals(config.getProject().getId())) {
            throw new UnavailableTopicException("The topic for the task must be from the same project as the task itself.");
        }
        try {
            schemaValidationService.validateSchema(fulltextTaskConfigMapper.mapFulltextSchemaToSchemaDto(config.getSchema()));
        } catch (InvalidSchemaException invalidSchemaException) {
            throw new InvalidSchemaException(String.format("Unable to create config for fulltext task %s because schema is invalid!", config.getName()), invalidSchemaException);
        }
    }

    private void validateName(FulltextTaskConfig config) throws ConfigAlreadyExists {
        if (fulltextTaskConfigRepository.existsByNameAndProjectId(config.getName(), config.getProject().getId())) {
            throw new ConfigAlreadyExists(String.format("Config with name %s already exists in project %s", config.getName(), config.getProject().getProjectName()));
        }
    }

    private void validateQuota(FulltextTaskConfig config) throws QuotaExceededException {
        if (config.getProject().getFulltextQuota() <= fulltextTaskConfigRepository.countByProjectId(config.getProject().getId())) {
            throw new QuotaExceededException("Fulltext quota exceeded for project " + config.getProject().getProjectName());
        }
    }

    @Override
    public List<FulltextTaskConfig> findForProjects(List<Long> projectIds, Authentication authentication) {
        projectIds.forEach(projectId -> adminManager.checkAccessThrowable(authentication, projectId, PermissionTypeEnum.FULL_TEXT_VIEW_TASK));
        return fulltextTaskConfigRepository.findAllByProject_IdIn(projectIds);
    }

    @Override
    public void removeConfig(Long configId, boolean forceDelete, Authentication authentication) throws FulltextConfigNotFoundException, FulltextConfigDeletionForbiddenException, FulltextStatusWontChangedException, FulltextTaskInstanceNotFoundException, SolrServerException, IOException {
        FulltextTaskConfig config = findById(configId, authentication);
        adminManager.checkAccessThrowable(authentication, config.getProject().getId(), PermissionTypeEnum.FULL_TEXT_MANAGE_TASK);
        Optional<FulltextTaskInstance> fulltextTaskInstanceOptionally = fulltextTaskInstanceService.findByConfigIdOptionally(configId);
        if (fulltextTaskInstanceOptionally.isPresent()) {
            FulltextTaskInstance fulltextTaskInstance = fulltextTaskInstanceOptionally.get();
            if (!forceDelete) {
                throw new FulltextConfigDeletionForbiddenException(String.format("Fulltext task config with id %s have instance with id %s with status %s. Delete it or use flag [forceDelete=true]",
                        config.getId(),
                        fulltextTaskInstance.getId(),
                        fulltextTaskInstance.getTaskStatus()));
            } else {
                log.info("Deleting fulltext task instance with id {}", fulltextTaskInstance.getId());
                fulltextTaskInstanceService.removeInstance(fulltextTaskInstance.getId(), authentication);
            }
        }

        fulltextTaskConfigRepository.deleteById(configId);
    }

    @Override
    public void updateConfig(long configId, FulltextTaskConfig fulltextTaskConfig, Authentication authentication) throws FulltextConfigNotFoundException, FulltextConfigUpdateException, QuotaExceededException, InvalidSchemaException, ConfigAlreadyExists, UnavailableTopicException {
        FulltextTaskConfig config = findById(configId, authentication);
        adminManager.checkAccessThrowable(authentication, config.getProject().getId(), PermissionTypeEnum.FULL_TEXT_MANAGE_TASK);
        if (!config.getProject().getId().equals(fulltextTaskConfig.getProject().getId())) {
            throw new FulltextConfigUpdateException("Can't change config project when update");
        }
        if (!config.getName().equals(fulltextTaskConfig.getName())) {
            throw new FulltextConfigUpdateException("Can't change config name when update");
        }
        config.setName(fulltextTaskConfig.getName());
        config.setReplicationFactor(fulltextTaskConfig.getReplicationFactor());
        config.setTopic(fulltextTaskConfig.getTopic());
        config.setSchema(fulltextTaskConfig.getSchema());
        config.setShardsCount(fulltextTaskConfig.getShardsCount());
        Optional<FulltextTaskInstance> fulltextTaskInstance = fulltextTaskInstanceService.findByConfigIdOptionally(configId);
        fulltextTaskInstance.ifPresent(taskInstance -> {
            taskInstance.setNeedUpdate(true);
            try {
                fulltextTaskInstanceService.update(taskInstance);
            } catch (FulltextTaskInstanceNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        saveConfig(config);
    }


    @Override
    public FulltextTaskConfig findById(long id, Authentication authentication) throws FulltextConfigNotFoundException {
        FulltextTaskConfig fulltextTaskConfig = fulltextTaskConfigRepository.findById(id).orElseThrow(() -> new FulltextConfigNotFoundException("Fulltext config with id %s not found!".formatted(id)));
        adminManager.checkAccessThrowable(authentication, fulltextTaskConfig.getProject().getId(), PermissionTypeEnum.FULL_TEXT_VIEW_TASK);
        return fulltextTaskConfig;
    }

}
