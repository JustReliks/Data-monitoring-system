package ru.spbstu.rakitin.archive_service.dto;

import lombok.Builder;
import lombok.Data;
import ru.spbstu.rakitin.commonstarter.utils.MapJson;

@Data
@Builder
public class FileDto {

    private long size;
    private String filename;
    private MapJson fileData;

}
