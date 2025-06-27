package ru.spbstu.rakitin.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClusterMetricDto {

    private double value;
    private String metricName;
    private String clusterName;

}
