package ru.spbstu.rakitin.dto;

import lombok.Data;

@Data
public class TaskClientDto {

    private TaskType taskType;
    private String taskName;
    private Long taskId;
    private Long projectId;

}
