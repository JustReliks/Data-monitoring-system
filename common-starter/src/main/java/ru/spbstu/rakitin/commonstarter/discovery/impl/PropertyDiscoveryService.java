package ru.spbstu.rakitin.commonstarter.discovery.impl;

import io.micrometer.common.util.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import ru.spbstu.rakitin.commonstarter.configuration.InnerRequestConfiguration;
import ru.spbstu.rakitin.commonstarter.discovery.AbstractDiscoveryService;
import ru.spbstu.rakitin.dto.ServiceName;
import ru.spbstu.rakitin.commonstarter.discovery.ServicePeakStrategy;

import java.util.Arrays;
import java.util.List;

@Service
@ConditionalOnProperty(value = "mds.discovery.type", havingValue = "STATIC", matchIfMissing = true)
public class PropertyDiscoveryService extends AbstractDiscoveryService {

    private static final String PROPERTY_PATTERN = "dms.service.discovery.%s";

    private final Environment environment;

    public PropertyDiscoveryService(ServicePeakStrategy servicePeakStrategy, InnerRequestConfiguration innerRequestConfiguration, Environment environment) {
        super(servicePeakStrategy, innerRequestConfiguration);
        this.environment = environment;
    }

    @Override
    protected List<String> getServers(ServiceName serviceName) {
        String property = environment.getProperty(String.format(PROPERTY_PATTERN, serviceName.name()));
        if (StringUtils.isEmpty(property)) {
            return List.of();
        }
        return Arrays.stream(property.split(",")).toList();
    }
}
