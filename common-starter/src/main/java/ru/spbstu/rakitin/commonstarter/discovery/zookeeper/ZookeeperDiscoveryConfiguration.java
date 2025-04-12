package ru.spbstu.rakitin.commonstarter.discovery.zookeeper;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.spbstu.rakitin.commonstarter.configuration.InnerRequestConfiguration;
import ru.spbstu.rakitin.commonstarter.discovery.DiscoveryService;
import ru.spbstu.rakitin.dto.ServiceName;
import ru.spbstu.rakitin.commonstarter.discovery.ServicePeakStrategy;

import java.io.IOException;
import java.net.NetworkInterface;

@Data
@ConfigurationProperties(prefix = "mds.discovery.zookeeper")
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(value = "mds.discovery.type", havingValue = "ZOOKEEPER")
public class ZookeeperDiscoveryConfiguration {

    private String zookeeperHost;
    private String basePath;
    private int sessionTimeoutMs = 5000;
    private ServiceName serviceName;


    @Bean
    public DiscoveryService zookeeperDiscoveryService(ZooKeeper zooKeeper, ServicePeakStrategy servicePeakStrategy, InnerRequestConfiguration innerRequestConfiguration) {
        return new ZookeeperDiscoveryService(servicePeakStrategy, innerRequestConfiguration, zooKeeper, basePath);
    }

    @Bean
    public ZooKeeper zookeeperClient() throws IOException {
        return new ZooKeeper(zookeeperHost, sessionTimeoutMs,
                watchedEvent -> {
                });
    }

    @Bean
    public ZookeeperDiscoveryRegistrar zookeeperDiscoveryRegistrar(ZooKeeper zooKeeper, @Value("${server.port}") int port) throws IOException {
        String ipAddress = NetworkInterface.getNetworkInterfaces().nextElement().getInetAddresses().nextElement().getHostAddress();
        return new ZookeeperDiscoveryRegistrar(serviceName, zooKeeper, basePath, ipAddress + ":" + port);
    }

}
