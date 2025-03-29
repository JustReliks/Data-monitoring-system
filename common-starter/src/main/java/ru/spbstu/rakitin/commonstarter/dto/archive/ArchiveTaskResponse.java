package ru.spbstu.rakitin.commonstarter.dto.archive;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.spbstu.rakitin.commonstarter.dto.TaskInstanceResponse;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArchiveTaskResponse {

    private long id;
    private ArchiveTaskConfigDto config;
    private TaskInstanceResponse instance;

}
