package ru.spbstu.rakitin.commonstarter.dto.archive;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.spbstu.rakitin.commonstarter.dto.JobDto;

@Getter
@Setter
@NoArgsConstructor
public class ArchiveJobDto extends JobDto<ArchiveTaskSchemaDto> {

    private String jobFolderName;
    private boolean accessOverwriting;


    @Builder
    public ArchiveJobDto(long projectId, long instanceId, long topicId, String archiveTaskName, String jobFolderName, boolean accessOverwriting, boolean needUpdate, ArchiveTaskSchemaDto schema) {
        super(projectId, instanceId, topicId, archiveTaskName, needUpdate, schema);
        this.jobFolderName = jobFolderName;
        this.accessOverwriting = accessOverwriting;
    }

}
