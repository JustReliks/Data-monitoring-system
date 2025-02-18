package ru.spbstu.rakitin.fulltext_service.migrations;

import lombok.RequiredArgsConstructor;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.common.cloud.ZkStateReader;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.nio.file.Path;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class R__add_security_file_to_solr extends BaseJavaMigration {

    private final CloudSolrClient cloudSolrClient;

    @Override
    public void migrate(Context context) throws Exception {
        ZkStateReader zkStateReader = ZkStateReader.from(cloudSolrClient);
        URI securityJsonUri = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("solr/config/security.json")).toURI();
        zkStateReader.getZkClient().setData("/security.json",
                Path.of(securityJsonUri), true);
    }

    @Override
    public Integer getChecksum() {
        return (int) (Math.random() * Integer.MAX_VALUE);
    }
}
