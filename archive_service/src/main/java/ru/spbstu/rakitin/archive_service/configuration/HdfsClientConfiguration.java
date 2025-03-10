package ru.spbstu.rakitin.archive_service.configuration;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hdfs.client.HdfsAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class HdfsClientConfiguration {

    @Bean
    public org.apache.hadoop.conf.Configuration hadoopConf() {
        org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();

        return conf;
    }


    @Bean
    public HdfsAdmin hdfsAdmin(HdfsProperties hdfsProperties) throws IOException, URISyntaxException {
        return new HdfsAdmin(new URI(hdfsProperties.getHostname()), hadoopConf());
    }

    @Bean
    public FileSystem fileSystem(HdfsProperties hdfsProperties) throws IOException, URISyntaxException, InterruptedException {
        return FileSystem.get(new URI(hdfsProperties.getHostname()), hadoopConf(), hdfsProperties.getUser());
    }


}
