package ru.spbstu.rakitin.commonstarter.discovery.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import ru.spbstu.rakitin.commonstarter.controller.MetricsDirectClient;
import ru.spbstu.rakitin.commonstarter.discovery.ServicePeakStrategy;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@ConditionalOnProperty(value = "mds.discovery.strategy", havingValue = "cpu_min")
public class MinCpuServicePeakStrategy implements ServicePeakStrategy {

    private final MetricsDirectClient metricsDirectClient;

    public MinCpuServicePeakStrategy(MetricsDirectClient metricsDirectClient) {
        this.metricsDirectClient = metricsDirectClient;
    }

    @Override
    public String peakServer(List<String> servers) {
        String selectedServer = null;
        Double selectedCpu = null;
        for (String server : servers) {
            Optional<Double> cpuUsage = metricsDirectClient.getCpuUsage(server);
            if (cpuUsage.isPresent()) {
                Double cpu = cpuUsage.get();
                if (selectedServer == null ||
                        selectedCpu > cpu) {
                    selectedServer = server;
                    selectedCpu = cpu;
                }
            }
        }
        log.info("Selected server: {} cpu: {}", selectedServer, selectedCpu);
        return selectedServer;
    }
}
