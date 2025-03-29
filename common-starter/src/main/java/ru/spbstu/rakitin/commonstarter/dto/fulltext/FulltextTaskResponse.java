package ru.spbstu.rakitin.commonstarter.dto.fulltext;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.spbstu.rakitin.commonstarter.dto.TaskInstanceResponse;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FulltextTaskResponse {

    private long id;
    private FulltextTaskConfigDto config;
    private TaskInstanceResponse instance;
}
