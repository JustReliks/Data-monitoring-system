package ru.spbstu.rakitin.dto.fulltext;

import lombok.Data;
import ru.spbstu.rakitin.dto.MapJson;

import java.util.List;

@Data
public class SolrQueryResponseDto {

    private int qTime;
    private int responseSize;
    private List<MapJson> response;

}
