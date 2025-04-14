package ru.spbstu.rakitin.archive_service.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import ru.spbstu.rakitin.archive_service.model.ArchiveTaskConfig;
import ru.spbstu.rakitin.archive_service.model.ArchiveTaskInstance;
import ru.spbstu.rakitin.archive_service.model.ArchiveTaskSchema;
import ru.spbstu.rakitin.commonentites.model.Project;
import ru.spbstu.rakitin.commonentites.model.Topic;
import ru.spbstu.rakitin.commonstarter.admin.AdminManager;
import ru.spbstu.rakitin.commonstarter.datamanagement.DataManagementManager;
import ru.spbstu.rakitin.dto.*;
import ru.spbstu.rakitin.dto.archive.ArchiveJobDto;
import ru.spbstu.rakitin.dto.archive.ArchiveTaskConfigDto;
import ru.spbstu.rakitin.dto.archive.ArchiveTaskResponse;
import ru.spbstu.rakitin.dto.archive.ArchiveTaskSchemaDto;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ArchiveTaskConfigMapper {

    private final AdminManager adminManager;
    private final DataManagementManager dataManagementManager;


    public ArchiveTaskConfig mapDtoToArchiveTaskConfig(final ArchiveTaskConfigDto archiveTaskConfigDto, Authentication authentication) {
        return ArchiveTaskConfig.builder()
                .project(adminManager.findProjectById(archiveTaskConfigDto.getProjectId()))
                .topic(mapTopicDtoToTopic(dataManagementManager.findTopicById(archiveTaskConfigDto.getTopicId(), authentication)))
                .name(archiveTaskConfigDto.getName())
                .overwritingEnabled(archiveTaskConfigDto.isOverwritingEnabled())
                .schema(mapDtoToArchiveTaskSchema(archiveTaskConfigDto.getSchema())).build();
    }

    public ArchiveTaskSchema mapDtoToArchiveTaskSchema(ArchiveTaskSchemaDto schemaDto) {
        ArchiveTaskSchema schema = new ArchiveTaskSchema();
        schema.setSchema(schemaDto.getFields());
        schema.setTimestampField(Optional.ofNullable(schemaDto.getTimestampField()).orElse(new TimestampFieldDto()));
        schema.setFilter(schemaDto.getFilterExpression());
        schema.setFilenameFieldName(schemaDto.getFilenameFieldName());
        return schema;
    }

    public Topic mapTopicDtoToTopic(final TopicDto topicDto) {
        return Topic.builder()
                .project(mapProjectDtoToProject(topicDto.getProject()))
                .id(topicDto.getId())
                .uuid(topicDto.getUuid())
                .name(topicDto.getName())
                .nameInKafka(topicDto.getNameInKafka())
                .partitions(topicDto.getPartitions())
                .replicationFactor(topicDto.getReplicationFactor()).build();
    }

    public Project mapProjectDtoToProject(final ProjectDto projectDto) {
        return Project.builder()
                .archiveQuota(projectDto.getArchiveQuota())
                .fulltextQuota(projectDto.getFulltextQuota())
                .id(projectDto.getId())
                .projectName(projectDto.getProjectName())
                .monitoringQuota(projectDto.getMonitoringQuota())
                .topicQuota(projectDto.getTopicQuota())
                .build();
    }

    public ArchiveTaskConfigDto mapArchiveTaskConfigToDto(final ArchiveTaskConfig archiveTaskConfig) {
        return ArchiveTaskConfigDto.builder()
                .name(archiveTaskConfig.getName())
                .overwritingEnabled(archiveTaskConfig.isOverwritingEnabled())
                .projectId(archiveTaskConfig.getProject().getId())
                .topicId(archiveTaskConfig.getTopic().getId())
                .schema(mapArchiveTaskSchemaToArchiveTaskSchemaDto(archiveTaskConfig.getSchema())).build();
    }

    public TaskInstanceResponse mapArchiveTaskInstanceToTaskInstanceResponse(ArchiveTaskInstance instance) {
        return TaskInstanceResponse.builder()
                .id(instance.getId())
                .status(instance.getStatus())
                .needUpdate(instance.isNeedUpdate()).build();
    }


    public ArchiveTaskSchemaDto mapArchiveTaskSchemaToArchiveTaskSchemaDto(final ArchiveTaskSchema archiveTaskSchema) {
        return ArchiveTaskSchemaDto.builder()
                .fields(archiveTaskSchema.getSchema())
                .filterExpression(archiveTaskSchema.getFilter())
                .timestampField(archiveTaskSchema.getTimestampField())
                .filenameFieldName(archiveTaskSchema.getFilenameFieldName()).build();
    }

    public ArchiveJobDto mapArchiveTaskToArchiveJobDto(final ArchiveTaskInstance archiveTaskInstance) {
        return ArchiveJobDto.builder()
                .instanceId(archiveTaskInstance.getId())
                .archiveTaskName(archiveTaskInstance.getConfig().getName())
                .topicId(archiveTaskInstance.getConfig().getTopic().getId())
                .projectId(archiveTaskInstance.getConfig().getProject().getId())
                .accessOverwriting(archiveTaskInstance.getConfig().isOverwritingEnabled())
                .jobFolderName(getJobFolderName(archiveTaskInstance))
                .needUpdate(archiveTaskInstance.isNeedUpdate())
                .schema(mapArchiveTaskSchemaToArchiveTaskSchemaDto(archiveTaskInstance.getConfig().getSchema())).build();
    }

    public static String getJobFolderName(ArchiveTaskInstance archiveTaskInstance) {
        return archiveTaskInstance.getConfig().getProject().getProjectName() + "/" + archiveTaskInstance.getConfig().getName();
    }


    private static FieldType mapFieldTypeDtoToFieldType(FieldType schemaFieldDto) {
        return schemaFieldDto != null ? FieldType.valueOf(schemaFieldDto.name()) : null;
    }


    public ArchiveTaskResponse mapArchiveTaskConfigAndInstanceToResponse(ArchiveTaskConfig config, Optional<ArchiveTaskInstance> instance) {
        ArchiveTaskResponse archiveTaskResponse = new ArchiveTaskResponse();
        archiveTaskResponse.setConfig(mapArchiveTaskConfigToDto(config));
        archiveTaskResponse.setId(config.getId());

        instance.
                ifPresent(archiveTaskInstance -> archiveTaskResponse.setInstance(mapArchiveTaskInstanceToTaskInstanceResponse(archiveTaskInstance)));

        return archiveTaskResponse;
    }
}
