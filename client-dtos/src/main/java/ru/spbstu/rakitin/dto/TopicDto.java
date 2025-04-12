package ru.spbstu.rakitin.dto;

import lombok.Data;

@Data
public class TopicDto {

    private Long id;

    private String name;

    private ProjectDto project;

    private int partitions;

    private int replicationFactor;

    private String uuid;

    private String nameInKafka;


}
