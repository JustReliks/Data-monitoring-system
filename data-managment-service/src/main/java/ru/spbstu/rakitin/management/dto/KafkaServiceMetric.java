package ru.spbstu.rakitin.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum KafkaServiceMetric {

    KAFKA_CLUSTER_BYTES_IN_PER_SECOND(new JmxMetricFullName("kafka.server:type=BrokerTopicMetrics,name=BytesInPerSec", "MeanRate")),
    KAFKA_CLUSTER_BYTES_OUT_PER_SECOND(new JmxMetricFullName("kafka.server:type=BrokerTopicMetrics,name=BytesInPerSec", "MeanRate")),
    KAFKA_CLUSTER_CURRENT_CPU_LOAD_MEAN(new JmxMetricFullName("java.lang:type=OperatingSystem", "ProcessCpuLoad"));

    private final JmxMetricFullName metricFullName;

}
