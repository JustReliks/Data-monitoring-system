package ru.spbstu.rakitin.management.engine.solr.client;

import org.apache.solr.client.solrj.impl.Http2SolrClient;

public interface Http2SolrClientFactory {

    Http2SolrClient buildHttp2SolrClient();

}
