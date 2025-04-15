package ru.spbstu.rakitin.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.spbstu.rakitin.client.*;

@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = "library.mds")
public class MdsConfiguration {

    private String username;
    private String password;
    private String url;
    private long retryDelayMs = 1000;
    private long retryCount = 5;
    private int threadsCount = 10;
    private long apiKeyAliveMs = 60000;


    @Bean
    public AuthProperties authProperties() {
        return new AuthProperties(username, password);
    }

    @Bean
    public RequestProperties requestProperties() {
        return new RequestProperties(retryDelayMs, retryCount, threadsCount, url);
    }

    @Bean
    public ApiKeyStorageProperties apiKeyStorageProperties() {
        return new ApiKeyStorageProperties(apiKeyAliveMs);
    }

    @Bean
    public MdsClient mdsClient(AuthProperties authProperties, RequestProperties requestProperties, ApiKeyStorageProperties apiKeyStorageProperties) {
        return new MdsClientImpl(authProperties, requestProperties, apiKeyStorageProperties);
    }


}
