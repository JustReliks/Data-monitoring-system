package ru.spbstu.rakitin.user_api_service.api.archive;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.spbstu.rakitin.commonstarter.admin.aspect.LogController;
import ru.spbstu.rakitin.commonstarter.admin.aspect.ProjectIdContainer;
import ru.spbstu.rakitin.commonstarter.archive.ArchiveServiceManager;
import ru.spbstu.rakitin.dto.archive.ArchiveTaskConfigDto;
import ru.spbstu.rakitin.dto.archive.ArchiveTaskResponse;

import java.util.List;

@RestController
@RequestMapping("/api/v1/archive/config")
@Tag(name = "4. Операции с конфигурациями архивных задач")
@RequiredArgsConstructor
public class ArchiveConfigController {

    private final ArchiveServiceManager archiveServiceManager;

    @PostMapping("/create")
    @LogController
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(description = "Создание конфигурации задачи полнотекстовой индексации")
    public long create(Authentication authentication, @RequestBody @ProjectIdContainer(innerFieldName = "projectId") ArchiveTaskConfigDto configDto) {
        return archiveServiceManager.createConfig(configDto, authentication);
    }

    @GetMapping("/list")
    @LogController
    @Operation(description = "Запрос списка задач архвиной индексации")
    public List<ArchiveTaskResponse> list(Authentication authentication, @RequestParam List<Long> projects) {
        return archiveServiceManager.list(projects, authentication);

    }

    @DeleteMapping("/{configId}/delete")
    @LogController
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(description = "Удаление конфигурации задачи архивной индексации")
    public void delete(Authentication authentication, @PathVariable Long configId,
                       @RequestParam(required = false, defaultValue = "false") boolean forceDelete) {
        archiveServiceManager.removeConfig(configId, forceDelete, authentication);
    }

    @PutMapping("/{configId}/update")
    @LogController
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(description = "Обновление конфигурации задачи архивной индексации")
    public void updateConfig(Authentication authentication, @PathVariable long configId, @RequestBody ArchiveTaskConfigDto configDto) {
        archiveServiceManager.updateConfig(configId, configDto, authentication);
    }

}
