package ru.spbstu.rakitin.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserTaskAccessDto {

    private UserProjectAccessDto projectAccess;
    private List<TaskInformator> tasks;
    private List<LightWeightTopicDto> topics;

}
