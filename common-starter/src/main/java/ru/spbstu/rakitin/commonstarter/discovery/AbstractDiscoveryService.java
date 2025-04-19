package ru.spbstu.rakitin.commonstarter.discovery;


import lombok.RequiredArgsConstructor;
import ru.spbstu.rakitin.commonstarter.configuration.InnerRequestConfiguration;
import ru.spbstu.rakitin.commonstarter.exception.ServiceNotFoundException;
import ru.spbstu.rakitin.dto.ServiceName;

import java.util.List;

@RequiredArgsConstructor
public abstract class AbstractDiscoveryService implements DiscoveryService {

    private final ServicePeakStrategy servicePeakStrategy;
    private final InnerRequestConfiguration innerRequestConfiguration;


    @Override
    public String findServiceHost(ServiceName serviceName, List<String> deadServers) throws ServiceNotFoundException {
        List<String> servers = getServers(serviceName).stream().map(s -> getInnerProtocol(serviceName) + "://" + s).filter(server -> !deadServers.contains(server))
                .toList();
        if (servers.isEmpty()) {
            throw new ServiceNotFoundException("No Live servers found for " + serviceName);
        }

        return getServicePeakStrategy(serviceName).peakServer(servers);

    }

    @Override
    public boolean isServiceAvailable(ServiceName serviceName) {
        List<String> servers = getServers(serviceName);
        return !servers.isEmpty();
    }

    protected ServicePeakStrategy getServicePeakStrategy(ServiceName serviceName) {
        return servicePeakStrategy;
    }

    protected String getInnerProtocol(ServiceName serviceName) {
        return innerRequestConfiguration.getProtocol().name().toLowerCase();
    }

    protected abstract List<String> getServers(ServiceName serviceName);
}
