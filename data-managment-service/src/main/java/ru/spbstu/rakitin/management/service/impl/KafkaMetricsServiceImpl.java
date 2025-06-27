package ru.spbstu.rakitin.management.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.spbstu.rakitin.management.dto.ClusterMetricDto;
import ru.spbstu.rakitin.management.dto.JmxConnectorProperties;
import ru.spbstu.rakitin.management.dto.KafkaServiceMetric;
import ru.spbstu.rakitin.management.service.JmxConnectorService;
import ru.spbstu.rakitin.management.service.KafkaMetricsService;

import java.util.*;

@Service
public class KafkaMetricsServiceImpl implements KafkaMetricsService {

    @Value("${dms.kafka.cluster.name:default_cluster}")
    private String clusterName;

    private final JmxConnectorService jmxConnectorService;

    public KafkaMetricsServiceImpl(JmxConnectorService jmxConnectorService) {
        this.jmxConnectorService = jmxConnectorService;
    }

    @Override
    public List<ClusterMetricDto> fetchMetricsFromCluster(List<String> addresses, List<KafkaServiceMetric> metrics) {
        Map<KafkaServiceMetric, ClusterMetricDto> metricsMap = new HashMap<>();
        addresses.forEach(address -> {
            String[] addressSplit = address.split(":");
            JmxConnectorProperties properties = new JmxConnectorProperties(addressSplit[0], Integer.parseInt(addressSplit[1]));
            metrics.forEach(kafkaServiceMetric -> {
                Optional<Double> metricValueFromJmx = jmxConnectorService.getMetricValueFromJmx(properties, kafkaServiceMetric.getMetricFullName().getObjectName(), kafkaServiceMetric.getMetricFullName().getAttribute(), Double.class);
                metricValueFromJmx.ifPresent(aDouble -> metricsMap.compute(kafkaServiceMetric, (kafkaServiceMetric1, clusterMetricDto) -> {
                    if (clusterMetricDto == null) {
                        clusterMetricDto = new ClusterMetricDto();
                        clusterMetricDto.setClusterName(clusterName);
                        clusterMetricDto.setMetricName(kafkaServiceMetric.name().toLowerCase());
                    }
                    clusterMetricDto.setValue(clusterMetricDto.getValue() + aDouble);
                    return clusterMetricDto;
                }));
            });
        });
        return new ArrayList<>(metricsMap.values());
    }

}
