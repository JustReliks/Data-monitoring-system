package ru.spbstu.rakitin.management.engine.hdfs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "dms.archive.hdfs")
@Configuration
@Data
public class HdfsConfigurationProperties {

    private String hostname;
    private String user;
    private String basePath;

}
