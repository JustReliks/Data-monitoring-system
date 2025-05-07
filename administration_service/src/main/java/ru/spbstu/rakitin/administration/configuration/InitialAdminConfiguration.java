package ru.spbstu.rakitin.administration.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "dms.administration.initial")
@Data
public class InitialAdminConfiguration {

    private String username = "initial_admin";

}
