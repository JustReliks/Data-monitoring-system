package ru.spbstu.rakitin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LightWeightTopicDto {

    private long id;
    private String name;
    private String nameInKafka;
    private long projectId;
    private int partitions;
    private int replicationFactor;

}
