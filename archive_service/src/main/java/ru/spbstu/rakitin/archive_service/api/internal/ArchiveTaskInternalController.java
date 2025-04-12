package ru.spbstu.rakitin.archive_service.api.internal;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.spbstu.rakitin.archive_service.dto.ArchiveTaskConfigMapper;
import ru.spbstu.rakitin.archive_service.exception.ArchiveStatusWontChangedException;
import ru.spbstu.rakitin.archive_service.exception.ArchiveTaskInstanceNotFoundException;
import ru.spbstu.rakitin.archive_service.service.ArchiveTaskInstanceService;
import ru.spbstu.rakitin.dto.TaskStatus;
import ru.spbstu.rakitin.dto.archive.ArchiveJobDto;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/internal/archive/instance")
public class ArchiveTaskInternalController {

    private final ArchiveTaskInstanceService archiveTaskInstanceService;
    private final ArchiveTaskConfigMapper archiveTaskConfigMapper;

    @PostMapping("/{taskId}/status/{status}")
    public void changeStatus(@PathVariable long taskId, @PathVariable TaskStatus status) throws ArchiveStatusWontChangedException, ArchiveTaskInstanceNotFoundException {
        archiveTaskInstanceService.forceChangeArchiveInstanceStatus(taskId, status);
    }

    @GetMapping("/status/{status}")
    public List<ArchiveJobDto> getAllTaskWithStatus(@PathVariable TaskStatus status) {
        return archiveTaskInstanceService.findAllTaskInstancesWithStatus(status)
                .stream().map(archiveTaskConfigMapper::mapArchiveTaskToArchiveJobDto).toList();
    }

}
