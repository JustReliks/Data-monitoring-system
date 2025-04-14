package ru.spbstu.rakitin.management.configuration;

import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@ConfigurationProperties("dms.kafka")
public class KafkaConfiguration {

    @Value("#{'${dms.kafka.bootstrap.servers:}'.split(',')}")
    private List<String> bootstrapServers;

    @Bean("kafkaProperties")
    @Scope("prototype")
    public Map<String, Object> kafkaProperties() {
        Map<String, Object> properties = getPublicProperties();
        properties.put("auto.offset.reset", "latest");
        properties.put("enable.auto.commit", true);
        properties.put("application.id", "DataManagementApplication");

        return properties;
    }

    public Map<String, Object> getPublicProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put("key.deserializer", StringDeserializer.class);
        properties.put("value.deserializer", StringDeserializer.class);
        return properties;
    }

    @Bean
    public Admin admin() {
        return Admin.create(kafkaProperties());
    }
}
