package ru.spbstu.rakitin.commonstarter.dto.fulltext;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FulltextJobDto {


    private long instanceId;
    private long topicId;
    private long projectId;
    private String fulltextTaskName;
    private String collectionName;
    private FulltextTaskSchemaDto schema;

}
