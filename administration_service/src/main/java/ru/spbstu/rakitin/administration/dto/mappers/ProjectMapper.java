package ru.spbstu.rakitin.administration.dto.mappers;

import org.springframework.stereotype.Component;
import ru.spbstu.rakitin.administration.dto.ProjectDto;
import ru.spbstu.rakitin.administration.model.Project;

@Component
public class ProjectMapper {

    public Project projectDtoToProject(ProjectDto projectDto) {
        return Project.builder()
                .projectName(projectDto.getProjectName())
                .archiveQuota(projectDto.getArchiveQuota())
                .fulltextQuota(projectDto.getFulltextQuota())
                .monitoringQuota(projectDto.getMonitoringQuota()).build();
    }

}
