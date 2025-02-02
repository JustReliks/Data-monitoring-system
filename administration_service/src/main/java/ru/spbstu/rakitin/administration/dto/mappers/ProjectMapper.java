package ru.spbstu.rakitin.administration.dto.mappers;

import org.springframework.stereotype.Component;
import ru.spbstu.rakitin.administration.dto.ProjectDto;
import ru.spbstu.rakitin.commonentites.model.Project;

@Component
public class ProjectMapper {

    public Project projectDtoToProject(ProjectDto projectDto) {
        return Project.builder()
                .id(projectDto.getId())
                .projectName(projectDto.getProjectName())
                .archiveQuota(projectDto.getArchiveQuota())
                .fulltextQuota(projectDto.getFulltextQuota())
                .monitoringQuota(projectDto.getMonitoringQuota())
                .topicQuota(projectDto.getTopicQuota()).build();
    }

    public ProjectDto projectToProjectDto(Project project) {
        return ProjectDto.builder()
                .id(project.getId())
                .projectName(project.getProjectName())
                .archiveQuota(project.getArchiveQuota())
                .fulltextQuota(project.getFulltextQuota())
                .monitoringQuota(project.getMonitoringQuota())
                .topicQuota(project.getTopicQuota()).build();
    }

}
