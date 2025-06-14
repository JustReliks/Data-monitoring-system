package ru.spbstu.rakitin.user_api_service.api.fulltext;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.spbstu.rakitin.commonstarter.admin.aspect.LogController;
import ru.spbstu.rakitin.commonstarter.admin.aspect.ProjectIdContainer;
import ru.spbstu.rakitin.dto.fulltext.FulltextTaskConfigDto;
import ru.spbstu.rakitin.dto.fulltext.FulltextTaskResponse;
import ru.spbstu.rakitin.commonstarter.fulltext.FulltextServiceManager;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fulltext/config")
@Tag(name = "4. Операции с конфигурациями полнотекстовых задач")
@RequiredArgsConstructor
public class FulltextConfigController {

    private final FulltextServiceManager fulltextServiceManager;

    @PostMapping("/create")
    @LogController
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(description = "Создание конфигурации задачи полнотекстовой индексации")
    public long create(Authentication authentication, @RequestBody @ProjectIdContainer(innerFieldName = "projectId") FulltextTaskConfigDto configDto) {
        return fulltextServiceManager.createConfig(configDto, authentication);
    }

    @GetMapping("/list")
    @LogController
    @Operation(description = "Запрос списка задач полнотекстовой индексации")
    public List<FulltextTaskResponse> list(Authentication authentication, @RequestParam List<Long> projects) {
        return fulltextServiceManager.list(projects, authentication);

    }

    @DeleteMapping("/{configId}/delete")
    @LogController
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(description = "Удаление конфигурации задачи полнотекстовой индексации")
    public void delete(Authentication authentication, @PathVariable Long configId,
                       @RequestParam(required = false, defaultValue = "false") boolean forceDelete) {
        fulltextServiceManager.removeConfig(configId, forceDelete, authentication);
    }

    @PutMapping("/{configId}/update")
    @LogController
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(description = "Обновление конфигурации задачи полнотекстовой индексации")
    public void updateConfig(Authentication authentication, @PathVariable long configId, @RequestBody FulltextTaskConfigDto configDto) {
        fulltextServiceManager.updateConfig(configId, configDto, authentication);
    }

    @GetMapping("/{taskId}")
    @Operation(description = "Получение полнотекстовой задачи по индентификатору")
    public FulltextTaskResponse findById(@PathVariable long taskId, Authentication authentication) {
        return fulltextServiceManager.findById(taskId, authentication);
    }

    @GetMapping("/name/{taskName}")
    @Operation(description = "Получение полнотекстовой задачи по имени")
    public FulltextTaskResponse findByName(@PathVariable String taskName, @RequestParam("projectId") Long projectId, Authentication authentication)  {
        return fulltextServiceManager.findByName(taskName, projectId, authentication);
    }


}
