package ru.spbstu.rakitin.dto.fulltext;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.spbstu.rakitin.dto.TaskInformator;
import ru.spbstu.rakitin.dto.TaskInstanceResponse;
import ru.spbstu.rakitin.dto.TaskType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FulltextTaskResponse implements TaskInformator {

    private long id;
    private FulltextTaskConfigDto config;
    private TaskInstanceResponse instance;
//    private final String type = TaskType.FULLTEXT.name();

    @Override
    public long getTopicId() {
        return config.getTopicId();
    }

    @Override
    public long getTaskId() {
        return id;
    }

    @Override
    public long getProjectId() {
        return config.getProjectId();
    }

}
