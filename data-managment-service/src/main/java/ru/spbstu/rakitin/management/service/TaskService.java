package ru.spbstu.rakitin.management.service;

import org.springframework.security.core.Authentication;
import ru.spbstu.rakitin.commonstarter.dto.JobDto;
import ru.spbstu.rakitin.commonstarter.dto.TaskType;
import ru.spbstu.rakitin.management.dto.TaskDto;

import java.util.List;

public interface TaskService {

    List<TaskDto> list(Authentication authentication, List<Long> projectIds, List<TaskType> taskTypes);

}
