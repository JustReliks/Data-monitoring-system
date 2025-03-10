package ru.spbstu.rakitin.fulltext_service.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import ru.spbstu.rakitin.commonstarter.admin.AdminManager;
import ru.spbstu.rakitin.commonstarter.datamanagement.DataManagementManager;
import ru.spbstu.rakitin.commonstarter.dto.FieldType;
import ru.spbstu.rakitin.commonstarter.dto.SchemaFieldDto;
import ru.spbstu.rakitin.commonstarter.dto.TaskSchemaDto;
import ru.spbstu.rakitin.commonstarter.dto.TimestampFieldDto;
import ru.spbstu.rakitin.commonstarter.dto.fulltext.FulltextJobDto;
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
                .topic(dataManagementManager.findTopicById(dto.getTopicId(), authentication))
                .replicationFactor(dto.getReplicationFactor())
                .shardsCount(dto.getShardsCount())
                .schema(Optional.of(dto.getSchema()).map(this::mapDtoToFulltextTaskSchema).orElseThrow(() -> new IllegalArgumentException("Schema must be defined"))).build();
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
                .collectionName(SolrUtils.buildWriteCollectionName(config.getProject().getProjectName(), config.getName()))
                .schema(mapFulltextSchemaToSchemaDto(config.getSchema())).build();
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
