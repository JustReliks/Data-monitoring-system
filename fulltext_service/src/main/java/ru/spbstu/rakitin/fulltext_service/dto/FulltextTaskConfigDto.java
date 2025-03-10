package ru.spbstu.rakitin.fulltext_service.dto;

import lombok.Data;
import ru.spbstu.rakitin.commonstarter.dto.TaskSchemaDto;

@Data
public class FulltextTaskConfigDto {

    private String name;
    private long projectId;
    private long topicId;
    private TaskSchemaDto schema;

    private int replicationFactor;
    private int shardsCount;

}
