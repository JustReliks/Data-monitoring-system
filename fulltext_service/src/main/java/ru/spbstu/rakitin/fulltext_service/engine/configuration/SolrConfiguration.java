package ru.spbstu.rakitin.fulltext_service.engine.configuration;

import lombok.RequiredArgsConstructor;
import org.apache.solr.client.solrj.impl.CloudLegacySolrClient;
import org.apache.solr.client.solrj.impl.LBHttpSolrClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
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
    public CloudLegacySolrClient cloudSolrClient(LBHttpSolrClient.Builder lbBuilder) {
        return new CloudLegacySolrClient.Builder(solrConfigurationProperties.getZookeeperProperties().getZkHosts(), solrConfigurationProperties.getZookeeperProperties().getZkChroot())
                .withLBHttpSolrClient(lbBuilder.build())
                .withZkClientTimeout(solrConfigurationProperties.getZookeeperProperties().getZkClientTimeoutSec(), TimeUnit.SECONDS)
                .withZkConnectTimeout(solrConfigurationProperties.getZookeeperProperties().getZkConnectTimeoutSec(), TimeUnit.SECONDS).build();
    }

}
