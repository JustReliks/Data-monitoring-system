package ru.spbstu.rakitin.administration.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectDto {

    private Long id;
    private String projectName;
    private int fulltextQuota;
    private int archiveQuota;
    private int monitoringQuota;
    private int topicQuota;

}
