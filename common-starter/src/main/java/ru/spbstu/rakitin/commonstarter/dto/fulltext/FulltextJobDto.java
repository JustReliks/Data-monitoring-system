package ru.spbstu.rakitin.commonstarter.dto.fulltext;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.spbstu.rakitin.commonstarter.dto.JobDto;

@Getter
@Setter
@NoArgsConstructor
public class FulltextJobDto extends JobDto {

    private String collectionName;
    private FulltextTaskSchemaDto schema;

    @Builder
    public FulltextJobDto(long projectId, long instanceId, long topicId, String fulltextTaskName, String collectionName, FulltextTaskSchemaDto schema) {
        super(projectId, instanceId, topicId, fulltextTaskName);
        this.collectionName = collectionName;
        this.schema = schema;
    }
}
