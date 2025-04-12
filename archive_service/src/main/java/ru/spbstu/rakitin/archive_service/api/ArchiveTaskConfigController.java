package ru.spbstu.rakitin.archive_service.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.spbstu.rakitin.archive_service.dto.ArchiveTaskConfigMapper;
import ru.spbstu.rakitin.archive_service.exception.*;
import ru.spbstu.rakitin.archive_service.model.ArchiveTaskConfig;
import ru.spbstu.rakitin.archive_service.service.ArchiveTaskConfigService;
import ru.spbstu.rakitin.archive_service.service.ArchiveTaskInstanceService;
import ru.spbstu.rakitin.commonentites.model.PermissionTypeEnum;
import ru.spbstu.rakitin.commonstarter.admin.aspect.CheckPermission;
import ru.spbstu.rakitin.commonstarter.admin.aspect.LogController;
import ru.spbstu.rakitin.commonstarter.admin.aspect.ProjectIdContainer;
import ru.spbstu.rakitin.dto.archive.ArchiveTaskConfigDto;
import ru.spbstu.rakitin.dto.archive.ArchiveTaskResponse;
import ru.spbstu.rakitin.commonstarter.exception.ConfigAlreadyExists;
import ru.spbstu.rakitin.commonstarter.exception.InvalidSchemaException;
import ru.spbstu.rakitin.commonstarter.exception.QuotaExceededException;
import ru.spbstu.rakitin.commonstarter.exception.UnavailableTopicException;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/archive/config")
public class ArchiveTaskConfigController {

    private final ArchiveTaskConfigService archiveTaskConfigService;
    private final ArchiveTaskConfigMapper archiveTaskConfigMapper;
    private final ArchiveTaskInstanceService archiveTaskInstanceService;

    @LogController
    @PostMapping("/")
    @CheckPermission(permission = PermissionTypeEnum.ARCHIVE_CREATE_TASK, userIdField = "authentication", projectIdField = "archiveTaskConfigDto")
    public long createConfig(Authentication authentication, @RequestBody @ProjectIdContainer(innerFieldName = "projectId") ArchiveTaskConfigDto archiveTaskConfigDto) throws InvalidSchemaException, QuotaExceededException, ConfigAlreadyExists, UnavailableTopicException {
        return archiveTaskConfigService.createArchiveTaskConfig(archiveTaskConfigMapper.mapDtoToArchiveTaskConfig(archiveTaskConfigDto, authentication));
    }

    @LogController
    @DeleteMapping("/{configId}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteConfig(Authentication authentication, @PathVariable long configId, @RequestParam(required = false, defaultValue = "false") boolean forceDelete) throws ArchiveConfigNotFoundException, ArchiveStatusWontChangedException, ArchiveTaskInstanceNotFoundException, IOException, ArchiveConfigDeletionForbiddenException {
        archiveTaskConfigService.deleteArchiveTaskConfig(authentication, configId, forceDelete);
    }

    @GetMapping("/list")
    @LogController
    public List<ArchiveTaskResponse> list(Authentication authentication, @RequestParam(name = "projects") List<Long> projectIds) {
        List<ArchiveTaskConfig> configs = archiveTaskConfigService.findForProjects(projectIds, authentication);

        return configs.stream().map(config -> {
            ArchiveTaskResponse archiveTaskResponse = new ArchiveTaskResponse();
            archiveTaskResponse.setConfig(archiveTaskConfigMapper.mapArchiveTaskConfigToDto(config));
            archiveTaskResponse.setId(config.getId());
            archiveTaskInstanceService.findByConfigIdOptionally(config.getId())
                    .ifPresent(archiveTaskInstance -> archiveTaskResponse.setInstance(archiveTaskConfigMapper.mapArchiveTaskInstanceToTaskInstanceResponse(archiveTaskInstance)));

            return archiveTaskResponse;
        }).toList();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{configId}/update")
    public void updateConfig(Authentication authentication, @PathVariable long configId, @RequestBody ArchiveTaskConfigDto archiveTaskConfigDto) throws ArchiveConfigNotFoundException, ArchiveTaskInstanceNotFoundException, ArchiveConfigUpdateException, InvalidSchemaException {
        ArchiveTaskConfig archiveTaskConfig = archiveTaskConfigMapper.mapDtoToArchiveTaskConfig(archiveTaskConfigDto, authentication);
        archiveTaskConfigService.updateArchiveTaskConfig(configId, archiveTaskConfig, authentication);

    }

}
