package ru.spbstu.rakitin.commonstarter.dto.archive;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArchiveTaskConfigDto {

    private long projectId;
    private long topicId;

    @NotNull
    private ArchiveTaskSchemaDto schema;
    @NotNull
    private String name;
    private boolean overwritingEnabled;

}