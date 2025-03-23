package ru.spbstu.rakitin.fulltext_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FulltextTaskResponse {

    private long id;
    private FulltextTaskConfigDto config;
    private FulltextTaskInstanceResponse instance;
}
