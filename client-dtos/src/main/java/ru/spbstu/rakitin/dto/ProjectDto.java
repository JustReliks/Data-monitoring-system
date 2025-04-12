package ru.spbstu.rakitin.dto;

import lombok.Data;

@Data
public class ProjectDto {

    private Long id;

    private String projectName;
    private int fulltextQuota;
    private int archiveQuota;
    private int monitoringQuota;
    private int topicQuota;


}
