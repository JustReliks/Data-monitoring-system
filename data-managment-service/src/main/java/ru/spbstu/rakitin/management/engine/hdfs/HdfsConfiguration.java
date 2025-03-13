package ru.spbstu.rakitin.management.engine.hdfs;

import lombok.RequiredArgsConstructor;
import org.apache.hadoop.fs.FileSystem;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.net.URI;

@Configuration
@RequiredArgsConstructor
public class HdfsConfiguration {

    private final HdfsConfigurationProperties hdfsConfigurationProperties;

    @Bean
    @Profile("!docker")
    public org.apache.hadoop.conf.Configuration configuration() {
        org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
        return conf;
    }

    @Bean
    @Profile("docker")
    public org.apache.hadoop.conf.Configuration dockerConfiguration() {
        org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
        conf.set("dfs.client.use.datanode.hostname", "true");

        return conf;
    }

    @Bean
    public FileSystem fileSystem(org.apache.hadoop.conf.Configuration configuration) throws IOException, InterruptedException {
        System.setProperty("hadoop.home.dir", "C:/hadoop");
        return FileSystem.get(URI.create(hdfsConfigurationProperties.getHostname()), configuration, hdfsConfigurationProperties.getUser());
    }

}
