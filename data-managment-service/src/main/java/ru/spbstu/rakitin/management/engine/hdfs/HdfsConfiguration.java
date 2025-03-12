package ru.spbstu.rakitin.management.engine.hdfs;

import lombok.RequiredArgsConstructor;
import org.apache.hadoop.fs.FileSystem;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.URI;

@Configuration
@RequiredArgsConstructor
public class HdfsConfiguration {

    private final HdfsConfigurationProperties hdfsConfigurationProperties;

    @Bean
    public org.apache.hadoop.conf.Configuration configuration() {
        org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
        return conf;
    }

    @Bean
    public FileSystem fileSystem() throws IOException, InterruptedException {
        return FileSystem.get(URI.create(hdfsConfigurationProperties.getHostname()), configuration(), hdfsConfigurationProperties.getUser());
    }

}
