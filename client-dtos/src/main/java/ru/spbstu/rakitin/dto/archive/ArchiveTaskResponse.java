package ru.spbstu.rakitin.dto.archive;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.spbstu.rakitin.dto.TaskInstanceResponse;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArchiveTaskResponse {

    private long id;
    private ArchiveTaskConfigDto config;
    private TaskInstanceResponse instance;

}
