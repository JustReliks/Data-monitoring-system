package ru.spbstu.rakitin.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.spbstu.rakitin.commonstarter.dto.JobDto;
import ru.spbstu.rakitin.commonstarter.dto.TaskType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto {

    private TaskType taskType;
    private JobDto<?> jobDto;

}
