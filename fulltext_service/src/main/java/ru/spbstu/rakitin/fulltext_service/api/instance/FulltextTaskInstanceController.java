package ru.spbstu.rakitin.fulltext_service.api.instance;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.spbstu.rakitin.commonstarter.admin.aspect.LogController;
import ru.spbstu.rakitin.fulltext_service.exception.*;
import ru.spbstu.rakitin.fulltext_service.service.FulltextTaskInstanceService;

@RestController
@RequestMapping("/api/v1/fulltext/instance")
@RequiredArgsConstructor
public class FulltextTaskInstanceController {

    private final FulltextTaskInstanceService fulltextTaskInstanceService;

    @PostMapping("/resume/{configId}")
    @LogController
    public void resume(@PathVariable("configId") long configId, Authentication authentication) throws FulltextConfigNotFoundException, FulltextTaskInstanceAlreadyRunningException, IllegalAccessException, InstanceInitiationFailedException {
        fulltextTaskInstanceService.resume(configId, authentication);
    }

    @PostMapping("/suspend/{configId}")
    @LogController
    public void suspend(@PathVariable("configId") long configId, Authentication authentication) throws FulltextTaskInstanceNotFoundException, FulltextStatusWontChangedException {
        fulltextTaskInstanceService.suspendTask(configId, authentication);
    }

}
