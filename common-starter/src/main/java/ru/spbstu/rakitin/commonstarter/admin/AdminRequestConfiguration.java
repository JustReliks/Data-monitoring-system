package ru.spbstu.rakitin.commonstarter.admin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import ru.spbstu.rakitin.commonstarter.admin.exception.ForbiddenRequestException;

import java.util.List;
import java.util.regex.Pattern;

@Configuration
@ConfigurationProperties
public class AdminRequestConfiguration {

    @Value("${dms.administration.service.url:}")
    private String administrationServiceUrl;
    @Value("${dms.administration.token:}")
    private String administrationToken;
    @Value("#{'${dms.administration.whitelist:}'.split(',')}")
    private List<String> whitelist;
    @Value("${dms.administration.token.header:MONITORING-ADMIN-TOKEN}")
    private String adminTokenHeader;

    private List<Pattern> whiteListPatterns;


    @Bean
    public RestTemplate adminRestTemplate() {
        whiteListPatterns = whitelist.stream().map(Pattern::compile).toList();
        return new RestTemplateBuilder()
                .rootUri(administrationServiceUrl)
                .defaultHeader(adminTokenHeader, administrationToken)
                .interceptors((request, body, execution) -> {
                    String path = request.getURI().getPath();
                    boolean matchPattern = whiteListPatterns.stream().anyMatch(pattern -> pattern.matcher(path).matches());
                    if (!matchPattern) {
                        throw new ForbiddenRequestException(String.format("Request %s is not available for this service", path));
                    }
                    return execution.execute(request, body);
                })
                .build();
    }

}
