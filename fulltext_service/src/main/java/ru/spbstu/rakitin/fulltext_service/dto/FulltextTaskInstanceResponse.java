package ru.spbstu.rakitin.fulltext_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.spbstu.rakitin.commonstarter.dto.TaskStatus;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FulltextTaskInstanceResponse {

    private long id;
    private TaskStatus status;
    private boolean needUpdate;


}
