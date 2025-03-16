package ru.spbstu.rakitin.monitoring_service;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

class MonitoringServiceApplicationTests {

    public static final String URL = "http://localhost:8886";
    public static final char[] CHAR_ARRAY = "dF3vpj-U1e-jz728GN70HiZgJAD3JYpi9PF1McbSGQzC5zlVpnyMVSHBJcqtxGkp1skjJGCAcINMjfbgv6lbTw==".toCharArray();
    private static char[] token = "MyInitialAdminToken0==".toCharArray();
    private static String org = "my-org";
    private static String bucket = "my-bucket";


    @Test
    void contextLoads() {
    }


    @Test
    public void testInfluxDB() {
        try (InfluxDBClient influxDBClient = InfluxDBClientFactory.create(URL, token);
        ) {
            WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
            List<String> locations = Stream.of("north", "west", "south", "east").toList();
            while (true) {
                locations.forEach(location -> {
                    Point point = Point.measurement("temperature")
                            .addTag("location", location)
                            .addField("value", Math.random() * 60)
                            .time(Instant.now().toEpochMilli(), WritePrecision.MS);
                    writeApi.writePoint(bucket, "acf65326747d121a", point);

                    System.out.println(point.toLineProtocol());
                });
                Thread.sleep(500);


            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Measurement(name = "temperature")
    private static class Temperature {

        @Column(tag = true)
        String location;

        @Column
        Double value;

        @Column(timestamp = true)
        Instant time;
    }

}
