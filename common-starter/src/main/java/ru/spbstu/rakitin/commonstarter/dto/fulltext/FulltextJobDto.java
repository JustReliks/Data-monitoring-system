package ru.spbstu.rakitin.commonstarter.dto.fulltext;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.spbstu.rakitin.commonstarter.dto.JobDto;
import ru.spbstu.rakitin.commonstarter.dto.TaskSchemaDto;

@Getter
@Setter
@NoArgsConstructor
public class FulltextJobDto extends JobDto<TaskSchemaDto> {

    private String collectionName;

    @Builder
    public FulltextJobDto(long projectId, long instanceId, long topicId, String fulltextTaskName, String collectionName, TaskSchemaDto schema, boolean needUpdate) {
        super(projectId, instanceId, topicId, fulltextTaskName, needUpdate, schema);
        this.collectionName = collectionName;
    }
}
