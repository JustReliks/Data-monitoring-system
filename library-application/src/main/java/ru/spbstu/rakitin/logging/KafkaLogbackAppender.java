package ru.spbstu.rakitin.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import net.logstash.logback.layout.LogstashLayout;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class KafkaLogbackAppender extends AppenderBase<ILoggingEvent> {

    private KafkaProducer<String, String> producer;
    private String topic;
    private LayoutWrappingEncoder<ILoggingEvent> encoder;

    public void setTopic(String topic) {
        this.topic = topic;
    }

    @Override
    protected void append(ILoggingEvent event) {
        try {
            String json = new String(encoder.encode(event), StandardCharsets.UTF_8);
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, json);
            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    System.err.println("Kafka logging error: " + exception.getMessage());
                }
            });
        } catch (Exception e) {
            System.err.println("Log encoding/sending error: " + e.getMessage());
        }
    }

    @Override
    public void start() {
        // Kafka producer config
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092,localhost:9093,localhost:9091");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.ACKS_CONFIG, "1");
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, "2000");
        props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, "3000");
        props.put(ProducerConfig.RETRIES_CONFIG, "0");
        props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, "2000");

        producer = new KafkaProducer<>(props);

        // Logstash encoder with UTC timestamp formatting
        LogstashLayout layout = new LogstashLayout();
        layout.setContext(getContext());
        layout.setTimeZone("UTC"); // 👈 ключевой момент — формат с "Z"
        layout.start();

        encoder = new LayoutWrappingEncoder<>();
        encoder.setContext(getContext());
        encoder.setLayout(layout);
        encoder.start();

        super.start();
    }

    @Override
    public void stop() {
        if (producer != null) producer.close();
        if (encoder != null) encoder.stop();
        super.stop();
    }
}
