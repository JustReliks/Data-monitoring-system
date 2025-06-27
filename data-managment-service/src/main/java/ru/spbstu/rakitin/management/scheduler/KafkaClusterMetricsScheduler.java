package ru.spbstu.rakitin.management.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.spbstu.rakitin.management.configuration.KafkaConfiguration;
import ru.spbstu.rakitin.management.dto.ClusterMetricDto;
import ru.spbstu.rakitin.management.dto.KafkaServiceMetric;
import ru.spbstu.rakitin.management.service.KafkaMetricsService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@ConditionalOnProperty(value = "dms.kafka.metrics.enabled", havingValue = "true", matchIfMissing = true)
@Component
public class KafkaClusterMetricsScheduler {

    @Value("${dms.kafka.metrics.jmx-addresses:}")
    private List<String> jmxAddresses;

    @Value("${dms.kafka.metrics.topic:mds.mds-metrics}")
    private String metricsTopic;

    private final KafkaMetricsService kafkaMetricsService;

    private final KafkaConfiguration kafkaConfiguration;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public KafkaClusterMetricsScheduler(KafkaMetricsService kafkaMetricsService, KafkaConfiguration kafkaConfiguration) {
        this.kafkaMetricsService = kafkaMetricsService;
        this.kafkaConfiguration = kafkaConfiguration;
    }


    @Scheduled(fixedRateString = "${dms.kafka.metrics.rate-ms:5000}")
    public void fetchMetricsTask() {
        if (jmxAddresses.isEmpty()) {
            log.warn("Kafka jmx addresses not specified");
        }
        log.info("Fetching metrics");
        Map<String, Object> kafkaProperties = kafkaConfiguration.kafkaProperties();
        List<ClusterMetricDto> clusterMetricDtos = kafkaMetricsService.fetchMetricsFromCluster(jmxAddresses, Arrays.stream(KafkaServiceMetric.values()).toList());
        double meanCpu = clusterMetricDtos.stream().filter(clusterMetricDto -> clusterMetricDto.getMetricName().equalsIgnoreCase(KafkaServiceMetric.KAFKA_CLUSTER_CURRENT_CPU_LOAD_MEAN.name()))
                .mapToDouble(ClusterMetricDto::getValue).average().orElse(-1);
        ClusterMetricDto metricCpu = clusterMetricDtos.stream().filter(clusterMetricDto -> clusterMetricDto.getMetricName().equalsIgnoreCase(KafkaServiceMetric.KAFKA_CLUSTER_CURRENT_CPU_LOAD_MEAN.name())).findAny().orElse(null);
        if (metricCpu != null) {
            clusterMetricDtos.removeIf(clusterMetricDto -> clusterMetricDto.getMetricName().equalsIgnoreCase(KafkaServiceMetric.KAFKA_CLUSTER_CURRENT_CPU_LOAD_MEAN.name()));
            clusterMetricDtos.add(new ClusterMetricDto(meanCpu, metricCpu.getMetricName(), metricCpu.getClusterName()));
        }
        try (KafkaProducer<String, String> producer = new KafkaProducer<>(kafkaProperties)) {
            List<String> metrics = clusterMetricDtos.stream().map(clusterMetricDto -> {
                try {
                    return objectMapper.writeValueAsString(clusterMetricDto);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }).toList();
            metrics.forEach(s -> producer.send(new ProducerRecord<>(metricsTopic, s)));
        }
        log.info("Fetched metrics");
    }

}
