package ru.spbstu.rakitin.management.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.spbstu.rakitin.dto.PermissionTypeEnum;
import ru.spbstu.rakitin.commonstarter.admin.AdminManager;
import ru.spbstu.rakitin.dto.TaskType;
import ru.spbstu.rakitin.management.dto.TaskDto;
import ru.spbstu.rakitin.management.service.JobService;
import ru.spbstu.rakitin.management.service.TaskService;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final Map<TaskType, JobService<?>> jobServiceMap;
    private final AdminManager adminManager;

    @Override
    public List<TaskDto> list(Authentication authentication, List<Long> projectIds, List<TaskType> taskTypes) {

        projectIds.forEach(projectId -> {
            if (taskTypes.contains(TaskType.ARCHIVE)) {
                adminManager.checkAccessThrowable(authentication, projectId, PermissionTypeEnum.ARCHIVE_VIEW_TASK);
            }
            if (taskTypes.contains(TaskType.MONITORING)) {
                adminManager.checkAccessThrowable(authentication, projectId, PermissionTypeEnum.MONITORING_VIEW_TASK);
            }
            if (taskTypes.contains(TaskType.FULLTEXT)) {
                adminManager.checkAccessThrowable(authentication, projectId, PermissionTypeEnum.FULL_TEXT_VIEW_TASK);
            }
        });

        return getTasksListForProjectsAndTaskTypes(projectIds, taskTypes);
    }

    @Override
    public List<TaskDto> getTasksListForProjectsAndTaskTypes(List<Long> projectIds, List<TaskType> taskTypes) {
        return jobServiceMap.values().stream().filter(jobService -> taskTypes.contains(jobService.getTaskType()))
                .flatMap(jobService -> jobService.getJobs().stream()
                        .filter(jobDto -> projectIds.contains(jobDto.getProjectId()))
                        .map(jobDto ->
                                TaskDto
                                        .builder()
                                        .jobDto(jobDto)
                                        .taskType(jobService.getTaskType()).build()))
                .toList();
    }
}
