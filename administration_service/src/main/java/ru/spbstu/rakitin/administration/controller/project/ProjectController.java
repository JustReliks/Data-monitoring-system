package ru.spbstu.rakitin.administration.controller.project;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.spbstu.rakitin.administration.dto.ProjectDto;
import ru.spbstu.rakitin.administration.dto.mappers.ProjectMapper;
import ru.spbstu.rakitin.administration.model.Project;
import ru.spbstu.rakitin.administration.service.auth.ProjectService;

@RestController
@RequestMapping("/api/v1/admin/project")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectMapper projectMapper;
    private final ProjectService projectService;

    @PostMapping("/")
    public long createProject(@Valid @RequestBody ProjectDto projectDto) {
        Project project = projectMapper.projectDtoToProject(projectDto);
        return projectService.saveProject(project);
    }

}
