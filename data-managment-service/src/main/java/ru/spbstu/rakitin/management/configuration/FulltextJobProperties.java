package ru.spbstu.rakitin.management.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "mds.data-management.job.fulltext")
@Data
public class FulltextJobProperties {

    private long fetchTasksTimeoutMs = 30 * 1000;

}
