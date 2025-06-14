package ru.spbstu.rakitin.fulltext_service.engine.configuration;

import lombok.RequiredArgsConstructor;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.Http2SolrClient;
import org.apache.solr.client.solrj.impl.LBHttpSolrClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ru.spbstu.rakitin.fulltext_service.engine.client.DockerHttp2SolrClient;
import ru.spbstu.rakitin.fulltext_service.engine.client.DockerLBHttpClient;

import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class SolrConfiguration {

    private final SolrConfigurationProperties solrConfigurationProperties;

    @Bean
    @Profile("docker")
    public LBHttpSolrClient.Builder lbHttpSolrClientBuilderDocker() {
        return new DockerLBHttpClient.Builder();
    }

    @Bean
    @Profile("!docker")
    public LBHttpSolrClient.Builder lbHttpSolrClientBuilderDefault() {
        return new LBHttpSolrClient.Builder();
    }

    @Bean
    @Profile("!docker")
    public Http2SolrClient http2SolrClient() {
        return new Http2SolrClient.Builder()
                .withBasicAuthCredentials(solrConfigurationProperties.getUsername(), solrConfigurationProperties.getPassword())
                .withRequestTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    @Bean
    @Profile("docker")
    public Http2SolrClient dockerHttp2SolrClient() {
        return new DockerHttp2SolrClient.Builder()
                .withBasicAuthCredentials("solr", "SolrRocks")
                .withRequestTimeout(10, TimeUnit.SECONDS)
                .withIdleTimeout(10, TimeUnit.MINUTES)
                .withConnectionTimeout(10, TimeUnit.SECONDS)
                .build();
    }


    @Bean
    public CloudSolrClient cloudSolrClient(Http2SolrClient http2SolrClient) {
        return new CloudSolrClient.Builder(solrConfigurationProperties.getZookeeperProperties().getZkHosts(), solrConfigurationProperties.getZookeeperProperties().getZkChroot())
                .withZkClientTimeout(solrConfigurationProperties.getZookeeperProperties().getZkClientTimeoutSec(), TimeUnit.SECONDS)
                .withZkConnectTimeout(solrConfigurationProperties.getZookeeperProperties().getZkConnectTimeoutSec(), TimeUnit.SECONDS)
                .withHttpClient(http2SolrClient).build();
    }

}
