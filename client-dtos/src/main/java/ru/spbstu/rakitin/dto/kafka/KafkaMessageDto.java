package ru.spbstu.rakitin.dto.kafka;

import lombok.Data;

@Data
public class KafkaMessageDto {

    private String message;
    private String key;

}
