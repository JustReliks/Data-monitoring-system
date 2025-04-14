package ru.spbstu.rakitin.fulltext_service.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import ru.spbstu.rakitin.commonentites.model.Project;
import ru.spbstu.rakitin.commonentites.model.Topic;
import ru.spbstu.rakitin.commonstarter.admin.AdminManager;
import ru.spbstu.rakitin.commonstarter.datamanagement.DataManagementManager;
import ru.spbstu.rakitin.dto.*;
import ru.spbstu.rakitin.dto.fulltext.FulltextJobDto;
import ru.spbstu.rakitin.dto.fulltext.FulltextTaskConfigDto;
import ru.spbstu.rakitin.dto.fulltext.FulltextTaskResponse;
import ru.spbstu.rakitin.fulltext_service.engine.utils.SolrUtils;
import ru.spbstu.rakitin.fulltext_service.model.*;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FulltextTaskConfigMapper {

    private final AdminManager adminManager;
    private final DataManagementManager dataManagementManager;

    public FulltextTaskConfig mapDtoToFulltextTaskConfig(FulltextTaskConfigDto dto, Authentication authentication) {
        return FulltextTaskConfig.builder()
                .name(dto.getName())
                .project(adminManager.findProjectById(dto.getProjectId()))
                .topic(mapTopicDtoToTopic(dataManagementManager.findTopicById(dto.getTopicId(), authentication)))
                .replicationFactor(dto.getReplicationFactor())
                .shardsCount(dto.getShardsCount())
                .schema(Optional.of(dto.getSchema()).map(this::mapDtoToFulltextTaskSchema).orElseThrow(() -> new IllegalArgumentException("Schema must be defined"))).build();
    }

    public FulltextTaskResponse mapFulltextTaskConfigAndInstanceToResponse(FulltextTaskConfig fulltextTaskConfig, Optional<FulltextTaskInstance> fulltextTaskInstance) {
        FulltextTaskResponse response = new FulltextTaskResponse();
        FulltextTaskConfigDto fulltextTaskConfigDto = mapFulltextTaskConfigToDto(fulltextTaskConfig);
        response.setId(fulltextTaskConfig.getId());
        response.setConfig(fulltextTaskConfigDto);
        fulltextTaskInstance.ifPresent(fulltextTaskInstanceResponse -> response.setInstance(mapFulltextTaskInstanceToTaskInstanceResponse(fulltextTaskInstanceResponse)));
        return response;
    }


    public FulltextTaskConfigDto mapFulltextTaskConfigToDto(FulltextTaskConfig fulltextTaskConfig) {
        return FulltextTaskConfigDto.builder()
                .name(fulltextTaskConfig.getName())
                .projectId(fulltextTaskConfig.getProject().getId())
                .topicId(fulltextTaskConfig.getTopic().getId())
                .replicationFactor(fulltextTaskConfig.getReplicationFactor())
                .shardsCount(fulltextTaskConfig.getReplicationFactor())
                .schema(
                        mapFulltextSchemaToSchemaDto(fulltextTaskConfig.getSchema())
                ).build();
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


    public FulltextTaskSchema mapDtoToFulltextTaskSchema(TaskSchemaDto schemaDto) {
        FulltextTaskSchema schema = new FulltextTaskSchema();
        schema.setSchema(schemaDto.getFields().stream().map(schemaFieldDto -> SchemaField.builder().fieldName(schemaFieldDto.getFieldName()).fieldType(mapFieldTypeDtoToFieldType(schemaFieldDto.getFieldType())).subType(mapFieldTypeDtoToFieldType(schemaFieldDto.getSubType())).build()).toList());
        schema.setTimestampField(
                Optional.ofNullable(schemaDto.getTimestampField())
                        .map(timestampFieldDto ->
                                TimestampField.builder()
                                        .fieldName(timestampFieldDto.getFieldName())
                                        .useInsertionDate(timestampFieldDto.isUseInsertionDate())
                                        .build())
                        .orElse(new TimestampField()));
        schema.setFilter(schemaDto.getFilterExpression());
        return schema;
    }

    public FulltextJobDto mapFulltextTaskConfigToJobDto(FulltextTaskInstance instance) {
        FulltextTaskConfig config = instance.getConfig();
        return FulltextJobDto.builder()
                .instanceId(instance.getId())
                .fulltextTaskName(config.getName())
                .projectId(config.getProject().getId())
                .topicId(config.getTopic().getId())
                .needUpdate(instance.isNeedUpdate())
                .collectionName(SolrUtils.buildWriteCollectionName(config.getProject().getProjectName(), config.getName()))
                .schema(mapFulltextSchemaToSchemaDto(config.getSchema())).build();
    }

    public TaskInstanceResponse mapFulltextTaskInstanceToTaskInstanceResponse(FulltextTaskInstance instance) {
        return TaskInstanceResponse.builder()
                .id(instance.getId())
                .status(instance.getTaskStatus())
                .needUpdate(instance.isNeedUpdate()).build();
    }

    public TaskSchemaDto mapFulltextSchemaToSchemaDto(FulltextTaskSchema schema) {
        return TaskSchemaDto.builder()
                .timestampField(mapTimestampFieldToDto(schema))
                .fields(schema.getSchema().stream().map(this::mapSchemaFieldToDto).toList())
                .filterExpression(schema.getFilter()).build();
    }

    private TimestampFieldDto mapTimestampFieldToDto(FulltextTaskSchema schema) {
        return TimestampFieldDto.builder()
                .fieldName(schema.getTimestampField().getFieldName())
                .useInsertionDate(schema.getTimestampField().isUseInsertionDate()).build();
    }

    private SchemaFieldDto mapSchemaFieldToDto(SchemaField schemaField) {
        SchemaFieldDto schemaFieldDto = new SchemaFieldDto();
        schemaFieldDto.setFieldName(schemaField.getFieldName());
        schemaFieldDto.setFieldType(FieldType.valueOf(schemaField.getFieldType().name()));
        if (schemaField.getSubType() != null) {
            schemaFieldDto.setSubType(FieldType.valueOf(schemaField.getSubType().name()));
        }
        return schemaFieldDto;
    }

    private static FieldType mapFieldTypeDtoToFieldType(FieldType schemaFieldDto) {
        return schemaFieldDto != null ? FieldType.valueOf(schemaFieldDto.name()) : null;
    }

}
