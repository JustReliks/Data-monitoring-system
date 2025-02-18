package ru.spbstu.rakitin.fulltext_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimestampField {

    private String fieldName = "timestamp";
    private boolean useInsertionDate = true;

}
