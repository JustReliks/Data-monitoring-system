package ru.spbstu.rakitin.monitoring_service.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "mds.monitoring.influx")
public class InfluxDbProperties {

    private String url;
    private String adminToken;

}
