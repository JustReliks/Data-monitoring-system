package ru.spbstu.rakitin.commonstarter.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("mds.inner.protocol")
@Data
public class InnerRequestConfiguration {

    public enum Protocol {
        HTTP, HTTPS
    }

    private Protocol protocol = Protocol.HTTP;

}
