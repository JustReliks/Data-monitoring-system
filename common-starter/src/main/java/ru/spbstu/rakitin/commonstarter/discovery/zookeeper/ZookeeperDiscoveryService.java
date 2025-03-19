package ru.spbstu.rakitin.commonstarter.discovery.zookeeper;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import ru.spbstu.rakitin.commonstarter.configuration.InnerRequestConfiguration;
import ru.spbstu.rakitin.commonstarter.discovery.AbstractDiscoveryService;
import ru.spbstu.rakitin.commonstarter.discovery.ServiceName;
import ru.spbstu.rakitin.commonstarter.discovery.ServicePeakStrategy;

import java.util.List;

public class ZookeeperDiscoveryService extends AbstractDiscoveryService {

    private final ZooKeeper zooKeeper;
    private final String basePath;

    public ZookeeperDiscoveryService(ServicePeakStrategy servicePeakStrategy, InnerRequestConfiguration innerRequestConfiguration, ZooKeeper zooKeeper, String basePath) {
        super(servicePeakStrategy, innerRequestConfiguration);
        this.zooKeeper = zooKeeper;
        this.basePath = basePath;
    }

    @SneakyThrows
    @Override
    protected List<String> getServers(ServiceName serviceName) {
        List<String> children = zooKeeper.getChildren(basePath + "/" + serviceName.name(), false);
        return children.stream().map(child -> {
            try {
                return new String(zooKeeper.getData(basePath + "/" + serviceName.name() + "/" + child, false, null));
            } catch (KeeperException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }
}
