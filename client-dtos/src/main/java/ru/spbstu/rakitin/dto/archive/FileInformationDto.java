package ru.spbstu.rakitin.dto.archive;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileInformationDto {

    private String filename;
    private long size;

}
