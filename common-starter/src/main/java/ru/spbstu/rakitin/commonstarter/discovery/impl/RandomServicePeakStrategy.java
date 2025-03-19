package ru.spbstu.rakitin.commonstarter.discovery.impl;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import ru.spbstu.rakitin.commonstarter.discovery.ServicePeakStrategy;

import java.util.List;

@Component
@ConditionalOnProperty(value = "mds.discovery.strategy", havingValue = "random", matchIfMissing = true)
public class RandomServicePeakStrategy implements ServicePeakStrategy {
    @Override
    public String peakServer(List<String> servers) {
        return servers.get((int) (Math.random() * servers.size()));
    }
}
