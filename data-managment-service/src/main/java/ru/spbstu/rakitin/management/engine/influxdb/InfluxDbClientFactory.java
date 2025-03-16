package ru.spbstu.rakitin.management.engine.influxdb;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.InfluxDBClientOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class InfluxDbClientFactory {

    private final InfluxDbProperties influxDbProperties;
    private final Map<String, InfluxDBClient> clients = new HashMap<>();

    public InfluxDBClient getInfluxDbClient(String taskName, String organization) {
        return clients.computeIfAbsent(taskName, s -> {
            InfluxDBClientOptions options = InfluxDBClientOptions.builder()
                    .bucket(s)
                    .org(organization)
                    .url(influxDbProperties.getUrl())
                    .authenticate(influxDbProperties.getUsername(), influxDbProperties.getPassword().toCharArray()).build();
            return InfluxDBClientFactory.create(options);

        });
    }

    public void close(String taskName) {
        clients.get(taskName).close();
        clients.remove(taskName);
    }

}
