package ru.spbstu.rakitin.management.api;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.spbstu.rakitin.dto.JobDto;
import ru.spbstu.rakitin.dto.TaskType;
import ru.spbstu.rakitin.management.dto.TaskDto;
import ru.spbstu.rakitin.management.service.TaskService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/task")
@RequiredArgsConstructor
public class TasksController {

    private final TaskService taskService;

    @GetMapping("/list")
    public List<TaskDto> listTasks(
            @RequestParam List<Long> projectIds,
            @RequestParam List<TaskType> types, Authentication authentication) {

        return taskService.list(authentication, projectIds, types);


    }

}
