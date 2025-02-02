package ru.spbstu.rakitin.administration.service.auth.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.spbstu.rakitin.administration.exceptions.ProjectNotFoundException;
import ru.spbstu.rakitin.commonentites.model.Project;
import ru.spbstu.rakitin.administration.repository.auth.ProjectRepository;
import ru.spbstu.rakitin.administration.service.auth.ProjectService;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;

    @Override
    public Project findProjectByName(String projectName) throws ProjectNotFoundException {
        return projectRepository.findProjectByProjectName(projectName)
                .orElseThrow(() -> new ProjectNotFoundException(String.format("Project with name %s not found", projectName)));
    }

    public Project findProjectById(long id) throws ProjectNotFoundException {
        return projectRepository.findById(id).orElseThrow(() -> new ProjectNotFoundException(String.format("Project with id %s not found", id)));
    }

    @Override
    public Long saveProject(Project project) {
        return projectRepository.save(project).getId();
    }
}
