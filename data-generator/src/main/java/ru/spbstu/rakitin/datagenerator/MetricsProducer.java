package ru.spbstu.rakitin.datagenerator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.Properties;
import java.util.Set;

public class MetricsProducer {
    private static final String TOPIC = "testProj.metricsTaskTopic";
    private static final String BOOTSTRAP_SERVERS = "localhost:9091,localhost:9092,localhost:9093";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(props)) {
            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            while (true) {
                Set<ObjectName> mBeans = mBeanServer.queryNames(null, null);
                for (ObjectName name : mBeans) {
                    for (MBeanAttributeInfo attr : mBeanServer.getMBeanInfo(name).getAttributes()) {
                        if (attr.isReadable()) {
                            try {
                                Object value = mBeanServer.getAttribute(name, attr.getName());
                                if (value instanceof Number) {
                                    Metric metric = new Metric(name.toString() + "." + attr.getName(), ((Number) value).doubleValue());
                                    String jsonMetric = OBJECT_MAPPER.writeValueAsString(metric);
                                    ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, jsonMetric);

                                    producer.send(record, (metadata, exception) -> {
                                        if (exception != null) {
                                            System.err.println("Error sending message: " + exception.getMessage());
                                        } else {
                                            System.out.println("Sent metric to " + metadata.topic() + " partition " + metadata.partition());
                                        }
                                    });
                                }
                            } catch (Exception ignored) {
                            }
                        }
                    }
                }
                Thread.sleep(5000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class Metric {
        public String metricName;
        public double value;

        public Metric(String metricName, double value) {
            this.metricName = metricName;
            this.value = value;
        }
    }
}

