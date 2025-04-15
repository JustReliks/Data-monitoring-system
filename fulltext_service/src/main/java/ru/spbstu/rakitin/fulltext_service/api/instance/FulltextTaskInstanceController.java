package ru.spbstu.rakitin.fulltext_service.api.instance;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.spbstu.rakitin.commonstarter.admin.aspect.LogController;
import ru.spbstu.rakitin.commonstarter.exception.InstanceInitiationFailedException;
import ru.spbstu.rakitin.fulltext_service.exception.*;
import ru.spbstu.rakitin.fulltext_service.service.FulltextTaskInstanceService;

@RestController
@RequestMapping("/api/v1/fulltext/instance")
@RequiredArgsConstructor
public class FulltextTaskInstanceController {

    private final FulltextTaskInstanceService fulltextTaskInstanceService;

    @PostMapping("/resume/{configId}")
    @LogController
    public Long resume(@PathVariable("configId") long configId, Authentication authentication) throws FulltextConfigNotFoundException, FulltextTaskInstanceAlreadyRunningException, IllegalAccessException, InstanceInitiationFailedException, FulltextTaskInstanceResumeException {
        return fulltextTaskInstanceService.resume(configId, authentication);
    }

    @PostMapping("/suspend/{configId}")
    @LogController
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("configId") long configId, Authentication authentication) throws FulltextTaskInstanceNotFoundException, FulltextStatusWontChangedException {
        fulltextTaskInstanceService.suspendTask(configId, authentication);
    }

    @PutMapping("/update/{configId}")
    @LogController
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable("configId") long configId, Authentication authentication) throws Exception {
        fulltextTaskInstanceService.update(configId, authentication);
    }

}
