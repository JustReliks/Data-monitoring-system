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
public class FulltextServiceApplication implements CommandLineRunner {

    private final CloudSolrClient cloudSolrClient;

    public FulltextServiceApplication(CloudSolrClient cloudSolrClient) {
        this.cloudSolrClient = cloudSolrClient;
    }

    public static void main(String[] args) {
        SpringApplication.run(FulltextServiceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Path tempDirectory = Files.createTempDirectory(null);
        Files.write(new File(tempDirectory.toFile(), "managed-schema").toPath(), "managed-schema".getBytes());
        Files.write(new File(tempDirectory.toFile(), "solrconfig.xml").toPath(), "solrconfig".getBytes());

        ConfigSetAdminRequest.Upload upload = new ConfigSetAdminRequest.Upload();
        upload.setConfigSetName("test2121");
        upload.setOverwrite(true);
        ConfigSetAdminResponse process = upload.setUploadFile(zipDirectory(tempDirectory), "application/zip")
                .process(cloudSolrClient);
        System.out.println(process);
    }

    private static File zipDirectory(Path folderPath) throws IOException {
        Path zipPath = Files.createTempFile("configset-", ".zip");

        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            Files.walk(folderPath)
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        ZipEntry zipEntry = new ZipEntry(folderPath.relativize(file).toString());
                        try {
                            zos.putNextEntry(zipEntry);
                            Files.copy(file, zos);
                            zos.closeEntry();
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
        }
        return zipPath.toFile();
    }


}
