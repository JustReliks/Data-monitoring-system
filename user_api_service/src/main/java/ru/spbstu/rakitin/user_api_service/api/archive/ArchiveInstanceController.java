package ru.spbstu.rakitin.user_api_service.api.archive;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.spbstu.rakitin.commonstarter.admin.aspect.LogController;
import ru.spbstu.rakitin.commonstarter.archive.ArchiveServiceManager;

@RestController
@RequestMapping("/api/v1/archive/instance")
@RequiredArgsConstructor
@Tag(name = "2. Операции с экземплярами архивных задач")
public class ArchiveInstanceController {

    private final ArchiveServiceManager archiveServiceManager;

    @PostMapping("/resume/{configId}")
    @LogController
    @Operation(description = "Запуск архивных задачи по id конфигурации")
    public long resume(@PathVariable("configId") long configId, Authentication authentication) {
        return archiveServiceManager.resume(configId, authentication);
    }

    @PostMapping("/suspend/{configId}")
    @LogController
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(description = "Остановка архивных задачи по id конфигурации")
    public void suspend(@PathVariable("configId") long configId, Authentication authentication) {
        archiveServiceManager.suspendTask(configId, authentication);
    }

    @PutMapping("/update/{configId}")
    @LogController
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(description = "Ротация архивных задачи по id конфигурации")
    public void update(@PathVariable("configId") long configId, Authentication authentication) {
        archiveServiceManager.update(configId, authentication);
    }


}
