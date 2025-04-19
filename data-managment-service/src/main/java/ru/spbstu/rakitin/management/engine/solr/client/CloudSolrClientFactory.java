package ru.spbstu.rakitin.management.engine.solr.client;

import lombok.extern.slf4j.Slf4j;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.springframework.stereotype.Component;
import ru.spbstu.rakitin.management.engine.solr.configuration.SolrConfigurationProperties;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class CloudSolrClientFactory {

    private final SolrConfigurationProperties solrConfigurationProperties;

    private final Http2SolrClientFactory http2SolrClientFactory;
    private CloudSolrClient cloudSolrClient;
    private final Object lock = new Object();

    public CloudSolrClientFactory(SolrConfigurationProperties solrConfigurationProperties, Http2SolrClientFactory http2SolrClientFactory) {
        this.solrConfigurationProperties = solrConfigurationProperties;
        this.http2SolrClientFactory = http2SolrClientFactory;
    }

    public CloudSolrClient buildCloudSolrClient() {
        synchronized (lock) {
            if (cloudSolrClient == null) {
                log.info("Creating CloudSolrClient");
                cloudSolrClient = new CloudSolrClient.Builder(solrConfigurationProperties.getZookeeperProperties().getZkHosts(), solrConfigurationProperties.getZookeeperProperties().getZkChroot())
                        .withZkClientTimeout(solrConfigurationProperties.getZookeeperProperties().getZkClientTimeoutSec(), TimeUnit.SECONDS)
                        .withZkConnectTimeout(solrConfigurationProperties.getZookeeperProperties().getZkConnectTimeoutSec(), TimeUnit.SECONDS)
                        .withHttpClient(http2SolrClientFactory.buildHttp2SolrClient()).build();
            } else {
                try {
                    cloudSolrClient.connect();
                } catch (Exception e) {
                    cloudSolrClient = null;
                    return buildCloudSolrClient();
                }
            }
        }
        return cloudSolrClient;
    }

}
