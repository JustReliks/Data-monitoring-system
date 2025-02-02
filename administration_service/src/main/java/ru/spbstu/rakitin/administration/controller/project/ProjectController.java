package ru.spbstu.rakitin.administration.controller.project;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.spbstu.rakitin.administration.dto.ProjectDto;
import ru.spbstu.rakitin.administration.dto.mappers.ProjectMapper;
import ru.spbstu.rakitin.administration.exceptions.ProjectNotFoundException;
import ru.spbstu.rakitin.administration.service.auth.ProjectService;
import ru.spbstu.rakitin.commonentites.model.Project;

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

    @GetMapping("/{id}")
    public ProjectDto findProjectById(@PathVariable long id) throws ProjectNotFoundException {
        Project project = projectService.findProjectById(id);

        return projectMapper.projectToProjectDto(project);

    }

}
