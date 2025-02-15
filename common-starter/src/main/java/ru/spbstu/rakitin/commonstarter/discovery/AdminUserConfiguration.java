package ru.spbstu.rakitin.commonstarter.discovery;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties("dms.user.admin")
public class AdminUserConfiguration {

    private String username;
    private String password;


}
