package ru.spbstu.rakitin.commonstarter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimestampFieldDto {

    private String fieldName;
    private boolean useInsertionDate;

}
