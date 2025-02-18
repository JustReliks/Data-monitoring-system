package ru.spbstu.rakitin.commonstarter.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JobNameDto {

    private String projectName;
    private String taskName;

}
