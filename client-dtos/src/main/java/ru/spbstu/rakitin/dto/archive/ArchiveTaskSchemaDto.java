package ru.spbstu.rakitin.dto.archive;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.spbstu.rakitin.dto.FilterExpression;
import ru.spbstu.rakitin.dto.SchemaFieldDto;
import ru.spbstu.rakitin.dto.TaskSchemaDto;
import ru.spbstu.rakitin.dto.TimestampFieldDto;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class ArchiveTaskSchemaDto extends TaskSchemaDto {

    @NotNull
    private String filenameFieldName;
    private String directoryFieldName;

    public ArchiveTaskSchemaDto(List<SchemaFieldDto> fields, TimestampFieldDto timestampField, FilterExpression filterExpression, String filenameFieldName, String directoryFieldName) {
        super(fields, timestampField, filterExpression);
        this.filenameFieldName = filenameFieldName;
        this.directoryFieldName = directoryFieldName;
    }

}
