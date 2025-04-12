package ru.spbstu.rakitin.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "library.project")
@Data
public class ProjectConfiguration {

    private long id;

}
