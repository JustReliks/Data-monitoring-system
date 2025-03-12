package ru.spbstu.rakitin.archive_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileInformationDto {

    private String filename;
    private long size;

}
