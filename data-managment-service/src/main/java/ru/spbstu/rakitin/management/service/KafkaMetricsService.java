package ru.spbstu.rakitin.management.service;

import ru.spbstu.rakitin.management.dto.ClusterMetricDto;
import ru.spbstu.rakitin.management.dto.KafkaServiceMetric;

import java.util.List;

public interface KafkaMetricsService {
    List<ClusterMetricDto> fetchMetricsFromCluster(List<String> addresses, List<KafkaServiceMetric> metrics);
}
