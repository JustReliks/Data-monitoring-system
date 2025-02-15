package ru.spbstu.rakitin.commonstarter.dto.fulltext;

import lombok.Data;

@Data
public class FulltextTaskConfigDto {

    private String name;
    private long projectId;
    private long topicId;
    private FulltextTaskSchemaDto schema;

    private int replicationFactor;
    private int shardsCount;

}
