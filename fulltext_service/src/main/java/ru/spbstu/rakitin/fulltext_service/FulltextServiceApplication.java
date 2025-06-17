package ru.spbstu.rakitin.fulltext_service;

import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.request.ConfigSetAdminRequest;
import org.apache.solr.client.solrj.response.ConfigSetAdminResponse;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import ru.spbstu.rakitin.commonentites.CommonEntitiesAutoConfiguration;
import ru.spbstu.rakitin.commonstarter.DataManagementCommonAutoConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Import({DataManagementCommonAutoConfiguration.class, CommonEntitiesAutoConfiguration.class})
@SpringBootApplication(scanBasePackages = "ru.spbstu.rakitin")
public class FulltextServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FulltextServiceApplication.class, args);
    }

}
