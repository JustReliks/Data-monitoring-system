package ru.spbstu.rakitin.benchmark;

import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.solr.client.solrj.embedded.SSLConfig;
import ru.org.eclipse.jetty.util.ssl.SslContextFactory;

public class VerifyHostsSslConfig extends SSLConfig {

    private final boolean verifyHosts;

    public VerifyHostsSslConfig(boolean useSsl, boolean clientAuth, String keyStore, String keyStorePassword, String trustStore, String trustStorePassword, boolean verifyHosts) {
        super(useSsl, clientAuth, keyStore, keyStorePassword, trustStore, trustStorePassword);
        this.verifyHosts = verifyHosts;
    }

    @Override
    public SslContextFactory.Client createClientContextFactory() {
        SslContextFactory.Client clientContextFactory = super.createClientContextFactory();
        specifyVerifyHostsToSslContextFactory(clientContextFactory);
        return clientContextFactory;
    }

    @Override
    public SslContextFactory.Server createContextFactory() {
        SslContextFactory.Server contextFactory = super.createContextFactory();
        specifyVerifyHostsToSslContextFactory(contextFactory);
        return contextFactory;
    }

    private void specifyVerifyHostsToSslContextFactory(SslContextFactory sslContextFactory) {
        if (sslContextFactory == null) return;
        if (verifyHosts) {
            sslContextFactory.setHostnameVerifier(NoopHostnameVerifier.INSTANCE);
            sslContextFactory.setEndpointIdentificationAlgorithm(null);
        } else {
            sslContextFactory.setHostnameVerifier(new DefaultHostnameVerifier());
        }
    }
}
