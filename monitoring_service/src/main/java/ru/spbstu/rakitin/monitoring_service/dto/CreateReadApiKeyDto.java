package ru.spbstu.rakitin.monitoring_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateReadApiKeyDto {

    private long projectId;
    private List<Long> tasks;
    private String description;

}
