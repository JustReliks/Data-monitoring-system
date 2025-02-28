package ru.spbstu.rakitin.fulltext_service.engine.client;

import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.Http2SolrClient;
import org.apache.solr.common.util.NamedList;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

public class DockerHttp2SolrClient extends Http2SolrClient {

    protected DockerHttp2SolrClient(String serverBaseUrl, Builder builder) {
        super(serverBaseUrl, builder);
    }

    @Override
    public NamedList<Object> request(SolrRequest<?> solrRequest, String collection) throws SolrServerException, IOException {
        if (solrRequest.getBasePath() != null) {
            solrRequest.setBasePath(changeServerHost(solrRequest.getBasePath()));
        }
        return super.request(solrRequest, collection);
    }

    public static class Builder extends Http2SolrClient.Builder {
        @Override
        public Http2SolrClient build() {
            if (baseSolrUrl != null) {
                baseSolrUrl = changeServerHost(baseSolrUrl);
            }

            return new DockerHttp2SolrClient(baseSolrUrl, this);
        }
    }

    private static String changeServerHost(String server) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(server);
        builder.host("localhost");
        return builder.toUriString();
    }
}
