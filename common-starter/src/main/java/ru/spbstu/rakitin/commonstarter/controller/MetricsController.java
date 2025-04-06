package ru.spbstu.rakitin.commonstarter.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/metrics")
@RequiredArgsConstructor
public class MetricsController {

    private final MetricsEndpoint metricsEndpoint;

    @GetMapping("/cpu")
    public double getCpuUsage() {
        MetricsEndpoint.MetricDescriptor metric = metricsEndpoint.metric("process.cpu.usage", null);
        return metric.getMeasurements().stream().filter(sample -> sample.getStatistic().name().equals("VALUE")).findFirst().get().getValue();
    }

}
