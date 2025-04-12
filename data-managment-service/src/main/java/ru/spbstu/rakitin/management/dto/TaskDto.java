package ru.spbstu.rakitin.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.spbstu.rakitin.dto.JobDto;
import ru.spbstu.rakitin.dto.TaskType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto {

    private TaskType taskType;
    private JobDto<?> jobDto;

}
