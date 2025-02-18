package ru.spbstu.rakitin.management.dto;

import lombok.Data;

@Data
public class TopicDto {

    private long id;
    private String name;
    private long projectId;
    private int partitions;
    private int replicationFactor;

}
