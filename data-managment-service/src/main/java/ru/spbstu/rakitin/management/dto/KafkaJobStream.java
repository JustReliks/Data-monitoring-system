package ru.spbstu.rakitin.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.streams.KafkaStreams;
import ru.spbstu.rakitin.dto.JobDto;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KafkaJobStream {

    private JobDto<?> job;
    private KafkaStreams kafkaStreams;

}
