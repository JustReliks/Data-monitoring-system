package ru.spbstu.rakitin.management.engine.solr.client;

import org.apache.solr.client.solrj.impl.Http2SolrClient;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Profile("docker")
public class DockerHttp2SolrClientFactory implements Http2SolrClientFactory {
    @Override
    public Http2SolrClient buildHttp2SolrClient() {
        return new DockerHttp2SolrClient.Builder()
                .withBasicAuthCredentials("solr", "SolrRocks")
                .withRequestTimeout(100, TimeUnit.SECONDS)
                .withIdleTimeout(10 * 365, TimeUnit.DAYS)
                .withConnectionTimeout(100, TimeUnit.SECONDS)
                .build();
    }
}
