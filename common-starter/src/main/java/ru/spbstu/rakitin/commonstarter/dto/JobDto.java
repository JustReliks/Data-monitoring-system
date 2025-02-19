package ru.spbstu.rakitin.commonstarter.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JobDto {

    private long projectId;
    private long instanceId;
    private long topicId;
    private String taskName;
    private TaskSchemaDto schema;

}
