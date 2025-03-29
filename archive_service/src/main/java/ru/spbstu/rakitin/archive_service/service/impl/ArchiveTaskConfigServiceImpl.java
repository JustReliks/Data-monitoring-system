package ru.spbstu.rakitin.archive_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.spbstu.rakitin.archive_service.dto.ArchiveTaskConfigMapper;
import ru.spbstu.rakitin.archive_service.exception.*;
import ru.spbstu.rakitin.archive_service.model.ArchiveTaskConfig;
import ru.spbstu.rakitin.archive_service.model.ArchiveTaskInstance;
import ru.spbstu.rakitin.archive_service.repository.ArchiveTaskConfigRepository;
import ru.spbstu.rakitin.archive_service.service.ArchiveTaskConfigService;
import ru.spbstu.rakitin.archive_service.service.ArchiveTaskInstanceService;
import ru.spbstu.rakitin.commonentites.model.PermissionTypeEnum;
import ru.spbstu.rakitin.commonstarter.admin.AdminManager;
import ru.spbstu.rakitin.commonstarter.dto.TaskStatus;
import ru.spbstu.rakitin.commonstarter.dto.archive.ArchiveTaskSchemaDto;
import ru.spbstu.rakitin.commonstarter.exception.ConfigAlreadyExists;
import ru.spbstu.rakitin.commonstarter.exception.InvalidSchemaException;
import ru.spbstu.rakitin.commonstarter.exception.QuotaExceededException;
import ru.spbstu.rakitin.commonstarter.exception.UnavailableTopicException;
import ru.spbstu.rakitin.commonstarter.service.SchemaValidationService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArchiveTaskConfigServiceImpl implements ArchiveTaskConfigService {

    private final ArchiveTaskConfigRepository archiveTaskConfigRepository;
    private final SchemaValidationService<ArchiveTaskSchemaDto> schemaValidationService;
    private final ArchiveTaskConfigMapper archiveTaskConfigMapper;
    private final ArchiveTaskInstanceService archiveTaskInstanceService;
    private final AdminManager adminManager;


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

    public ArchiveTaskConfig findById(long id, Authentication authentication) throws ArchiveConfigNotFoundException {
        ArchiveTaskConfig archiveTaskConfig = archiveTaskConfigRepository.findById(id).orElseThrow(() -> new ArchiveConfigNotFoundException(String.format("Archive task config with id %s not found!", id)));
        adminManager.checkAccessThrowable(authentication, archiveTaskConfig.getProject().getId(), PermissionTypeEnum.ARCHIVE_VIEW_TASK);
        return archiveTaskConfig;
    }

    @Override
    public List<ArchiveTaskConfig> findForProjects(List<Long> projectIds, Authentication authentication) {
        projectIds.forEach(projectId -> adminManager.checkAccessThrowable(authentication, projectId, PermissionTypeEnum.ARCHIVE_VIEW_TASK));

        return archiveTaskConfigRepository.findByProject_IdIn(projectIds);
    }

    @Override
    public ArchiveTaskConfig updateArchiveTaskConfig(long configId, ArchiveTaskConfig archiveTaskConfig, Authentication authentication) throws ArchiveConfigNotFoundException, ArchiveTaskInstanceNotFoundException, ArchiveConfigUpdateException, InvalidSchemaException {
        ArchiveTaskConfig config = findById(configId, authentication);
        adminManager.checkAccessThrowable(authentication, config.getProject().getId(), PermissionTypeEnum.ARCHIVE_MANAGE_TASK);
        schemaValidationService.validateSchema(archiveTaskConfigMapper.mapArchiveTaskSchemaToArchiveTaskSchemaDto(archiveTaskConfig.getSchema()));
        if (!archiveTaskConfig.getProject().getId().equals(config.getProject().getId())) {
            throw new ArchiveConfigUpdateException("Can not change projects id");
        }
        if (!archiveTaskConfig.getName().equals(config.getName())) {
            throw new ArchiveConfigUpdateException("Can not change task name");
        }
        config.setSchema(archiveTaskConfig.getSchema());
        config.setTopic(archiveTaskConfig.getTopic());
        config.setOverwritingEnabled(archiveTaskConfig.isOverwritingEnabled());
        Optional<ArchiveTaskInstance> instance = archiveTaskInstanceService.findByConfigIdOptionally(configId);
        if (instance.isPresent()) {
            ArchiveTaskInstance archiveTaskInstance = instance.get();
            if (archiveTaskInstance.getStatus() == TaskStatus.RUNNING) {
                archiveTaskInstance.setNeedUpdate(true);
                archiveTaskInstanceService.saveInstance(archiveTaskInstance);
            }
        }
        archiveTaskConfig.setId(configId);

        return archiveTaskConfigRepository.save(config);
    }

    @Override
    public void deleteArchiveTaskConfig(Authentication authentication, long configId, boolean forceDelete) throws ArchiveConfigNotFoundException, ArchiveConfigDeletionForbiddenException, ArchiveStatusWontChangedException, ArchiveTaskInstanceNotFoundException, IOException {
        ArchiveTaskConfig config = findById(configId, authentication);
        adminManager.checkAccessThrowable(authentication, config.getProject().getId(), PermissionTypeEnum.ARCHIVE_MANAGE_TASK);
        Optional<ArchiveTaskInstance> archiveTaskInstanceOptional = archiveTaskInstanceService.findByConfigIdOptionally(configId);
        if (archiveTaskInstanceOptional.isPresent()) {
            ArchiveTaskInstance archiveTaskInstance = archiveTaskInstanceOptional.get();
            if (!forceDelete) {
                throw new ArchiveConfigDeletionForbiddenException(String.format("Archive task config with id %s have instance with id %s with status %s. Delete it or use flag [forceDelete=true]",
                        config.getId(),
                        archiveTaskInstance.getId(),
                        archiveTaskInstance.getStatus()));
            } else {
                log.info("Deleting fulltext task instance with id {}", archiveTaskInstance.getId());
                archiveTaskInstanceService.removeInstance(archiveTaskInstance.getId(), authentication);
            }
        }

        archiveTaskConfigRepository.deleteById(configId);

    }
}
