package ru.spbstu.rakitin.archive_service.api;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.spbstu.rakitin.archive_service.dto.ArchiveTaskConfigDto;
import ru.spbstu.rakitin.archive_service.dto.ArchiveTaskConfigMapper;
import ru.spbstu.rakitin.archive_service.service.ArchiveTaskConfigService;
import ru.spbstu.rakitin.commonentites.model.PermissionTypeEnum;
import ru.spbstu.rakitin.commonstarter.admin.aspect.CheckPermission;
import ru.spbstu.rakitin.commonstarter.admin.aspect.LogController;
import ru.spbstu.rakitin.commonstarter.admin.aspect.ProjectIdContainer;
import ru.spbstu.rakitin.commonstarter.exception.ConfigAlreadyExists;
import ru.spbstu.rakitin.commonstarter.exception.InvalidSchemaException;
import ru.spbstu.rakitin.commonstarter.exception.QuotaExceededException;
import ru.spbstu.rakitin.commonstarter.exception.UnavailableTopicException;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/archive/config")
public class ArchiveTaskConfigController {

    private final ArchiveTaskConfigService archiveTaskConfigService;
    private final ArchiveTaskConfigMapper archiveTaskConfigMapper;

    @LogController
    @PostMapping("/")
    @CheckPermission(permission = PermissionTypeEnum.ARCHIVE_CREATE_TASK, userIdField = "authentication", projectIdField = "archiveTaskConfigDto")
    public long createConfig(Authentication authentication, @RequestBody @ProjectIdContainer(innerFieldName = "projectId") ArchiveTaskConfigDto archiveTaskConfigDto) throws InvalidSchemaException, QuotaExceededException, ConfigAlreadyExists, UnavailableTopicException {
        return archiveTaskConfigService.createArchiveTaskConfig(archiveTaskConfigMapper.mapDtoToArchiveTaskConfig(archiveTaskConfigDto, authentication));
    }

}
