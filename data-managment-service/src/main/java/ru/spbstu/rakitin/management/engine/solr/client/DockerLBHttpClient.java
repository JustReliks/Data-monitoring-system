package ru.spbstu.rakitin.management.engine.solr.client;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.LBHttpSolrClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;

public class DockerLBHttpClient extends LBHttpSolrClient {

    protected DockerLBHttpClient(Builder builder) {
        super(builder);
    }

    @Override
    public Rsp request(Req req) throws SolrServerException, IOException {
        List<String> servers = req.getServers().stream().map(server -> {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(server);
            builder.host("localhost");
            return builder.build().toUriString();
        }).toList();

        Req newReq = new Req(req.getRequest(), servers);
        newReq.setNumDeadServersToTry(req.getNumDeadServersToTry());
        return super.request(newReq);
    }

    public static class Builder extends LBHttpSolrClient.Builder {

        @Override
        public LBHttpSolrClient build() {
            return new DockerLBHttpClient(this);
        }
    }
}
