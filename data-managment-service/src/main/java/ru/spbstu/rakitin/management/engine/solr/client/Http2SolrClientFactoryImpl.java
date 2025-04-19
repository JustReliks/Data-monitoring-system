package ru.spbstu.rakitin.management.engine.solr.client;

import org.apache.solr.client.solrj.impl.Http2SolrClient;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Profile("!docker")
public class Http2SolrClientFactoryImpl implements Http2SolrClientFactory {
    @Override
    public Http2SolrClient buildHttp2SolrClient() {
        return new Http2SolrClient.Builder()
                .withBasicAuthCredentials("solr", "SolrRocks")
                .withRequestTimeout(100, TimeUnit.SECONDS)
                .build();
    }
}
