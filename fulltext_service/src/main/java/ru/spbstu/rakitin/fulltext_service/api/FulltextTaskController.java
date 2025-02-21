package ru.spbstu.rakitin.fulltext_service.api;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.spbstu.rakitin.commonentites.model.PermissionTypeEnum;
import ru.spbstu.rakitin.commonstarter.admin.aspect.CheckPermission;
import ru.spbstu.rakitin.commonstarter.admin.aspect.LogController;
import ru.spbstu.rakitin.commonstarter.admin.aspect.ProjectIdContainer;
import ru.spbstu.rakitin.commonstarter.dto.fulltext.FulltextTaskConfigDto;
import ru.spbstu.rakitin.commonstarter.exception.InvalidSchemaException;
import ru.spbstu.rakitin.fulltext_service.dto.FulltextTaskConfigMapper;
import ru.spbstu.rakitin.fulltext_service.exception.ConfigAlreadyExists;
import ru.spbstu.rakitin.fulltext_service.exception.FulltextQuotaExceededException;
import ru.spbstu.rakitin.fulltext_service.exception.UnavailableTopicException;
import ru.spbstu.rakitin.fulltext_service.service.FulltextTaskConfigService;

@RestController
@RequestMapping("/api/v1/fulltext/config")
@RequiredArgsConstructor
public class FulltextTaskController {

    private final FulltextTaskConfigService fulltextTaskConfigService;
    private final FulltextTaskConfigMapper fulltextTaskConfigMapper;

    @PostMapping("/create")
    @LogController
    @CheckPermission(permission = PermissionTypeEnum.FULL_TEXT_CREATE_TASK, userIdField = "authentication", projectIdField = "configDto")
    public void create(Authentication authentication, @RequestBody @ProjectIdContainer(innerFieldName = "projectId") FulltextTaskConfigDto configDto) throws ConfigAlreadyExists, FulltextQuotaExceededException, UnavailableTopicException, InvalidSchemaException {
        fulltextTaskConfigService.createConfig(fulltextTaskConfigMapper.mapDtoToFulltextTaskConfig(configDto, authentication), authentication);
    }

}
