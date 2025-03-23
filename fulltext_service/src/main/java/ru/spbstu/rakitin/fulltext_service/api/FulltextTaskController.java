package ru.spbstu.rakitin.fulltext_service.api;

import lombok.RequiredArgsConstructor;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.spbstu.rakitin.commonentites.model.PermissionTypeEnum;
import ru.spbstu.rakitin.commonstarter.admin.aspect.CheckPermission;
import ru.spbstu.rakitin.commonstarter.admin.aspect.LogController;
import ru.spbstu.rakitin.commonstarter.admin.aspect.ProjectIdContainer;
import ru.spbstu.rakitin.commonstarter.exception.ConfigAlreadyExists;
import ru.spbstu.rakitin.commonstarter.exception.InvalidSchemaException;
import ru.spbstu.rakitin.commonstarter.exception.QuotaExceededException;
import ru.spbstu.rakitin.commonstarter.exception.UnavailableTopicException;
import ru.spbstu.rakitin.fulltext_service.dto.FulltextTaskConfigDto;
import ru.spbstu.rakitin.fulltext_service.dto.FulltextTaskConfigMapper;
import ru.spbstu.rakitin.fulltext_service.dto.FulltextTaskResponse;
import ru.spbstu.rakitin.fulltext_service.exception.*;
import ru.spbstu.rakitin.fulltext_service.model.FulltextTaskInstance;
import ru.spbstu.rakitin.fulltext_service.service.FulltextTaskConfigService;
import ru.spbstu.rakitin.fulltext_service.service.FulltextTaskInstanceService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/fulltext/config")
@RequiredArgsConstructor
public class FulltextTaskController {

    private final FulltextTaskConfigService fulltextTaskConfigService;
    private final FulltextTaskInstanceService fulltextTaskInstanceService;
    private final FulltextTaskConfigMapper fulltextTaskConfigMapper;

    @PostMapping("/create")
    @LogController
    @ResponseStatus(HttpStatus.CREATED)
    @CheckPermission(permission = PermissionTypeEnum.FULL_TEXT_CREATE_TASK, userIdField = "authentication", projectIdField = "configDto")
    public long create(Authentication authentication, @RequestBody @ProjectIdContainer(innerFieldName = "projectId") FulltextTaskConfigDto configDto) throws ConfigAlreadyExists, QuotaExceededException, UnavailableTopicException, InvalidSchemaException {
        return fulltextTaskConfigService.createConfig(fulltextTaskConfigMapper.mapDtoToFulltextTaskConfig(configDto, authentication), authentication);
    }

    @GetMapping("/list")
    @LogController
    public ResponseEntity<List<FulltextTaskResponse>> list(Authentication authentication, @RequestParam List<Long> projects) {
        List<FulltextTaskResponse> list = fulltextTaskConfigService.findForProjects(projects, authentication)
                .stream().map(fulltextTaskConfig -> {
                    FulltextTaskResponse response = new FulltextTaskResponse();
                    FulltextTaskConfigDto fulltextTaskConfigDto = fulltextTaskConfigMapper.mapFulltextTaskConfigToDto(fulltextTaskConfig);
                    response.setId(fulltextTaskConfig.getId());
                    response.setConfig(fulltextTaskConfigDto);
                    Optional<FulltextTaskInstance> fulltextTaskInstance = fulltextTaskInstanceService.findByConfigIdOptionally(fulltextTaskConfig.getId());
                    fulltextTaskInstance.ifPresent(fulltextTaskInstanceResponse -> response.setInstance(fulltextTaskConfigMapper.mapFulltextTaskInstanceToFulltextTaskInstanceResponse(fulltextTaskInstanceResponse)));
                    return response;
                }).toList();

        if (list.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/{configId}/delete")
    @LogController
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(Authentication authentication, @PathVariable Long configId,
                       @RequestParam(required = false, defaultValue = "false") boolean forceDelete) throws FulltextConfigNotFoundException, FulltextConfigDeletionForbiddenException, FulltextStatusWontChangedException, FulltextTaskInstanceNotFoundException, SolrServerException, IOException {
        fulltextTaskConfigService.removeConfig(configId, forceDelete, authentication);
    }

    @PutMapping("/{configId}/update")
    @LogController
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateConfig(Authentication authentication, @PathVariable long configId, @RequestBody FulltextTaskConfigDto configDto) throws QuotaExceededException, FulltextConfigNotFoundException, InvalidSchemaException, ConfigAlreadyExists, FulltextConfigUpdateException, UnavailableTopicException {
        fulltextTaskConfigService.updateConfig(configId, fulltextTaskConfigMapper.mapDtoToFulltextTaskConfig(configDto, authentication), authentication);
    }

}
