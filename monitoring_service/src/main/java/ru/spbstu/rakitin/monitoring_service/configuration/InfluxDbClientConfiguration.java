package ru.spbstu.rakitin.monitoring_service.configuration;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class InfluxDbClientConfiguration {

    private final InfluxDbProperties influxDbProperties;

    @Bean
    public InfluxDBClient influxDbClient() {
        return InfluxDBClientFactory.create(influxDbProperties.getUrl(), influxDbProperties.getAdminToken().toCharArray());
    }

}
