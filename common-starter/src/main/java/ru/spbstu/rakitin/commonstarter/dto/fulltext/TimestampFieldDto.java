package ru.spbstu.rakitin.commonstarter.dto.fulltext;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TimestampFieldDto {

    private String fieldName;
    private boolean useInsertionDate;

}
