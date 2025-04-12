package ru.spbstu.rakitin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskInstanceResponse {

    private long id;
    private TaskStatus status;
    private boolean needUpdate;


}
