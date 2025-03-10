package ru.spbstu.rakitin.archive_service.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import ru.spbstu.rakitin.archive_service.model.ArchiveTaskConfig;
import ru.spbstu.rakitin.archive_service.model.ArchiveTaskSchema;
import ru.spbstu.rakitin.commonstarter.admin.AdminManager;
import ru.spbstu.rakitin.commonstarter.datamanagement.DataManagementManager;
import ru.spbstu.rakitin.commonstarter.dto.FieldType;
import ru.spbstu.rakitin.commonstarter.dto.TaskSchemaDto;
import ru.spbstu.rakitin.commonstarter.dto.TimestampFieldDto;
import ru.spbstu.rakitin.commonstarter.dto.archive.ArchiveTaskSchemaDto;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ArchiveTaskConfigMapper {

    private final AdminManager adminManager;
    private final DataManagementManager dataManagementManager;


    public ArchiveTaskConfig mapDtoToArchiveTaskConfig(final ArchiveTaskConfigDto archiveTaskConfigDto, Authentication authentication) {
        return ArchiveTaskConfig.builder()
                .project(adminManager.findProjectById(archiveTaskConfigDto.getProjectId()))
                .topic(dataManagementManager.findTopicById(archiveTaskConfigDto.getTopicId(), authentication))
                .name(archiveTaskConfigDto.getName())
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

    public ArchiveTaskSchemaDto mapArchiveTaskSchemaToArchiveTaskSchemaDto(final ArchiveTaskSchema archiveTaskSchema) {
        return ArchiveTaskSchemaDto.builder()
                .fields(archiveTaskSchema.getSchema())
                .filterExpression(archiveTaskSchema.getFilter())
                .timestampField(archiveTaskSchema.getTimestampField())
                .filenameFieldName(archiveTaskSchema.getFilenameFieldName()).build();
    }


    private static FieldType mapFieldTypeDtoToFieldType(FieldType schemaFieldDto) {
        return schemaFieldDto != null ? FieldType.valueOf(schemaFieldDto.name()) : null;
    }


}
