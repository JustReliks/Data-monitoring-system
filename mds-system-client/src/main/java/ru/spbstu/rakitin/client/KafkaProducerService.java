package ru.spbstu.rakitin.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

@RequiredArgsConstructor
public class KafkaProducerService {

    private final Properties producerProperties;
    private static final ObjectMapper objectMapper = new ObjectMapper();


    public <T> void sendDataToTopic(String topic, T data) {

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(producerProperties)) {
            try {
                producer.send(new ProducerRecord<>(topic, objectMapper.writeValueAsString(data)));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(String.format("Unable to process data object [%s] to json.", data), e);
            }

        }
    }
}
