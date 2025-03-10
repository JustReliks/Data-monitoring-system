package ru.spbstu.rakitin.archive_service.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "mds.archive.hdfs")
@Configuration
@Data
public class HdfsProperties {

    private String hostname;
    private String user;

}
