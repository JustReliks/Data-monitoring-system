package ru.spbstu.rakitin.monitoring_service.dto;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import ru.spbstu.rakitin.commonentites.model.Project;
import ru.spbstu.rakitin.commonentites.model.Topic;
import ru.spbstu.rakitin.commonstarter.admin.AdminManager;
import ru.spbstu.rakitin.commonstarter.datamanagement.DataManagementManager;
import ru.spbstu.rakitin.dto.*;
import ru.spbstu.rakitin.dto.monitoring.MonitoringJobDto;
import ru.spbstu.rakitin.dto.monitoring.MonitoringTaskConfigDto;
import ru.spbstu.rakitin.dto.monitoring.MonitoringTaskResponse;
import ru.spbstu.rakitin.monitoring_service.model.*;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MonitoringTaskConfigMapper {

    private final AdminManager adminManager;
    private final DataManagementManager dataManagementManager;

    public MonitoringTaskConfig mapDtoToMonitoringTaskConfig(MonitoringTaskConfigDto dto, Authentication authentication) {
        return MonitoringTaskConfig.builder()
                .name(dto.getName())
                .project(adminManager.findProjectById(dto.getProjectId()))
                .topic(mapTopicDtoToTopic(dataManagementManager.findTopicById(dto.getTopicId(), authentication)))
                .retentionTimeSeconds(dto.getRetentionTimeSeconds())
                .shardGroupDurationSeconds(dto.getShardGroupDurationSeconds())
                .schema(Optional.of(dto.getSchema()).map(this::mapDtoToMonitoringTaskSchema).orElseThrow(() -> new IllegalArgumentException("Schema must be defined"))).build();
    }

    public MonitoringTaskSchema mapDtoToMonitoringTaskSchema(TaskSchemaDto schemaDto) {
        MonitoringTaskSchema schema = new MonitoringTaskSchema();
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

    public MonitoringJobDto mapMonitoringTaskConfigToJobDto(MonitoringTaskInstance instance) {
        MonitoringTaskConfig config = instance.getConfig();
        return MonitoringJobDto.builder()
                .instanceId(instance.getId())
                .taskName(config.getName())
                .projectId(config.getProject().getId())
                .topicId(config.getTopic().getId())
                .organization(config.getProject().getProjectName())
                .needUpdate(instance.isNeedUpdate())
                .schema(mapMonitoringSchemaToSchemaDto(config.getSchema())).build();
    }

    public TaskSchemaDto mapMonitoringSchemaToSchemaDto(MonitoringTaskSchema schema) {
        return TaskSchemaDto.builder()
                .timestampField(mapTimestampFieldToDto(schema))
                .fields(schema.getSchema().stream().map(this::mapSchemaFieldToDto).toList())
                .filterExpression(schema.getFilter()).build();
    }

    private TimestampFieldDto mapTimestampFieldToDto(MonitoringTaskSchema schema) {
        return TimestampFieldDto.builder()
                .fieldName(schema.getTimestampField().getFieldName())
                .useInsertionDate(schema.getTimestampField().isUseInsertionDate()).build();
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

    public MonitoringTaskConfigDto mapMonitoringTaskConfigToDto(MonitoringTaskConfig monitoringTaskConfig) {
        return MonitoringTaskConfigDto.builder()
                .name(monitoringTaskConfig.getName())
                .projectId(monitoringTaskConfig.getProject().getId())
                .topicId(monitoringTaskConfig.getTopic().getId())
                .shardGroupDurationSeconds(monitoringTaskConfig.getShardGroupDurationSeconds())
                .retentionTimeSeconds(monitoringTaskConfig.getRetentionTimeSeconds())
                .schema(mapMonitoringSchemaToSchemaDto(monitoringTaskConfig.getSchema())).build();
    }

    public TaskInstanceResponse mapMonitoringInstanceToTaskInstanceResponse(@NotNull MonitoringTaskInstance monitoringTaskInstance) {
        return TaskInstanceResponse.builder()
                .id(monitoringTaskInstance.getId())
                .needUpdate(monitoringTaskInstance.isNeedUpdate())
                .status(monitoringTaskInstance.getTaskStatus()).build();
    }

    public MonitoringTaskResponse mapMonitoringTaskConfigAndInstanceToResponse(MonitoringTaskConfig monitoringTaskConfig, Optional<MonitoringTaskInstance> monitoringTaskInstanceOptional) {
        MonitoringTaskConfigDto monitoringTaskConfigDto = mapMonitoringTaskConfigToDto(monitoringTaskConfig);
        MonitoringTaskResponse response = new MonitoringTaskResponse();
        response.setId(monitoringTaskConfig.getId());
        response.setConfig(monitoringTaskConfigDto);
        monitoringTaskInstanceOptional.ifPresent(monitoringTaskInstance -> response.setInstance(mapMonitoringInstanceToTaskInstanceResponse(monitoringTaskInstance)));
        return response;

    }
}
