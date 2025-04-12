package ru.spbstu.rakitin.commonstarter.discovery;

import ru.spbstu.rakitin.commonstarter.exception.ServiceNotFoundException;
import ru.spbstu.rakitin.dto.ServiceName;

import java.util.List;

public interface DiscoveryService {

    String findServiceHost(ServiceName serviceName, List<String> deadServers) throws ServiceNotFoundException;

}
