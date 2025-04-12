package ru.spbstu.rakitin.dto.archive;

import lombok.Builder;
import lombok.Data;
import ru.spbstu.rakitin.dto.MapJson;

@Data
@Builder
public class FileDto {

    private long size;
    private String filename;
    private MapJson fileData;

}
