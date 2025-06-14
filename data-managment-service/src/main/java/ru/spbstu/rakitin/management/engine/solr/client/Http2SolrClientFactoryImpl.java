package ru.spbstu.rakitin.management.engine.solr.client;

import org.apache.solr.client.solrj.impl.Http2SolrClient;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.spbstu.rakitin.management.engine.solr.configuration.SolrConfigurationProperties;

import java.util.concurrent.TimeUnit;

@Component
@Profile("!docker")
public class Http2SolrClientFactoryImpl implements Http2SolrClientFactory {

    private final SolrConfigurationProperties solrConfigurationProperties;

    public Http2SolrClientFactoryImpl(SolrConfigurationProperties solrConfigurationProperties) {
        this.solrConfigurationProperties = solrConfigurationProperties;
    }

    @Override
    public Http2SolrClient buildHttp2SolrClient() {
        return new Http2SolrClient.Builder()
                .withBasicAuthCredentials(solrConfigurationProperties.getUsername(), solrConfigurationProperties.getPassword())
                .withRequestTimeout(100, TimeUnit.SECONDS)
                .build();
    }
}
