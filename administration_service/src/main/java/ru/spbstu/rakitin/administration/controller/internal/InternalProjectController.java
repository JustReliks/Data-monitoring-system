package ru.spbstu.rakitin.administration.controller.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.spbstu.rakitin.administration.exceptions.ProjectNotFoundException;
import ru.spbstu.rakitin.administration.service.auth.ProjectService;
import ru.spbstu.rakitin.commonentites.model.Project;

@RestController
@RequestMapping("/api/v1/admin/internal/project")
@RequiredArgsConstructor
public class InternalProjectController {

    private final ProjectService projectService;

    @GetMapping("/{id}")
    public Project findProjectById(@PathVariable long id) throws ProjectNotFoundException {
        return projectService.findProjectById(id);

    }
}
