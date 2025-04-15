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

    private Long id;
    private String name;
    private String nameInKafka;
    private Long projectId;
    private Integer partitions;
    private Integer replicationFactor;

}
