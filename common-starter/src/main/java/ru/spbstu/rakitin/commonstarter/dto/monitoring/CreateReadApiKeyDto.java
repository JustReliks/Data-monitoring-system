package ru.spbstu.rakitin.commonstarter.dto.monitoring;

import lombok.Data;

import java.util.List;

@Data
public class CreateReadApiKeyDto {

    private long projectId;
    private List<Long> tasks;
    private String description;

}
