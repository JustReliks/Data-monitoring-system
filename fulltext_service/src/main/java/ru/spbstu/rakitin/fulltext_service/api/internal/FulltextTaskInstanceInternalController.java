package ru.spbstu.rakitin.fulltext_service.api.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.spbstu.rakitin.dto.fulltext.FulltextJobDto;
import ru.spbstu.rakitin.fulltext_service.dto.FulltextTaskConfigMapper;
import ru.spbstu.rakitin.fulltext_service.exception.FulltextStatusWontChangedException;
import ru.spbstu.rakitin.fulltext_service.exception.FulltextTaskInstanceNotFoundException;
import ru.spbstu.rakitin.dto.TaskStatus;
import ru.spbstu.rakitin.fulltext_service.service.FulltextTaskInstanceService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/internal/fulltext/instance")
public class FulltextTaskInstanceInternalController {

    private final FulltextTaskInstanceService fulltextTaskInstanceService;
    private final FulltextTaskConfigMapper fulltextTaskConfigMapper;

    @PostMapping("/{taskId}/status/{status}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeStatus(@PathVariable long taskId, @PathVariable TaskStatus status) throws FulltextTaskInstanceNotFoundException, FulltextStatusWontChangedException {
        fulltextTaskInstanceService.forceChangeFulltextInstanceStatus(taskId, status);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<FulltextJobDto>> getAllTaskWithStatus(@PathVariable TaskStatus status) {
        List<FulltextJobDto> fulltextJobDtos = fulltextTaskInstanceService.findAllTaskInstancesWithStatus(status)
                .stream().map(fulltextTaskConfigMapper::mapFulltextTaskConfigToJobDto).toList();
        return ResponseEntity.ok(fulltextJobDtos);
    }

}
