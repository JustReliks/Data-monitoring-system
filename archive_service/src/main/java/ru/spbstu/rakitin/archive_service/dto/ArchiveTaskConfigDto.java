package ru.spbstu.rakitin.archive_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.spbstu.rakitin.archive_service.model.ArchiveTaskSchema;
import ru.spbstu.rakitin.commonstarter.dto.TaskSchemaDto;
import ru.spbstu.rakitin.commonstarter.dto.archive.ArchiveTaskSchemaDto;

/**
 * DTO for {@link ru.spbstu.rakitin.archive_service.model.ArchiveTaskConfig}
 */
@Data
public class ArchiveTaskConfigDto {

    private long projectId;
    private long topicId;

    @NotNull
    private ArchiveTaskSchemaDto schema;
    @NotNull
    private String name;
    private boolean overwritingEnabled;

}