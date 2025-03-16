package ru.spbstu.rakitin.management.engine.influxdb;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "mds.monitoring.influx")
public class InfluxDbProperties {

    private String url;
    private String username;
    private String password;

}
