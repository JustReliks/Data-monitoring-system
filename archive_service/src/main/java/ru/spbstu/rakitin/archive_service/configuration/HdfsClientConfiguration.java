package ru.spbstu.rakitin.archive_service.configuration;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hdfs.client.HdfsAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class HdfsClientConfiguration {


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
    public HdfsAdmin hdfsAdmin(HdfsProperties hdfsProperties, org.apache.hadoop.conf.Configuration configuration) throws IOException, URISyntaxException {
        return new HdfsAdmin(new URI(hdfsProperties.getHostname()), configuration);
    }

    @Bean
    public FileSystem fileSystem(HdfsProperties hdfsProperties, org.apache.hadoop.conf.Configuration configuration) throws IOException, URISyntaxException, InterruptedException {
        System.setProperty("hadoop.home.dir", "C:/hadoop");
        return FileSystem.get(new URI(hdfsProperties.getHostname()), configuration, hdfsProperties.getUser());
    }


}
