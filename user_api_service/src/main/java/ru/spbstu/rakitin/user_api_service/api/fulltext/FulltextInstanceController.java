package ru.spbstu.rakitin.user_api_service.api.fulltext;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.spbstu.rakitin.commonstarter.admin.aspect.LogController;
import ru.spbstu.rakitin.commonstarter.fulltext.FulltextServiceManager;

@RestController
@RequestMapping("/api/v1/fulltext/instance")
@RequiredArgsConstructor
@Tag(name = "2. Операции с экземплярами полнотекстовых задач")
public class FulltextInstanceController {

    private final FulltextServiceManager fulltextServiceManager;

    @PostMapping("/resume/{configId}")
    @LogController
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(description = "Запуск полнотекстовой задачи по id конфигурации")
    public long resume(@PathVariable("configId") long configId, Authentication authentication) {
        return fulltextServiceManager.resume(configId, authentication);
    }

    @PostMapping("/suspend/{configId}")
    @LogController
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(description = "Остановка полнотекстовой задачи по id конфигурации")
    public void suspend(@PathVariable("configId") long configId, Authentication authentication) {
        fulltextServiceManager.suspendTask(configId, authentication);
    }

    @PutMapping("/update/{configId}")
    @LogController
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(description = "Ротация полнотекстовой задачи по id конфигурации")
    public void update(@PathVariable("configId") long configId, Authentication authentication) {
        fulltextServiceManager.update(configId, authentication);
    }


}
