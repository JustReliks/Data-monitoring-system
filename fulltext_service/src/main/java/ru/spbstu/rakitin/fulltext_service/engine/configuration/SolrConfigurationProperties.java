package ru.spbstu.rakitin.fulltext_service.engine.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Optional;

@Configuration
@ConfigurationProperties("dms.fulltext.solr")
@Data
public class SolrConfigurationProperties {

    private ZookeeperProperties zookeeperProperties;

    @Data
    public static class ZookeeperProperties {
        private List<String> zkHosts;
        private Optional<String> zkChroot = Optional.empty();
        private int zkClientTimeoutSec = 10;
        private int zkConnectTimeoutSec = 10;
    }

}
