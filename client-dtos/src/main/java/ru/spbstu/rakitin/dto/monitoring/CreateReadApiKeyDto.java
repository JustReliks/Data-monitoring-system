package ru.spbstu.rakitin.dto.monitoring;

import lombok.Data;

import java.util.List;

@Data
public class CreateReadApiKeyDto {

    private long projectId;
    private List<Long> tasks;
    private String description;

}
