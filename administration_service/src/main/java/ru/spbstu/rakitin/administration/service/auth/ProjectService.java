package ru.spbstu.rakitin.administration.service.auth;

import ru.spbstu.rakitin.administration.exceptions.ProjectNotFoundException;
import ru.spbstu.rakitin.commonentites.model.Project;

public interface ProjectService {

    Project findProjectByName(String projectName) throws ProjectNotFoundException;
    Project findProjectById(long id) throws ProjectNotFoundException;

    Long saveProject(Project project);

}
