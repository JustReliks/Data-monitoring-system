package ru.spbstu.rakitin.management.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.spbstu.rakitin.management.configuration.KafkaConfiguration;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/kafka")
public class KafkaController {

    private final KafkaConfiguration kafkaConfiguration;

    public KafkaController(KafkaConfiguration kafkaConfiguration) {
        this.kafkaConfiguration = kafkaConfiguration;
    }

    @GetMapping("/properties")
    public Map<String, Object> getKafkaProperties() {
        return kafkaConfiguration.getPublicProperties();
    }

}
