package ru.spbstu.rakitin.commonstarter.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("mds.inner")
@Data
public class InnerRequestConfiguration {

    public static enum Protocol {
        HTTP, HTTPS
    }

    private Protocol protocol = Protocol.HTTP;
    private boolean useHttp2 = false;

}
