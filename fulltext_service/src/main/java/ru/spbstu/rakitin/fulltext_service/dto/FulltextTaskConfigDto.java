package ru.spbstu.rakitin.fulltext_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.spbstu.rakitin.commonstarter.dto.TaskSchemaDto;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FulltextTaskConfigDto {

    private String name;
    private long projectId;
    private long topicId;
    private TaskSchemaDto schema;
    private int replicationFactor;
    private int shardsCount;

}
