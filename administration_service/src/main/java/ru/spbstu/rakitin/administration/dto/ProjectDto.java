package ru.spbstu.rakitin.administration.dto;

import lombok.Data;

@Data
public class ProjectDto {

    private String projectName;
    private int fulltextQuota;
    private int archiveQuota;
    private int monitoringQuota;

}
