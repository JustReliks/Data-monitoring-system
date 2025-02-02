package ru.spbstu.rakitin.commonstarter.discovery.impl;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import ru.spbstu.rakitin.commonstarter.discovery.DiscoveryService;
import ru.spbstu.rakitin.commonstarter.discovery.ServiceName;

@Service
@RequiredArgsConstructor
public class PropertyDiscoveryService implements DiscoveryService {

    private static final String PROPERTY_PATTERN = "dms.service.discovery.%s";

    private final Environment environment;

    @Override
    public String findServiceHost(ServiceName serviceName) {
        String property = environment.getProperty(String.format(PROPERTY_PATTERN, serviceName.name()));
        if (StringUtils.isEmpty(property)) {
            throw new RuntimeException("Service with name " + serviceName + " is not found in your properties");
        }
        return property;
    }
}
