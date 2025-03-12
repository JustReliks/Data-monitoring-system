package ru.spbstu.rakitin.archive_service.api;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.spbstu.rakitin.archive_service.exception.ArchiveStatusWontChangedException;
import ru.spbstu.rakitin.archive_service.exception.ArchiveTaskInstanceNotFoundException;
import ru.spbstu.rakitin.archive_service.service.ArchiveTaskInstanceService;
import ru.spbstu.rakitin.commonstarter.admin.aspect.LogController;

@RestController
@RequestMapping("/api/v1/archive/instance")
@RequiredArgsConstructor
public class ArchiveTaskInstanceController {

    private final ArchiveTaskInstanceService archiveTaskInstanceService;

    @PostMapping("/resume/{configId}")
    public long resume(@PathVariable("configId") long configId, Authentication authentication) throws Exception {
        return archiveTaskInstanceService.resume(configId, authentication);
    }

    @PostMapping("/suspend/{configId}")
    @LogController
    public void suspend(@PathVariable("configId") long configId, Authentication authentication) throws ArchiveStatusWontChangedException, ArchiveTaskInstanceNotFoundException {
        archiveTaskInstanceService.suspendTask(configId, authentication);
    }


}
