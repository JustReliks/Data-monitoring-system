package ru.spbstu.rakitin.fulltext_service.api;

import lombok.RequiredArgsConstructor;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.spbstu.rakitin.dto.PermissionTypeEnum;
import ru.spbstu.rakitin.commonstarter.admin.aspect.CheckPermission;
import ru.spbstu.rakitin.commonstarter.admin.aspect.LogController;
import ru.spbstu.rakitin.commonstarter.admin.aspect.ProjectIdContainer;
import ru.spbstu.rakitin.commonstarter.exception.ConfigAlreadyExists;
import ru.spbstu.rakitin.commonstarter.exception.InvalidSchemaException;
import ru.spbstu.rakitin.commonstarter.exception.QuotaExceededException;
import ru.spbstu.rakitin.commonstarter.exception.UnavailableTopicException;
import ru.spbstu.rakitin.dto.fulltext.FulltextTaskConfigDto;
import ru.spbstu.rakitin.dto.fulltext.FulltextTaskResponse;
import ru.spbstu.rakitin.fulltext_service.dto.FulltextTaskConfigMapper;
import ru.spbstu.rakitin.fulltext_service.exception.*;
import ru.spbstu.rakitin.fulltext_service.model.FulltextTaskConfig;
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

    @GetMapping("/{taskId}")
    public FulltextTaskResponse findById(@PathVariable long taskId, Authentication authentication) throws FulltextConfigNotFoundException {
        return fulltextTaskConfigMapper.mapFulltextTaskConfigAndInstanceToResponse(fulltextTaskConfigService.findById(taskId, authentication), fulltextTaskInstanceService.findByConfigIdOptionally(taskId));
    }

    @GetMapping("/name/{taskName}")
    public FulltextTaskResponse findByName(@PathVariable String taskName, @RequestParam("projectId") long projectId, Authentication authentication) throws FulltextConfigNotFoundException {
        FulltextTaskConfig config = fulltextTaskConfigService.findByName(projectId, taskName, authentication);
        return fulltextTaskConfigMapper.mapFulltextTaskConfigAndInstanceToResponse(config, fulltextTaskInstanceService.findByConfigIdOptionally(config.getId()));
    }


    @GetMapping("/list")
    @LogController
    public List<FulltextTaskResponse> list(Authentication authentication, @RequestParam List<Long> projects) {

        return fulltextTaskConfigService.findForProjects(projects, authentication)
                .stream().map(fulltextTaskConfig -> {
                    Optional<FulltextTaskInstance> fulltextTaskInstance = fulltextTaskInstanceService.findByConfigIdOptionally(fulltextTaskConfig.getId());
                    return fulltextTaskConfigMapper.mapFulltextTaskConfigAndInstanceToResponse(fulltextTaskConfig, fulltextTaskInstance);
                }).toList();
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
