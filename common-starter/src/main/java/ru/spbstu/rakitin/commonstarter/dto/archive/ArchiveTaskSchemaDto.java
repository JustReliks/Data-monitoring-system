package ru.spbstu.rakitin.commonstarter.dto.archive;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.spbstu.rakitin.commonstarter.dto.FilterExpression;
import ru.spbstu.rakitin.commonstarter.dto.SchemaFieldDto;
import ru.spbstu.rakitin.commonstarter.dto.TaskSchemaDto;
import ru.spbstu.rakitin.commonstarter.dto.TimestampFieldDto;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class ArchiveTaskSchemaDto extends TaskSchemaDto {

    @NotNull
    private String filenameFieldName;

    public ArchiveTaskSchemaDto(List<SchemaFieldDto> fields, TimestampFieldDto timestampField, FilterExpression filterExpression, String filenameFieldName) {
        super(fields, timestampField, filterExpression);
        this.filenameFieldName = filenameFieldName;
    }

}
