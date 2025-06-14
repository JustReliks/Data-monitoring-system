package ru.spbstu.rakitin.management.engine.solr.configuration;

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
    private String username;
    private String password;

    @Data
    public static class ZookeeperProperties {
        private List<String> zkHosts;
        private Optional<String> zkChroot = Optional.empty();
        private int zkClientTimeoutSec = 10 * 60 * 6 * 365;
        private int zkConnectTimeoutSec = 10 * 60 * 6 * 365;
    }

}
